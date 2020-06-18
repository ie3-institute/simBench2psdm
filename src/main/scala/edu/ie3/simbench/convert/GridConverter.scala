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
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries
import edu.ie3.datamodel.models.value.{PValue, SValue}
import edu.ie3.simbench.convert.types.{
  LineTypeConverter,
  Transformer2wTypeConverter
}
import edu.ie3.simbench.model.datamodel.{GridModel, Node}

import scala.jdk.CollectionConverters._

case object GridConverter extends LazyLogging {

  /**
    * Converts a full simbench grid into power system data models [[JointGridContainer]]. Additionally, individual time
    * series for all system participants are delivered as well.
    *
    * @param simbenchCode Simbench code, that is used as identifier for the grid
    * @param gridInput    Total grid input model to be converted
    * @return A converted [[JointGridContainer]]
    */
  def convert(
      simbenchCode: String,
      gridInput: GridModel
  ): (JointGridContainer, Vector[IndividualTimeSeries[_ <: PValue]]) = {
    val (rawGridElements, nodeConversion) = convertGridElements(gridInput)
    val (systemParticipants, timeSeries) =
      convertParticipants(gridInput, nodeConversion)

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
      timeSeries
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
    val subnetConverter = SubnetConverter(gridInput.nodes.map(_.subnet))

    val nodeConversion = convertNodes(gridInput, subnetConverter)
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
    val loadsToTimeSeries = convertLoads(gridInput, nodeConversion)
    val powerPlantsToTimeSeries = convertPowerPlants(gridInput, nodeConversion)
    val resToTimeSeries = convertRes(gridInput, nodeConversion)

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
