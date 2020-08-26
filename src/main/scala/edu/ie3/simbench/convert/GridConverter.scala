package edu.ie3.simbench.convert

import com.typesafe.scalalogging.LazyLogging
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
import edu.ie3.datamodel.models.value.{PValue, SValue}
import edu.ie3.simbench.convert.types.{
  LineTypeConverter,
  Transformer2wTypeConverter
}
import edu.ie3.simbench.exception.ConversionException
import edu.ie3.simbench.model.datamodel.{GridModel, Node, NodePFResult, Switch}

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
      Vector[NodeResult]
  ) = {
    logger.debug(s"Converting raw grid elements of '${gridInput.simbenchCode}'")
    val (rawGridElements, nodeConversion) = convertGridElements(gridInput)

    logger.debug(
      s"Converting system participants and their time series of '${gridInput.simbenchCode}'"
    )
    val (systemParticipants, timeSeries) =
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

    val firstNodeConversion = convertNodes(gridInput, subnetConverter)

    /* Update the subnet attribute at all nodes within transformer's upstream switchgear */
    val nodeConversion = updateSubnetInSwitchGears(
      firstNodeConversion,
      subnetConverter,
      gridInput
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
    * Converts all apparent nodes to the equivalent power system data model. The slack information is derived from the
    * attributes of external nets, power plants and renewable energy sources.
    *
    * @param gridInput       Total grid input model to convert
    * @param subnetConverter Converter holding the mapping information from simbench to power system data model sub grid
    * @return A map from simbench to power system data model nodes
    */
  private def convertNodes(
      gridInput: GridModel,
      subnetConverter: SubnetConverter
  ): Map[Node, NodeInput] = {
    val slackNodeKeys = NodeConverter.getSlackNodeKeys(
      gridInput.externalNets,
      gridInput.powerPlants,
      gridInput.res
    )
    gridInput.nodes
      .map(
        node =>
          node -> NodeConverter.convert(node, slackNodeKeys, subnetConverter)
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
    * Traverse upstream of switch chain at transformer's high voltage nodes to comply the subnet assignment to
    * conventions found in PowerSystemDataModel: Those nodes will also belong to the inferior sub grid.
    *
    * @param initialNodeConversion  Mapping from SimBench to psdm nodes, that needs update
    * @param subnetConverter        Converter holding information about subnet mapping
    * @param gridModel              Overall grid model with all needed topological models
    * @return An updated mapping from SimBench to psdm nodes
    */
  def updateSubnetInSwitchGears(
      initialNodeConversion: Map[Node, NodeInput],
      subnetConverter: SubnetConverter,
      gridModel: GridModel
  ): Map[Node, NodeInput] = {
    val updatedConversion =
      gridModel.transformers2w.foldLeft(initialNodeConversion) {
        case (conversion, transformer) =>
          val relevantSubnet = subnetConverter.convert(
            transformer.nodeLV.vmR,
            transformer.nodeLV.subnet
          )

          updateAlongSwitchChain(
            conversion,
            transformer.nodeHV,
            gridModel,
            relevantSubnet
          )
      }

    gridModel.transformers3w.foldLeft(updatedConversion) {
      case (conversion, transformer) =>
        val relevantSubnet = subnetConverter.convert(
          transformer.nodeHV.vmR,
          transformer.nodeHV.subnet
        )

        updateAlongSwitchChain(
          conversion,
          transformer.nodeHV,
          gridModel,
          relevantSubnet
        )
    }
  }

  /**
    * Traveling along a switch chain starting from a starting node and stopping at dead ends and those nodes, that are
    * directly related to lines or transformers (read: not only switches). During this travel, every node we come along
    * is updated to the relevant subnet and the mapping from SimBench to psdm is updated as well.
    *
    * @param initialNodeConversion Mapping from SimBench to psdm model that needs update
    * @param startNode             Start node from which the travel is supposed to start
    * @param gridModel             Model of the grid to take information about lines and transformers from
    * @param relevantSubnet        The subnet id to set
    * @return updated mapping from SimBench to psdm node model
    */
  private def updateAlongSwitchChain(
      initialNodeConversion: Map[Node, NodeInput],
      startNode: Node,
      gridModel: GridModel,
      relevantSubnet: Int
  ): Map[Node, NodeInput] = {
    val junctions = (gridModel.lines.flatMap(
      line => Vector(line.nodeA, line.nodeB)
    ) ++ gridModel.transformers2w.flatMap(
      transformer => Vector(transformer.nodeHV, transformer.nodeLV)
    ) ++ gridModel.transformers3w.flatMap(
      transformer =>
        Vector(transformer.nodeHV, transformer.nodeLV, transformer.nodeMV)
    )).distinct
    updateAlongSwitchChain(
      initialNodeConversion,
      startNode,
      gridModel.switches,
      junctions,
      relevantSubnet
    )
  }

  /**
    * Traveling along a switch chain starting from a starting node and stopping at dead ends and those nodes, that are
    * marked explicitly as junctions. During this travel, every node we come along is updated to the relevant subnet and
    * the mapping from SimBench to psdm is updated as well.
    *
    * @param initialNodeConversion Mapping from SimBench to psdm model that needs update
    * @param startNode             Start node from which the travel is supposed to start
    * @param switches              Collection of all switches to consider
    * @param junctions             Collection of nodes that are meant to be junctions
    * @param relevantSubnet        The subnet id to set
    * @return updated mapping from SimBench to psdm node model
    */
  private def updateAlongSwitchChain(
      initialNodeConversion: Map[Node, NodeInput],
      startNode: Node,
      switches: Vector[Switch],
      junctions: Vector[Node],
      relevantSubnet: Int
  ): Map[Node, NodeInput] = {
    /* Get the switch, that is connected to the starting node and determine the next node */
    val nextSwitches = switches.filter {
      case Switch(_, nodeA, nodeB, _, _, _, _, _) =>
        nodeA == startNode || nodeB == startNode
    }

    if (nextSwitches.isEmpty) {
      /* There is no further switch, therefore the end is reached -> return the new mapping */
      initialNodeConversion
    } else {
      /* Copy new node and add it to the mapping */
      val updatedNode = initialNodeConversion
        .getOrElse(
          startNode,
          throw ConversionException(
            s"Cannot update the subnet of conversion of '$startNode', as its conversion cannot be found."
          )
        )
        .copy()
        .subnet(relevantSubnet)
        .build()
      val updatedConversion = initialNodeConversion + (startNode -> updatedNode)
      val newJunctions = junctions.appended(startNode)

      /* For all possible next upcoming switches -> Traverse along each branch (depth first search) */
      nextSwitches.foldLeft(updatedConversion) {
        case (conversion, switch) =>
          /* Determine the next node */
          val nextNode =
            Vector(switch.nodeA, switch.nodeB).find(_ != startNode) match {
              case Some(value) => value
              case None =>
                throw ConversionException(
                  s"Cannot traverse along '$switch', as the next node cannot be determined."
                )
            }

          /* If there is a junction at the end of the chain -> don't touch anything */
          if (newJunctions.contains(nextNode))
            return conversion
          else
            return updateAlongSwitchChain(
              conversion,
              nextNode,
              switches,
              newJunctions,
              relevantSubnet
            )
      }
    }
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
  ): (SystemParticipants, Vector[IndividualTimeSeries[_ <: PValue]]) = {
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

    /* Collect all their time series */
    val timeSeries = loadsToTimeSeries.values.toVector ++ powerPlantsToTimeSeries.values.toVector ++ resToTimeSeries.values
      .toVector

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
      timeSeries
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
