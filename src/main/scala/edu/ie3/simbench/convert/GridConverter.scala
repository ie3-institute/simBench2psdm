package edu.ie3.simbench.convert

import com.typesafe.scalalogging.LazyLogging
import edu.ie3.datamodel.io.source.TimeSeriesMappingSource
import edu.ie3.datamodel.io.source.TimeSeriesMappingSource.MappingEntry
import edu.ie3.datamodel.models.input.connector.{
  LineInput,
  Transformer2WInput,
  Transformer3WInput
}
import edu.ie3.datamodel.models.input.container.{
  GraphicElements,
  JointGridContainer,
  RawGridElements,
  SystemParticipants
}
import edu.ie3.datamodel.models.input.graphics.{
  LineGraphicInput,
  NodeGraphicInput
}
import edu.ie3.datamodel.models.input.system._
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.result.NodeResult
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries
import edu.ie3.datamodel.models.value.{PValue, SValue, Value}
import edu.ie3.simbench.convert.NodeConverter.AttributeOverride.{
  JoinOverride,
  SubnetOverride
}
import edu.ie3.simbench.convert.types.{
  LineTypeConverter,
  Transformer2wTypeConverter
}
import edu.ie3.simbench.exception.ConversionException
import edu.ie3.simbench.model.datamodel.{
  GridModel,
  Line,
  Node,
  NodePFResult,
  Switch,
  Transformer2W,
  Transformer3W
}

import java.util.UUID
import scala.annotation.tailrec
import scala.collection.immutable
import scala.jdk.CollectionConverters._

