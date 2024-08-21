package edu.ie3.simbench.convert

import com.typesafe.scalalogging.LazyLogging
import edu.ie3.datamodel.models.OperationTime
import edu.ie3.datamodel.models.input.system.FixedFeedInInput
import edu.ie3.datamodel.models.input.system.characteristic.CosPhiFixed
import edu.ie3.datamodel.models.input.{NodeInput, OperatorInput}
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries
import edu.ie3.datamodel.models.value.PValue
import edu.ie3.simbench.convert.profiles.PowerProfileConverter
import edu.ie3.simbench.model.datamodel.profiles.{ResProfile, ResProfileType}
import edu.ie3.simbench.model.datamodel.{Node, RES}
import edu.ie3.util.quantities.PowerSystemUnits.{
  MEGAVAR,
  MEGAVOLTAMPERE,
  MEGAWATT
}
import tech.units.indriya.quantity.Quantities

import java.util.{Locale, UUID}
import scala.collection.parallel.CollectionConverters._

case object ResConverter extends ShuntConverter with LazyLogging {

  /** Convert a full set of renewable energy source system
    *
    * @param res
    *   Input models to convert
    * @param nodes
    *   Mapping from Simbench to power system data model node
    * @param profiles
    *   Collection of [[ResProfile]]s
    * @return
    *   A mapping from converted renewable energy source system to equivalent
    *   individual time series
    */
  def convert(
      res: Vector[RES],
      nodes: Map[Node, NodeInput],
      profiles: Map[ResProfileType, ResProfile]
  ): Map[FixedFeedInInput, IndividualTimeSeries[PValue]] = {

    val modelScalings = res
      .map { input =>
        input.profile -> input.p
      }
      .groupBy(_._1)
      .map { entry => entry._1 -> entry._2.map(_._2).toSet }

    val convertedTimeSeries = PowerProfileConverter.convertP(
      modelScalings,
      profiles
    )

    logger.debug("Resulting RES time series: {]", convertedTimeSeries.size)

    res.par
      .map { plant =>
        val node = NodeConverter.getNode(plant.node, nodes)
        val series = convertedTimeSeries((plant.profile, plant.p))
        convert(plant, node) -> series
      }
      .seq
      .toMap
  }

  /** Converts a single renewable energy source system to a fixed feed in model
    * due to lacking information to sophistically guess typical types of assets.
    * Different voltage regulation strategies are not covered, yet.
    *
    * @param input
    *   Input model
    * @param node
    *   Node, the renewable energy source system is connected to
    * @param uuid
    *   Option to a specific uuid
    * @return
    *   A pair of [[FixedFeedInInput]] and matching active power time series
    */
  def convert(
      input: RES,
      node: NodeInput,
      uuid: Option[UUID] = None
  ): FixedFeedInInput = {
    val p = Quantities.getQuantity(input.p, MEGAWATT)
    val q = Quantities.getQuantity(input.q, MEGAVAR)
    val cosphi = cosPhi(p.getValue.doubleValue(), q.getValue.doubleValue())
    val varCharacteristicString =
      "cosPhiFixed:{(0.0,%#.2f)}".formatLocal(Locale.ENGLISH, cosphi)
    val sRated = Quantities.getQuantity(input.sR, MEGAVOLTAMPERE)

    new FixedFeedInInput(
      uuid.getOrElse(UUID.randomUUID()),
      input.id + "_" + input.resType.toString,
      OperatorInput.NO_OPERATOR_ASSIGNED,
      OperationTime.notLimited(),
      node,
      new CosPhiFixed(varCharacteristicString),
      null,
      sRated,
      cosphi
    )
  }
}
