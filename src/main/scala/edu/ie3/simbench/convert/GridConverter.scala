package edu.ie3.simbench.convert

import com.typesafe.scalalogging.LazyLogging
import edu.ie3.datamodel.io.source.TimeSeriesMappingSource
import edu.ie3.datamodel.io.source.TimeSeriesMappingSource.MappingEntry
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.connector.{
  LineInput,
  SwitchInput,
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
import edu.ie3.datamodel.models.input.system.*
import edu.ie3.datamodel.models.result.NodeResult
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries
import edu.ie3.datamodel.models.value.{PValue, SValue}
import edu.ie3.simbench.convert.NodeConverter.AttributeOverride.{
  JoinOverride,
  SubnetOverride
}
import edu.ie3.simbench.convert.types.{
  LineTypeConverter,
  Transformer2wTypeConverter
}
import edu.ie3.simbench.exception.ConversionException
import edu.ie3.simbench.model.datamodel.*

import scala.annotation.tailrec
import scala.collection.parallel.CollectionConverters.*
import scala.jdk.CollectionConverters.*

case object GridConverter extends LazyLogging {

  /** Converts a full simbench grid into power system data models
    * [[JointGridContainer]]. Additionally, individual time series for all
    * system participants are delivered as well.
    *
    * @param simbenchCode
    *   Simbench code, that is used as identifier for the grid
    * @param gridInput
    *   Total grid input model to be converted
    * @param removeSwitches
    *   Whether to remove switches from the grid structure
    * @return
    *   A converted [[JointGridContainer]], a [[Vector]] of
    *   [[IndividualTimeSeries]] as well as a [[Vector]] of [[NodeResult]]s
    */
  def convert(
      simbenchCode: String,
      gridInput: GridModel,
      removeSwitches: Boolean
  ): (
      JointGridContainer,
      Set[IndividualTimeSeries[? <: PValue]],
      Seq[MappingEntry],
      Vector[NodeResult]
  ) = {
    logger.debug(s"Converting raw grid elements of '${gridInput.simbenchCode}'")
    val (rawGridElements, nodeConversion) =
      convertGridElements(gridInput, removeSwitches)

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

  /** Converts all elements that do form the grid itself.
    *
    * @param gridInput
    *   Total grid input model to convert
    * @param removeSwitches
    *   Whether to remove switches from the grid structure
    * @return
    *   All grid elements in converted form + a mapping from old to new node
    *   models
    */
  def convertGridElements(
      gridInput: GridModel,
      removeSwitches: Boolean
  ): (RawGridElements, Map[Node, NodeInput]) = {
    /* Set up a subnet converter, by crawling all nodes */
    val subnetConverter = SubnetConverter(
      gridInput.nodes.map(node => (node.vmR, node.subnet))
    )

    val slackNodeKeys = NodeConverter.getSlackNodeKeys(
      gridInput.externalNets,
      gridInput.powerPlants,
      gridInput.res
    )

    val joinOverrides = if removeSwitches then {
      /* If switches are meant to be removed, join all nodes at closed switches */
      determineJoinOverrides(gridInput.switches, slackNodeKeys)
    } else Vector.empty

    val nodeConversion =
      convertNodes(
        gridInput.nodes,
        slackNodeKeys,
        subnetConverter,
        joinOverrides
      )

    val lines = convertLines(gridInput, nodeConversion).toSet.asJava
    val transformers2w =
      convertTransformers2w(gridInput, nodeConversion).toSet.asJava
    val transformers3w =
      Set
        .empty[Transformer3WInput]
        .asJava /* Currently, no conversion strategy is known */
    logger.debug(
      "Creation of three winding transformers is not yet implemented."
    )
    val switches =
      if !removeSwitches then
        SwitchConverter.convert(gridInput.switches, nodeConversion).toSet.asJava
      else Set.empty[SwitchInput].asJava
    val measurements = MeasurementConverter
      .convert(gridInput.measurements, nodeConversion)
      .toSet
      .asJava

    val connectedNodes = filterIsolatedNodes(
      nodeConversion,
      lines,
      transformers2w,
      transformers3w,
      switches
    )

    (
      new RawGridElements(
        connectedNodes.values.toSet.asJava,
        lines,
        transformers2w,
        transformers3w,
        switches,
        measurements
      ),
      connectedNodes
    )
  }

  /** Determine join overrides for all nodes, that are connected by closed
    * switches
    *
    * @param switches
    *   Collection of all (closed) switches
    * @param slackNodeKeys
    *   Collection of node keys, that are foreseen to be slack nodes
    * @return
    *   A collection of [[JoinOverride]]s
    */
  private def determineJoinOverrides(
      switches: Vector[Switch],
      slackNodeKeys: Vector[Node.NodeKey]
  ): Vector[JoinOverride] = {
    val closedSwitches = switches.filter(_.cond)
    val switchGroups = determineSwitchGroups(closedSwitches)
    switchGroups
      .flatMap(switchGroup =>
        determineJoinOverridesForSwitchGroup(switchGroup, slackNodeKeys)
      )
      .distinct
  }

  /** Determine groups of directly connected switches
    *
    * @param switches
    *   Collection of switches to group
    * @param switchGroups
    *   Current collection of switch groups (for recursion)
    * @return
    *   A collection of switch collections, that denote groups
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

  /** Determine overrides per switch group
    *
    * @param switchGroup
    *   A group of directly connected switches
    * @param slackNodeKeys
    *   Collection of node keys, that are foreseen to be slack nodes
    * @return
    *   A collection of [[JoinOverride]]s
    */
  private def determineJoinOverridesForSwitchGroup(
      switchGroup: Vector[Switch],
      slackNodeKeys: Vector[Node.NodeKey]
  ): Vector[JoinOverride] = {
    val nodes =
      switchGroup.flatMap(switch => Vector(switch.nodeA, switch.nodeB))
    val leadingNode =
      nodes.find(node => slackNodeKeys.contains(node.getKey)).getOrElse {
        /* Get the node with the most occurrences */
        nodes.groupBy(identity).view.mapValues(_.size).toMap.maxBy(_._2)._1
      }
    val leadingNodeKey = leadingNode.getKey
    nodes.distinct
      .filterNot(_ == leadingNode)
      .map(node => JoinOverride(node.getKey, leadingNodeKey))
  }

  /** Converts all apparent nodes to the equivalent power system data model. The
    * slack information is derived from the attributes of external nets, power
    * plants and renewable energy sources.
    *
    * @param nodes
    *   All nodes to convert
    * @param slackNodeKeys
    *   Node identifier for those, that are foreseen to be slack nodes
    * @param subnetConverter
    *   Converter holding the mapping information from simbench to power system
    *   data model sub grid
    * @param joinOverrides
    *   Collection of pairs of nodes, that are meant to be joined
    * @return
    *   A map from simbench to power system data model nodes
    */
  private def convertNodes(
      nodes: Vector[Node],
      slackNodeKeys: Vector[Node.NodeKey],
      subnetConverter: SubnetConverter,
      joinOverrides: Vector[JoinOverride]
  ): Map[Node, NodeInput] = {

    /* First convert all nodes, that are target of node joins */
    val nodeToJoinMap = joinOverrides.map { case JoinOverride(key, joinWith) =>
      key -> joinWith
    }.toMap
    val joinTargetNodeKeys = nodeToJoinMap.values.toSeq.distinct
    val (joinTargetNodes, remainingNodes) =
      nodes.partition(node => joinTargetNodeKeys.contains(node.getKey))
    val joinTargetConversion = joinTargetNodes.par
      .map(node =>
        node -> NodeConverter.convert(
          node,
          slackNodeKeys,
          subnetConverter
        )
      )
      .seq
      .toMap

    /* Then map all nodes to be joined to the converted target nodes */
    val (nodesToBeJoined, singleNodes) = remainingNodes.partition(node =>
      nodeToJoinMap.keySet.contains(node.getKey)
    )
    val conversionWithJoinedNodes = joinTargetConversion ++ nodesToBeJoined.par
      .map { node =>
        node -> joinTargetConversion.getOrElse(
          node,
          throw ConversionException(
            s"The node with key '${node.getKey}' was meant to be joined with another node, but that converted target node is not apparent."
          )
        )
      }
      .seq
      .toMap

    /* Finally convert all leftover nodes */
    conversionWithJoinedNodes ++ singleNodes.par
      .map(node =>
        node -> NodeConverter.convert(
          node,
          slackNodeKeys,
          subnetConverter
        )
      )
      .seq
      .toMap
  }

  /** Convert the given [[NodePFResult]]s with the help of yet known conversion
    * mapping of nodes
    *
    * @param input
    *   Vector of [[NodePFResult]] to convert
    * @param nodeConversion
    *   Mapping from SimBench to psdm node model
    * @return
    *   A [[Vector]] of converted [[NodeResult]]
    */
  private def convertNodeResults(
      input: Vector[NodePFResult],
      nodeConversion: Map[Node, NodeInput]
  ): Vector[NodeResult] =
    input.par.map { nodePfResult =>
      val node = nodeConversion.getOrElse(
        nodePfResult.node,
        throw ConversionException(
          s"Cannot convert power flow result for node ${nodePfResult.node}, as the needed node conversion cannot be found."
        )
      )
      NodePFResultConverter.convert(nodePfResult, node)
    }.seq

  /** Converts the given lines.
    *
    * @param gridInput
    *   Total grid input model to convert
    * @param nodeConversion
    *   Already known conversion mapping of nodes
    * @return
    *   A vector of converted line models
    */
  private def convertLines(
      gridInput: GridModel,
      nodeConversion: Map[Node, NodeInput]
  ): Vector[LineInput] = {
    val lineTypes = LineTypeConverter.convert(gridInput.lines)
    LineConverter.convert(gridInput.lines, lineTypes, nodeConversion)
  }

  /** Converts the given two winding transformers.
    *
    * @param gridInput
    *   Total grid input model to convert
    * @param nodeConversion
    *   Already known conversion mapping of nodes
    * @return
    *   A vector of converted two winding transformer models
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

  /** Filter the given node conversion for only those nodes, that are also part
    * of any branch
    *
    * @param nodeConversion
    *   Mapping from original node to converted model
    * @param lines
    *   Collection of all lines
    * @param transformers2w
    *   Collection of all two winding transformers
    * @param transformers3w
    *   Collection of all three winding transformers
    * @param switches
    *   Collection of all switches
    * @return
    *   A mapping, that only contains connected nodes
    */
  private def filterIsolatedNodes(
      nodeConversion: Map[Node, NodeInput],
      lines: java.util.Set[LineInput],
      transformers2w: java.util.Set[Transformer2WInput],
      transformers3w: java.util.Set[Transformer3WInput],
      switches: java.util.Set[SwitchInput]
  ): Map[Node, NodeInput] = {
    val branchNodes = lines.asScala.flatMap(line =>
      Vector(line.getNodeA, line.getNodeB)
    ) ++ transformers2w.asScala.flatMap(transformer =>
      Vector(transformer.getNodeA, transformer.getNodeB)
    ) ++ transformers3w.asScala.flatMap(transformer =>
      Vector(transformer.getNodeA, transformer.getNodeB, transformer.getNodeC)
    ) ++ switches.asScala.flatMap(switch =>
      Vector(switch.getNodeA, switch.getNodeB)
    )
    nodeConversion.partition { case (_, convertedNode) =>
      branchNodes.contains(convertedNode)
    } match {
      case (connectedNodes, unconnectedNodes) =>
        if unconnectedNodes.nonEmpty then
          logger.warn(
            "The nodes with following keys are not part of any branch (aka. isolated) and will be neglected in the sequel.\n\t{}",
            unconnectedNodes.map(_._1.getKey).mkString("\n\t")
          )
        connectedNodes
    }
  }

  /** Converts all system participants and extracts their individual power time
    * series
    *
    * @param gridInput
    *   Total grid input model to convert
    * @param nodeConversion
    *   Already known conversion mapping of nodes
    * @return
    *   A collection of converted system participants and their individual time
    *   series
    */
  def convertParticipants(
      gridInput: GridModel,
      nodeConversion: Map[Node, NodeInput]
  ): (
      SystemParticipants,
      Set[IndividualTimeSeries[? <: PValue]],
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
    val participantsToTimeSeries =
      loadsToTimeSeries ++ powerPlantsToTimeSeries ++ resToTimeSeries
    val mapping = participantsToTimeSeries.map { case (model, timeSeries) =>
      new TimeSeriesMappingSource.MappingEntry(
        model.getUuid,
        timeSeries.getUuid
      )
    }.toSeq
    val timeSeries: Set[IndividualTimeSeries[? <: PValue]] =
      participantsToTimeSeries.map(_._2).toSet

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

  /** Converting all loads.
    *
    * @param gridInput
    *   Total grid input model to convert
    * @param nodeConversion
    *   Already known conversion mapping of nodes
    * @return
    *   A mapping from loads to their assigned, specific time series
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

  /** Converting all power plants.
    *
    * @param gridInput
    *   Total grid input model to convert
    * @param nodeConversion
    *   Already known conversion mapping of nodes
    * @return
    *   A mapping from power plants to their assigned, specific time series
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

  /** Converting all renewable energy source system.
    *
    * @param gridInput
    *   Total grid input model to convert
    * @param nodeConversion
    *   Already known conversion mapping of nodes
    * @return
    *   A mapping from renewable energy source system to their assigned,
    *   specific time series
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