case object GridConverter extends LazyLogging {

  /**
    * Converts a full simbench grid into power system data models [[JointGridContainer]]. Additionally, individual time
    * series for all system participants are delivered as well.
    *
    * @param simbenchCode Simbench code, that is used as identifier for the grid
    * @param gridInput    Total grid input model to be converted
    * @return A converted [[JointGridContainer]], a [[Vector]] of [[IndividualTimeSeries]] as well as a [[Vector]] of [[NodeResult]]s
    */
  def convert(
      simbenchCode: String,
      gridInput: GridModel
  ): (
      JointGridContainer,
      Vector[IndividualTimeSeries[_ <: PValue]],
      Seq[MappingEntry],
      Vector[NodeResult]
  ) = {
    logger.debug(s"Converting raw grid elements of '${gridInput.simbenchCode}'")
    val (rawGridElements, nodeConversion) = convertGridElements(gridInput)

    logger.debug(
      s"Converting system participants and their time series of '${gridInput.simbenchCode}'"
    )
    val (systemParticipants, timeSeries, timeSeriesMapping) =
      convertParticipants(gridInput, nodeConversion)

    logger.debug(
      s"Converting power flow results of '${gridInput.simbenchCode}'"
    )
    val powerFlowResults =
      convertNodeResults(gridInput.nodePFResults, nodeConversion)

    (
      new JointGridContainer(
        simbenchCode,
        rawGridElements,
        systemParticipants,
        new GraphicElements(
          Set.empty[NodeGraphicInput].asJava,
          Set.empty[LineGraphicInput].asJava
        )
      ),
      timeSeries,
      timeSeriesMapping,
      powerFlowResults
    )
  }

  /**
    * Converts all elements that do form the grid itself.
    *
    * @param gridInput Total grid input model to convert
    * @return All grid elements in converted form + a mapping from old to new node models
    */
  def convertGridElements(
      gridInput: GridModel
  ): (RawGridElements, Map[Node, NodeInput]) = {
    /* Set up a sub net converter, by crawling all nodes */
    val subnetConverter = SubnetConverter(
      gridInput.nodes.map(node => (node.vmR, node.subnet))
    )

    val slackNodeKeys = NodeConverter.getSlackNodeKeys(
      gridInput.externalNets,
      gridInput.powerPlants,
      gridInput.res
    )

    /* Collect overriding attributes for node conversion, based on special constellations within the grid */
    val subnetOverrides = determineSubnetOverrides(
      gridInput.transformers2w,
      gridInput.transformers3w,
      gridInput.switches,
      gridInput.lines,
      subnetConverter
    )
    val joinOverrides =
      determineJoinOverrides(gridInput.switches, slackNodeKeys)

    val nodeConversion =
      convertNodes(
        gridInput.nodes,
        slackNodeKeys,
        subnetConverter,
        subnetOverrides,
        joinOverrides
      )

    val lines = convertLines(gridInput, nodeConversion).toSet.asJava
    val transformers2w =
      convertTransformers2w(gridInput, nodeConversion).toSet.asJava
    val transformers3w = Set.empty[Transformer3WInput].asJava /* Currently, no conversion strategy is known */
    logger.debug(
      "Creation of three winding transformers is not yet implemented."
    )
    val switches =
      SwitchConverter.convert(gridInput.switches, nodeConversion).toSet.asJava
    val measurements = MeasurementConverter
      .convert(gridInput.measurements, nodeConversion)
      .toSet
      .asJava

    // TODO: Remove all nodes, that are not connected to any branch element

    (
      new RawGridElements(
        nodeConversion.values.toSet.asJava,
        lines,
        transformers2w,
        transformers3w,
        switches,
        measurements
      ),
      nodeConversion
    )
  }

  /**
    * Determine all relevant subnet override information. SimBench has a different notion of where the border of a
    * subnet is, therefore, for all nodes that are upstream of a transformer's hv node and connected via switches,
    * explicit subnet numbers are provided.
    *
    * @param transformers2w   Collection of two winding transformers
    * @param transformers3w   Collection of three winding transformers
    * @param switches         Collection of switches
    * @param lines            Collection of lines
    * @param subnetConverter  Converter to determine subnet numbers
    * @return A collection of [[SubnetOverride]]s
    */
  private def determineSubnetOverrides(
      transformers2w: Vector[Transformer2W],
      transformers3w: Vector[Transformer3W],
      switches: Vector[Switch],
      lines: Vector[Line[_]],
      subnetConverter: SubnetConverter
  ): Vector[SubnetOverride] = {
    /* All nodes, at which a branch element is connected */
    val junctions = (lines.flatMap(
      line => Vector(line.nodeA, line.nodeB)
    ) ++ transformers2w
      .flatMap(
        transformer => Vector(transformer.nodeHV, transformer.nodeLV)
      ) ++ transformers3w.flatMap(
      transformer =>
        Vector(transformer.nodeHV, transformer.nodeLV, transformer.nodeMV)
    )).distinct

    transformers2w.flatMap { transformer =>
      val relevantSubnet = subnetConverter.convert(
        transformer.nodeLV.vmR,
        transformer.nodeLV.subnet
      )
      val startNode = transformer.nodeHV
      determineSubnetOverrides(
        startNode,
        switches,
        junctions.filterNot(_ == startNode),
        relevantSubnet
      )
    } ++ transformers3w.flatMap { transformer =>
      val relevantSubnet = subnetConverter.convert(
        transformer.nodeHV.vmR,
        transformer.nodeHV.subnet
      )
      val startNode = transformer.nodeHV
      determineSubnetOverrides(
        startNode,
        switches,
        junctions.filterNot(_ == startNode),
        relevantSubnet
      )
    }
  }

  /**
    * Traveling along a switch chain starting from a starting node and stopping at dead ends and those nodes, that are
    * marked explicitly as junctions. During this travel, every node we come along gets an [[SubnetOverride]] instance
    * assigned, marking, that later in node conversion, this explicit subnet number shall be used. The subnet at
    * junctions and dead ends is not altered. Adding the traveled nodes to the list of junctions, prevents from running
    * in circles forever. Pay attention, that when starting from the hv node of a transformer, it may not be included
    * in the set of junction nodes, if it is not part of any other junction.
    *
    * @param startNode      Node, where the traversal begins
    * @param switches       Collection of all switches
    * @param junctions      Collection of nodes, that are junctions
    * @param relevantSubnet The explicit subnet number to use later
    * @param overrides      Current collection of overrides (for recursive usage)
    * @return A collection of [[SubnetOverride]]s for this traversal
    */
  private def determineSubnetOverrides(
      startNode: Node,
      switches: Vector[Switch],
      junctions: Vector[Node],
      relevantSubnet: Int,
      overrides: Vector[SubnetOverride] = Vector.empty
  ): Vector[SubnetOverride] = {
    /* If the start node is among the junctions, do not travel further (Attention: Start node should not be among
     * junctions when the traversing starts, otherwise nothing will happen at all.) */
    if (junctions.contains(startNode))
      return overrides

    /* Get all switches, that are connected to the current starting point. If the other end of the switch is a junction,
     * don't follow this path, as the other side wouldn't be touched anyways. */
    val nextSwitches = switches.filter {
      case Switch(_, nodeA, nodeB, _, _, _, _, _) if nodeA == startNode =>
        !junctions.contains(nodeB)
      case Switch(_, nodeA, nodeB, _, _, _, _, _) if nodeB == startNode =>
        !junctions.contains(nodeA)
      case _ => false
    }

    if (nextSwitches.isEmpty) {
      /* There is no further switch, therefore the end is reached -> return the new mapping. Please note, as the subnet
       * of the current node is only altered, if there is a next switch available, dead end nodes are not altered. */
      overrides
    } else {
      /* Copy new node and add it to the mapping */
      val subnetOverride = SubnetOverride(startNode.getKey, relevantSubnet)
      val updatedOverrides = overrides :+ subnetOverride
      val updatedJunctions = junctions.appended(startNode)

      /* For all possible next upcoming switches -> Traverse along each branch (depth first search) */
      nextSwitches.foldLeft(updatedOverrides) {
        case (currentOverride, switch) =>
          /* Determine the next node */
          val nextNode =
            Vector(switch.nodeA, switch.nodeB).find(_ != startNode) match {
              case Some(value) => value
              case None =>
                throw ConversionException(
                  s"Cannot traverse along '$switch', as the next node cannot be determined."
                )
            }

          return determineSubnetOverrides(
            nextNode,
            switches,
            updatedJunctions,
            relevantSubnet,
            currentOverride
          )
      }
    }
  }

  /**
    * Determine join overrides for all nodes, that are connected by closed switches
    *
    * @param switches       Collection of all (closed) switches
    * @param slackNodeKeys  Collection of node keys, that are foreseen to be slack nodes
    * @return A collection of [[JoinOverride]]s
    */
  private def determineJoinOverrides(
      switches: Vector[Switch],
      slackNodeKeys: Vector[Node.NodeKey]
  ): Vector[JoinOverride] = {
    val closedSwitches = switches.filter(_.cond)
    val switchGroups = determineSwitchGroups(closedSwitches)
    switchGroups
      .flatMap(
        switchGroup =>
          determineJoinOverridesForSwitchGroup(switchGroup, slackNodeKeys)
      )
      .distinct
  }

  /**
    * Determine groups of directly connected switches
    *
    * @param switches     Collection of switches to group
    * @param switchGroups Current collection of switch groups (for recursion)
    * @return A collection of switch collections, that denote groups
    */
  @tailrec
  private def determineSwitchGroups(
      switches: Vector[Switch],
      switchGroups: Vector[Vector[Switch]] = Vector.empty
  ): Vector[Vector[Switch]] = switches.headOption match {
    case Some(currentSwitch) =>
      val currentNodes = Vector(currentSwitch.nodeA, currentSwitch.nodeB)
      val (group, remainingSwitches) = switches.partition { switch =>
        currentNodes.contains(switch.nodeA) || currentNodes.contains(
          switch.nodeB
        )
      }
      val updatedGroups = switchGroups :+ group
      determineSwitchGroups(remainingSwitches, updatedGroups)
    case None =>
      switchGroups
  }

  /**
    * Determine overrides per switch group
    *
    * @param switchGroup    A group of directly connected switches
    * @param slackNodeKeys  Collection of node keys, that are foreseen to be slack nodes
    * @return A collection of [[JoinOverride]]s
    */
  private def determineJoinOverridesForSwitchGroup(
      switchGroup: Vector[Switch],
      slackNodeKeys: Vector[Node.NodeKey]
  ): Vector[JoinOverride] = {
    val nodes =
      switchGroup.flatMap(switch => Vector(switch.nodeA, switch.nodeB)).distinct
    val leadingNode =
      nodes.find(node => slackNodeKeys.contains(node.getKey)).getOrElse {
        /* Get the node with the most occurrences */
        nodes.groupBy(identity).view.mapValues(_.size).toMap.maxBy(_._2)._1
      }
    val leadingNodeKey = leadingNode.getKey
    nodes
      .filterNot(_ == leadingNode)
      .map(node => JoinOverride(node.getKey, leadingNodeKey))
  }

  /**
    * Converts all apparent nodes to the equivalent power system data model. The slack information is derived from the
    * attributes of external nets, power plants and renewable energy sources.
    *
    * @param nodes           All nodes to convert
    * @param slackNodeKeys   Node identifier for those, that are foreseen to be slack nodes
    * @param subnetConverter Converter holding the mapping information from simbench to power system data model sub grid
    * @param subnetOverrides Collection of explicit subnet assignments
    * @param joinOverrides   Collection of pairs of nodes, that are meant to be joined
    * @return A map from simbench to power system data model nodes
    */
  private def convertNodes(
      nodes: Vector[Node],
      slackNodeKeys: Vector[Node.NodeKey],
      subnetConverter: SubnetConverter,
      subnetOverrides: Vector[SubnetOverride],
      joinOverrides: Vector[JoinOverride]
  ): Map[Node, NodeInput] = {
    val nodeToExplicitSubnet = subnetOverrides.map {
      case SubnetOverride(key, subnet) => key -> subnet
    }.toMap

    /* First convert all nodes, that are target of node joins */
    val nodeToJoinMap = joinOverrides.map {
      case JoinOverride(key, joinWith) => key -> joinWith
    }.toMap
    val targetNodeKeys = nodeToJoinMap.values.toSeq.distinct
    val (targetNodes, remainingNodes) =
      nodes.partition(node => targetNodeKeys.contains(node.getKey))
    val targetConversion = targetNodes
      .map(
        node =>
          node -> NodeConverter.convert(
            node,
            slackNodeKeys,
            subnetConverter,
            nodeToExplicitSubnet.get(node.getKey)
          )
      )
      .toMap

    /* Then map all nodes to be joined to the converted target nodes */
    val (nodesToBeJoined, singleNodes) = remainingNodes.partition(
      node => nodeToJoinMap.keySet.contains(node.getKey)
    )
    val conversionWithJoinedNodes = targetConversion ++ nodesToBeJoined.map {
      node =>
        node -> targetConversion.getOrElse(
          node,
          throw ConversionException(
            s"The node with key '${node.getKey}' was meant to be joined with another node, but that converted target node is not apparent."
          )
        )
    }.toMap

    /* Finally convert all left over nodes */
    conversionWithJoinedNodes ++ singleNodes
      .map(
        node =>
          node -> NodeConverter.convert(
            node,
            slackNodeKeys,
            subnetConverter,
            nodeToExplicitSubnet.get(node.getKey)
          )
      )
      .toMap
  }

  /**
    * Convert the given [[NodePFResult]]s with the help of yet known conversion mapping of nodes
    *
    * @param input          Vector of [[NodePFResult]] to convert
    * @param nodeConversion Mapping from SimBench to psdm node model
    * @return A [[Vector]] of converted [[NodeResult]]
    */
  private def convertNodeResults(
      input: Vector[NodePFResult],
      nodeConversion: Map[Node, NodeInput]
  ): Vector[NodeResult] = input.map { nodePfResult =>
    val node = nodeConversion.getOrElse(
      nodePfResult.node,
      throw ConversionException(
        s"Cannot convert power flow result for node ${nodePfResult.node}, as the needed node conversion cannot be found."
      )
    )
    NodePFResultConverter.convert(nodePfResult, node)
  }

  /**
    * Converts the given lines.
    *
    * @param gridInput      Total grid input model to convert
    * @param nodeConversion Already known conversion mapping of nodes
    * @return A vector of converted line models
    */
  private def convertLines(
      gridInput: GridModel,
      nodeConversion: Map[Node, NodeInput]
  ): Vector[LineInput] = {
    val lineTypes = LineTypeConverter.convert(gridInput.lines)
    LineConverter.convert(gridInput.lines, lineTypes, nodeConversion)
  }

  /**
    * Converts the given two winding transformers.
    *
    * @param gridInput      Total grid input model to convert
    * @param nodeConversion Already known conversion mapping of nodes
    * @return A vector of converted two winding transformer models
    */
  private def convertTransformers2w(
      gridInput: GridModel,
      nodeConversion: Map[Node, NodeInput]
  ): Vector[Transformer2WInput] = {
    val transformerTypes = Transformer2wTypeConverter.convert(
      gridInput.transformers2w.map(_.transformerType)
    )
    Transformer2wConverter.convert(
      gridInput.transformers2w,
      transformerTypes,
      nodeConversion
    )
  }

  /**
    * Converts all system participants and extracts their individual power time series
    *
    * @param gridInput      Total grid input model to convert
    * @param nodeConversion Already known conversion mapping of nodes
    * @return A collection of converted system participants and their individual time series
    */
  def convertParticipants(
      gridInput: GridModel,
      nodeConversion: Map[Node, NodeInput]
  ): (
      SystemParticipants,
      Vector[IndividualTimeSeries[_ <: PValue]],
      Seq[MappingEntry]
  ) = {
    /* Convert all participant groups */
    logger.debug(
      s"Participants to convert:\n\tLoads: ${gridInput.loads.size}" +
        s"\n\tPower Plants: ${gridInput.powerPlants.size}\n\tRES: ${gridInput.res.size}"
    )
    val loadsToTimeSeries = convertLoads(gridInput, nodeConversion)
    logger.debug(
      s"Done converting ${gridInput.loads.size} loads including time series"
    )
    val powerPlantsToTimeSeries = convertPowerPlants(gridInput, nodeConversion)
    logger.debug(
      s"Done converting ${gridInput.powerPlants.size} power plants including time series"
    )
    val resToTimeSeries = convertRes(gridInput, nodeConversion)
    logger.debug(
      s"Done converting ${gridInput.res.size} RES including time series"
    )

    /* Map participant uuid onto time series */
    val participantsToTimeSeries = loadsToTimeSeries ++ powerPlantsToTimeSeries ++ resToTimeSeries
    val mapping = participantsToTimeSeries.map {
      case (model, timeSeries) =>
        new TimeSeriesMappingSource.MappingEntry(
          UUID.randomUUID(),
          model.getUuid,
          timeSeries.getUuid
        )
    }.toSeq
    val timeSeries: Vector[IndividualTimeSeries[_ >: SValue <: PValue]] =
      participantsToTimeSeries.map(_._2).toVector

    (
      new SystemParticipants(
        Set.empty[BmInput].asJava,
        Set.empty[ChpInput].asJava,
        Set.empty[EvcsInput].asJava,
        Set.empty[EvInput].asJava,
        (powerPlantsToTimeSeries.keySet ++ resToTimeSeries.keySet).asJava,
        Set.empty[HpInput].asJava,
        loadsToTimeSeries.keySet.asJava,
        Set.empty[PvInput].asJava,
        Set.empty[StorageInput].asJava,
        Set.empty[WecInput].asJava
      ),
      timeSeries,
      mapping
    )
  }

  /**
    * Converting all loads.
    *
    * @param gridInput      Total grid input model to convert
    * @param nodeConversion Already known conversion mapping of nodes
    * @return A mapping from loads to their assigned, specific time series
    */
  def convertLoads(
      gridInput: GridModel,
      nodeConversion: Map[Node, NodeInput]
  ): Map[LoadInput, IndividualTimeSeries[SValue]] = {
    val loadProfiles = gridInput.loadProfiles
      .map(profile => profile.profileType -> profile)
      .toMap
    LoadConverter.convert(gridInput.loads, nodeConversion, loadProfiles)
  }

  /**
    * Converting all power plants.
    *
    * @param gridInput      Total grid input model to convert
    * @param nodeConversion Already known conversion mapping of nodes
    * @return A mapping from power plants to their assigned, specific time series
    */
  def convertPowerPlants(
      gridInput: GridModel,
      nodeConversion: Map[Node, NodeInput]
  ): Map[FixedFeedInInput, IndividualTimeSeries[PValue]] = {
    val powerPlantProfiles = gridInput.powerPlantProfiles
      .map(profile => profile.profileType -> profile)
      .toMap
    PowerPlantConverter.convert(
      gridInput.powerPlants,
      nodeConversion,
      powerPlantProfiles
    )
  }

  /**
    * Converting all renewable energy source system.
    *
    * @param gridInput      Total grid input model to convert
    * @param nodeConversion Already known conversion mapping of nodes
    * @return A mapping from renewable energy source system to their assigned, specific time series
    */
  def convertRes(
      gridInput: GridModel,
      nodeConversion: Map[Node, NodeInput]
  ): Map[FixedFeedInInput, IndividualTimeSeries[PValue]] = {
    val resProfiles =
      gridInput.resProfiles.map(profile => profile.profileType -> profile).toMap
    ResConverter.convert(gridInput.res, nodeConversion, resProfiles)
  }
}
