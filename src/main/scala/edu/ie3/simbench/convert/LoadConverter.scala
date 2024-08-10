package edu.ie3.simbench.convert

import edu.ie3.datamodel.models.OperationTime
import edu.ie3.datamodel.models.input.system.LoadInput
import edu.ie3.datamodel.models.input.system.characteristic.CosPhiFixed
import edu.ie3.datamodel.models.input.{NodeInput, OperatorInput}
import edu.ie3.datamodel.models.profile.LoadProfile.DefaultLoadProfiles
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries
import edu.ie3.datamodel.models.value.SValue
import edu.ie3.simbench.convert.profiles.PowerProfileConverter
import edu.ie3.simbench.model.datamodel.profiles.{LoadProfile, LoadProfileType}
import edu.ie3.simbench.model.datamodel.{Load, Node}
import edu.ie3.util.quantities.PowerSystemUnits.{KILOWATTHOUR, MEGAVOLTAMPERE}
import tech.units.indriya.quantity.Quantities

import java.util.{Locale, UUID}
import scala.collection.parallel.CollectionConverters._

case object LoadConverter extends ShuntConverter {
  def convert(
      loads: Vector[Load],
      nodes: Map[Node, NodeInput],
      profiles: Map[LoadProfileType, LoadProfile]
  ): Map[LoadInput, IndividualTimeSeries[SValue]] = {

    val modelScalings = loads
      .map { input =>
        input.profile -> (input.pLoad, input.qLoad)
      }
      .groupBy(_._1)
      .map { entry => entry._1 -> entry._2.map(_._2).toSet }

    val convertedTimeSeries = PowerProfileConverter.convertS(
      modelScalings,
      profiles
    )

    println(s"Load: ${convertedTimeSeries.size}")

    loads.par
      .map { load =>
        val node = NodeConverter.getNode(load.node, nodes)
        val series = convertedTimeSeries((load.profile, load.pLoad, load.qLoad))
        convert(load, node) -> series
      }
      .seq
      .toMap
  }

  /** Converts a single SimBench [[Load]] to ie3's [[LoadInput]]. Currently not
    * sufficiently covered:
    *   - Consumed energy throughout the year
    *   - different VAr characteristics
    *
    * @param input
    *   Input model
    * @param node
    *   Node, the load is connected to
    * @param uuid
    *   UUID to use for the model generation (default: Random UUID)
    * @return
    *   A [[LoadInput]] model
    */
  def convert(
      input: Load,
      node: NodeInput,
      uuid: UUID = UUID.randomUUID()
  ): LoadInput = {
    val id = input.id
    val cosphi = cosPhi(input.pLoad, input.qLoad)
    val varCharacteristicString =
      "cosPhiFixed:{(0.0,%#.2f)}".formatLocal(Locale.ENGLISH, cosphi)
    val eCons = Quantities.getQuantity(0d, KILOWATTHOUR)
    val sRated = Quantities.getQuantity(input.sR, MEGAVOLTAMPERE)

    new LoadInput(
      uuid,
      id,
      OperatorInput.NO_OPERATOR_ASSIGNED,
      OperationTime.notLimited(),
      node,
      new CosPhiFixed(varCharacteristicString),
      null,
      DefaultLoadProfiles.NO_LOAD_PROFILE,
      false,
      eCons,
      sRated,
      cosphi
    )
  }
}
