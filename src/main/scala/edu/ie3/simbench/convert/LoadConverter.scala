package edu.ie3.simbench.convert

import java.util.{Locale, UUID}

import edu.ie3.models.OperationTime
import edu.ie3.models.input.{NodeInput, OperatorInput}
import edu.ie3.models.input.system.LoadInput
import edu.ie3.models.timeseries.STimeSeries
import edu.ie3.simbench.convert.profiles.PowerProfileConverter
import edu.ie3.simbench.model.datamodel.{Load, Node}
import edu.ie3.simbench.model.datamodel.profiles.{LoadProfile, LoadProfileType}
import edu.ie3.util.quantities.PowerSystemUnits.{
  KILOWATTHOUR,
  MEGAVAR,
  MEGAVOLTAMPERE,
  MEGAWATT
}
import tec.uom.se.quantity.Quantities

import scala.math.{atan, cos}

case object LoadConverter {
  def convert(loads: Vector[Load],
              nodes: Map[Node, NodeInput],
              profiles: Map[LoadProfileType, LoadProfile])
    : Map[LoadInput, STimeSeries] = {
    (for (load <- loads) yield {
      val node = NodeConverter.getNode(load.node, nodes)
      val profile = PowerProfileConverter.getProfile(load.profile, profiles)
      convert(load, node, profile)
    }).toMap
  }

  /**
    * Converts a single SimBench [[Load]] to ie³'s [[LoadInput]]. Currently not sufficiently covered:
    *   - Consumed energy throughout the year
    *   - different VAr characteristics
    *
    * @param input    Input model
    * @param node     Node, the load is connected to
    * @param profile  SimBench load profile
    * @param uuid     UUID to use for the model generation (default: Random UUID)
    * @return         A [[LoadInput]] model
    */
  def convert(input: Load,
              node: NodeInput,
              profile: LoadProfile,
              uuid: UUID = UUID.randomUUID()): (LoadInput, STimeSeries) = {
    val id = input.id
    val cosphi = cos(atan((input.qLoad / input.pLoad).doubleValue))
    val varCharacteristics = "cosphi_fixed:" + "%#.2f".formatLocal(
      Locale.ENGLISH,
      cosphi)
    val eCons = Quantities.getQuantity(0d, KILOWATTHOUR)
    val sRated = Quantities.getQuantity(input.sR, MEGAVOLTAMPERE)

    val p = Quantities.getQuantity(input.pLoad, MEGAWATT)
    val q = Quantities.getQuantity(input.qLoad, MEGAVAR)
    val timeSeries = PowerProfileConverter.convert(profile, p, q)

    new LoadInput(uuid,
                  OperationTime.notLimited(),
                  OperatorInput.NO_OPERATOR_ASSIGNED,
                  id,
                  node,
                  varCharacteristics,
                  false,
                  eCons,
                  sRated,
                  cosphi) ->
      timeSeries
  }
}
