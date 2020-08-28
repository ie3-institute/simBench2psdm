/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.simbench.convert

import java.util.{Locale, UUID}

import edu.ie3.datamodel.models.OperationTime
import edu.ie3.datamodel.models.StandardLoadProfile.DefaultLoadProfiles
import edu.ie3.datamodel.models.input.system.LoadInput
import edu.ie3.datamodel.models.input.system.characteristic.CosPhiFixed
import edu.ie3.datamodel.models.input.{NodeInput, OperatorInput}
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries
import edu.ie3.datamodel.models.value.SValue
import edu.ie3.simbench.convert.profiles.PowerProfileConverter
import edu.ie3.simbench.model.datamodel.profiles.{LoadProfile, LoadProfileType}
import edu.ie3.simbench.model.datamodel.{Load, Node}
import edu.ie3.util.quantities.dep.PowerSystemUnits.{
  KILOWATTHOUR,
  MEGAVAR,
  MEGAVOLTAMPERE,
  MEGAWATT
}
import tec.uom.se.quantity.Quantities

case object LoadConverter extends ShuntConverter {
  def convert(
      loads: Vector[Load],
      nodes: Map[Node, NodeInput],
      profiles: Map[LoadProfileType, LoadProfile]
  ): Map[LoadInput, IndividualTimeSeries[SValue]] = {
    (for (load <- loads) yield {
      val node = NodeConverter.getNode(load.node, nodes)
      val profile = PowerProfileConverter.getProfile(load.profile, profiles)
      convert(load, node, profile)
    }).toMap
  }

  /**
    * Converts a single SimBench [[Load]] to ie3's [[LoadInput]]. Currently not sufficiently covered:
    *   - Consumed energy throughout the year
    *   - different VAr characteristics
    *
    * @param input    Input model
    * @param node     Node, the load is connected to
    * @param profile  SimBench load profile
    * @param uuid     UUID to use for the model generation (default: Random UUID)
    * @return         A [[LoadInput]] model
    */
  def convert(
      input: Load,
      node: NodeInput,
      profile: LoadProfile,
      uuid: UUID = UUID.randomUUID()
  ): (LoadInput, IndividualTimeSeries[SValue]) = {
    val id = input.id
    val cosphi = cosPhi(input.pLoad, input.qLoad)
    val varCharacteristicString =
      "cosPhiFixed:{(0.0,%#.2f)}".formatLocal(Locale.ENGLISH, cosphi)
    val eCons = Quantities.getQuantity(0d, KILOWATTHOUR)
    val sRated = Quantities.getQuantity(input.sR, MEGAVOLTAMPERE)

    val p = Quantities.getQuantity(input.pLoad, MEGAWATT)
    val q = Quantities.getQuantity(input.qLoad, MEGAVAR)
    val timeSeries = PowerProfileConverter.convert(profile, p, q)

    new LoadInput(
      uuid,
      id,
      OperatorInput.NO_OPERATOR_ASSIGNED,
      OperationTime.notLimited(),
      node,
      new CosPhiFixed(varCharacteristicString),
      DefaultLoadProfiles.NO_STANDARD_LOAD_PROFILE,
      false,
      eCons,
      sRated,
      cosphi
    ) ->
      timeSeries
  }
}
