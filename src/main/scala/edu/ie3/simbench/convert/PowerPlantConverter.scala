package edu.ie3.simbench.convert

import com.typesafe.scalalogging.LazyLogging

import java.util.{Locale, UUID}
import edu.ie3.datamodel.models.OperationTime
import edu.ie3.datamodel.models.input.system.FixedFeedInInput
import edu.ie3.datamodel.models.input.system.characteristic.CosPhiFixed
import edu.ie3.datamodel.models.input.{NodeInput, OperatorInput}
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries
import edu.ie3.datamodel.models.value.PValue
import edu.ie3.simbench.convert.profiles.PowerProfileConverter
import edu.ie3.simbench.model.datamodel.profiles.{
  PowerPlantProfile,
  PowerPlantProfileType
}
import edu.ie3.simbench.model.datamodel.{Node, PowerPlant}
import edu.ie3.util.quantities.PowerSystemUnits.{
  MEGAVAR,
  MEGAVOLTAMPERE,
  MEGAWATT
}
import tech.units.indriya.quantity.Quantities

import scala.collection.parallel.CollectionConverters.*

case object PowerPlantConverter extends ShuntConverter with LazyLogging {

  /** Convert a full set of power plants
    *
    * @param powerPlants
    *   Input models to convert
    * @param nodes
    *   Mapping from Simbench to power system data model node
    * @param profiles
    *   Collection of [[PowerPlantProfile]]s
    * @return
    *   A mapping from converted power plant to equivalent individual time
    *   series
    */
  def convert(
      powerPlants: Vector[PowerPlant],
      nodes: Map[Node, NodeInput],
      profiles: Map[PowerPlantProfileType, PowerPlantProfile]
  ): Map[FixedFeedInInput, IndividualTimeSeries[PValue]] = {

    val modelScalings = powerPlants
      .map { input =>
        input.profile -> input.p
      }
      .groupBy(_._1)
      .map { entry => entry._1 -> entry._2.map(_._2).toSet }

    val convertedTimeSeries = PowerProfileConverter.convertP(
      modelScalings,
      profiles
    )

    logger.debug(
      "Resulting power plant time series: {]",
      convertedTimeSeries.size
    )

    powerPlants.par
      .map { powerPlant =>
        val node = NodeConverter.getNode(powerPlant.node, nodes)
        val series = convertedTimeSeries((powerPlant.profile, powerPlant.p))
        convert(powerPlant, node) -> series
      }
      .seq
      .toMap
  }

  /** Converts a single power plant model to a fixed feed in model, as the power
    * system data model does not reflect power plants, yet. Voltage regulation
    * strategies are also not correctly accounted for.
    *
    * @param input
    *   Input model
    * @param node
    *   Node, the power plant is connected to
    * @param uuid
    *   Option to a specific uuid
    * @return
    *   A pair of [[FixedFeedInInput]] and matching active power time series
    */
  def convert(
      input: PowerPlant,
      node: NodeInput,
      uuid: Option[UUID] = None
  ): FixedFeedInInput = {
    val p = Quantities.getQuantity(input.p, MEGAWATT)
    val q = input.q match {
      case Some(value) => Quantities.getQuantity(value, MEGAVAR)
      case None        => Quantities.getQuantity(0d, MEGAVAR)
    }
    val cosphi = cosPhi(p.getValue.doubleValue(), q.getValue.doubleValue())
    val varCharacteristicString =
      "cosPhiFixed:{(0.0,%#.2f)}".formatLocal(Locale.ENGLISH, cosphi)
    val sRated = Quantities.getQuantity(input.sR, MEGAVOLTAMPERE)

    new FixedFeedInInput(
      uuid.getOrElse(UUID.randomUUID()),
      input.id,
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
