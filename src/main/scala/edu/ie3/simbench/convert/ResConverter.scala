package edu.ie3.simbench.convert

import edu.ie3.datamodel.models.{OperationTime, StandardUnits}
import edu.ie3.datamodel.models.input.system.characteristic.CosPhiFixed
import edu.ie3.datamodel.models.input.system.{FixedFeedInInput, PvInput}
import edu.ie3.datamodel.models.input.{NodeInput, OperatorInput}
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries
import edu.ie3.datamodel.models.value.PValue
import edu.ie3.simbench.convert.profiles.{
  PowerProfileConverter,
  PvProfileConverter
}
import edu.ie3.simbench.io.ParticipantToInput
import edu.ie3.simbench.model.datamodel.enums.ResType.{PV, PvMv}
import edu.ie3.simbench.model.datamodel.profiles.{ResProfile, ResProfileType}
import edu.ie3.simbench.model.datamodel.{Node, RES}
import edu.ie3.util.quantities.PowerSystemUnits.{
  MEGAVAR,
  MEGAVOLTAMPERE,
  MEGAWATT
}
import tech.units.indriya.ComparableQuantity
import tech.units.indriya.quantity.Quantities

import java.util.{Locale, UUID}
import javax.measure.quantity.Dimensionless
import scala.collection.parallel.CollectionConverters._

case object ResConverter extends ShuntConverter {

  final case class ConvertedRes(
      fixedFeedInInput: Map[FixedFeedInInput, IndividualTimeSeries[PValue]],
      pvInput: Set[PvInput]
  )

  /** Convert a full set of renewable energy source system
    *
    * @param res
    *   Input models to convert
    * @param nodes
    *   Mapping from Simbench to power system data model node
    * @param profiles
    *   Collection of [[ResProfile]]s
    * @param participantToInput
    *   Whether or not to convert a given type of participant into actual input
    *   models
    * @return
    *   A mapping from converted renewable energy source system to equivalent
    *   individual time series
    */
  def convert(
      res: Vector[RES],
      nodes: Map[Node, NodeInput],
      profiles: Map[ResProfileType, ResProfile],
      participantToInput: ParticipantToInput
  ): ConvertedRes = {

    /* check whether to convert pv plants into PvInputs */
    val (pvs, pvInputs) = if (participantToInput.pvInput) {
      val plants =
        res.filter(input => input.resType == PV || input.resType == PvMv)
      val inputs = plants.par
        .map { plant =>
          val node = NodeConverter.getNode(plant.node, nodes)
          val profile =
            PowerProfileConverter.getProfile(plant.profile, profiles)
          convertPv(plant, node, profile)
        }
        .seq
        .toSet

      (plants, inputs)
    } else (Vector.empty, Set.empty[PvInput])

    /* convert all other plants into a FixedFeedInInputs */
    val fixedFeedIns = res.diff(pvs)
    val fixedFeedInInput = fixedFeedIns.par
      .map { plant =>
        val node = NodeConverter.getNode(plant.node, nodes)
        val profile =
          PowerProfileConverter.getProfile(plant.profile, profiles)
        convert(plant, node, profile)
      }
      .seq
      .toMap

    /* return the converted models */
    ConvertedRes(fixedFeedInInput, pvInputs)
  }

  def convertPv(
      input: RES,
      node: NodeInput,
      profile: ResProfile,
      uuid: Option[UUID] = None
  ): PvInput = {
    val p = Quantities.getQuantity(input.p, MEGAWATT)
    val q = Quantities.getQuantity(input.q, MEGAVAR)
    val cosphi = cosPhi(p.getValue.doubleValue(), q.getValue.doubleValue())
    val varCharacteristicString =
      "cosPhiFixed:{(0.0,%#.2f)}".formatLocal(Locale.ENGLISH, cosphi)
    val sRated = Quantities.getQuantity(input.sR, MEGAVOLTAMPERE)

    /* Flip the sign, as infeed is negative in PowerSystemDataModel */
    val timeSeries = PowerProfileConverter.convert(profile, p.multiply(-1))

    /* build pv parameter from time series */
    // TODO: Check these values, that vn_simona defaults to
    val etaConv: ComparableQuantity[Dimensionless] = Quantities.getQuantity(
      97,
      StandardUnits.EFFICIENCY
    ) // vn_simona uses 95%, 97% and 98%
    val albedo: Double = 0.20000000298023224 // see vn_simona
    val kG: Double = 0.8999999761581421 // see vn_simona
    val kT: Double = 1.0 // see vn_simona

    /* calculate the power values before the converter */
    val powerBeforeConverter =
      PvProfileConverter.calculatePowerBeforeConverter(
        timeSeries,
        etaConv,
        kG,
        kT
      )

    /* calculate the angles of the pv input */
    val azimuth = PvProfileConverter.getAzimuth(profile.profileType)

    val elevationAngle =
      PvProfileConverter.calculateElevationAngle(
        powerBeforeConverter,
        sRated,
        profile.profileType,
        azimuth
      )

    new PvInput(
      uuid.getOrElse(UUID.randomUUID()),
      input.id + "_" + input.resType.toString,
      OperatorInput.NO_OPERATOR_ASSIGNED,
      OperationTime.notLimited(),
      node,
      new CosPhiFixed(varCharacteristicString),
      null,
      albedo,
      azimuth,
      etaConv,
      elevationAngle,
      kG,
      kT,
      false,
      sRated,
      cosphi
    )
  }

  /** Converts a single renewable energy source system to a fixed feed in model
    * due to lacking information to sophistically guess typical types of assets.
    * Different voltage regulation strategies are not covered, yet.
    *
    * @param input
    *   Input model
    * @param node
    *   Node, the renewable energy source system is connected to
    * @param profile
    *   SimBench renewable energy source system profile
    * @param uuid
    *   Option to a specific uuid
    * @return
    *   A pair of [[FixedFeedInInput]] and matching active power time series
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

    /* Flip the sign, as infeed is negative in PowerSystemDataModel */
    val timeSeries = PowerProfileConverter.convert(profile, p.multiply(-1))

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
    ) -> timeSeries
  }
}
