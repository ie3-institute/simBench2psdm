/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.simbench.convert

import java.util.{Locale, UUID}

import edu.ie3.datamodel.models.OperationTime
import edu.ie3.datamodel.models.input.system.FixedFeedInInput
import edu.ie3.datamodel.models.input.system.characteristic.CosPhiFixed
import edu.ie3.datamodel.models.input.{NodeInput, OperatorInput}
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries
import edu.ie3.datamodel.models.value.PValue
import edu.ie3.simbench.convert.PowerPlantConverter.cosPhi
import edu.ie3.simbench.convert.profiles.PowerProfileConverter
import edu.ie3.simbench.model.datamodel.profiles.{ResProfile, ResProfileType}
import edu.ie3.simbench.model.datamodel.{Node, RES}
import edu.ie3.util.quantities.dep.PowerSystemUnits.{
  MEGAVAR,
  MEGAVOLTAMPERE,
  MEGAWATT
}
import tec.uom.se.quantity.Quantities

import scala.math.{atan, cos, round}

case object ResConverter extends ShuntConverter {

  /**
    * Convert a full set of renewable energy source system
    *
    * @param res      Input models to convert
    * @param nodes    Mapping from Simbench to power system data model node
    * @param profiles Collection of [[ResProfile]]s
    * @return A mapping from converted renewable energy source system to equivalent individual time series
    */
  def convert(
      res: Vector[RES],
      nodes: Map[Node, NodeInput],
      profiles: Map[ResProfileType, ResProfile]
  ): Map[FixedFeedInInput, IndividualTimeSeries[PValue]] = {
    (for (plant <- res) yield {
      val node = NodeConverter.getNode(plant.node, nodes)
      val profile =
        PowerProfileConverter.getProfile(plant.profile, profiles)
      convert(plant, node, profile)
    }).toMap
  }

  /**
    * Converts a single renewable energy source system to a fixed feed in model due to lacking information to
    * sophistically guess typical types of assets. Different voltage regulation strategies are not covered, yet.
    *
    * @param input    Input model
    * @param node     Node, the renewable energy source system is connected to
    * @param profile  SimBench renewable energy source system profile
    * @param uuid     Option to a specific uuid
    * @return A pair of [[FixedFeedInInput]] and matching active power time series
    */
  def convert(
      input: RES,
      node: NodeInput,
      profile: ResProfile,
      uuid: Option[UUID] = None
  ): (FixedFeedInInput, IndividualTimeSeries[PValue]) = {
    val p = Quantities.getQuantity(input.p, MEGAWATT)
    val q = Quantities.getQuantity(input.q, MEGAVAR)
    val cosphi = cosPhi(p.getValue.doubleValue(), q.getValue.doubleValue())
    val varCharacteristicString =
      "cosPhiFixed:{(0.0,%#.2f)}".formatLocal(Locale.ENGLISH, cosphi)
    val sRated = Quantities.getQuantity(input.sR, MEGAVOLTAMPERE)

    val timeSeries = PowerProfileConverter.convert(profile, p)

    new FixedFeedInInput(
      uuid.getOrElse(UUID.randomUUID()),
      input.id + "_" + input.resType.toString,
      OperatorInput.NO_OPERATOR_ASSIGNED,
      OperationTime.notLimited(),
      node,
      new CosPhiFixed(varCharacteristicString),
      sRated,
      cosphi
    ) -> timeSeries
  }
}
