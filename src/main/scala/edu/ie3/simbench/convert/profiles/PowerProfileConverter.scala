package edu.ie3.simbench.convert.profiles

import edu.ie3.datamodel.models.timeseries.individual.{IndividualTimeSeries, TimeBasedValue}
import edu.ie3.datamodel.models.value.{PValue, SValue}
import edu.ie3.simbench.exception.ConversionException
import edu.ie3.simbench.model.datamodel.profiles.{ProfileModel, ProfileType}
import edu.ie3.util.quantities.PowerSystemUnits.{MEGAVAR, MEGAWATT}
import tech.units.indriya.ComparableQuantity
import tech.units.indriya.quantity.Quantities

import java.util.UUID
import javax.measure.quantity.Power
import scala.jdk.CollectionConverters._

case object PowerProfileConverter {

  /** Converts the given profiles with the given model scalings into ie3's data
    * model time series.
    *
    * @param modelScalings
    *   models with active and reactive power scalings
    * @param profiles
    *   to convert
    * @return
    *   a map of profile type and scaling factors to [[IndividualTimeSeries]]
    *   with active and reactive power for each time step
    */
  def convertS[T <: ProfileType, P <: ProfileModel[T, (BigDecimal, BigDecimal)]](
      modelScalings: Map[T, Set[(BigDecimal, BigDecimal)]],
      profiles: Map[T, P]
  ): Map[(T, BigDecimal, BigDecimal), IndividualTimeSeries[SValue]] =
    profiles.keySet.flatMap { key =>
      val profile: P = profiles(key)

      modelScalings(key).map { case (pLoad, qLoad) =>
        val p = Quantities.getQuantity(pLoad, MEGAWATT)
        val q = Quantities.getQuantity(qLoad, MEGAVAR)

        (key, pLoad, qLoad) -> PowerProfileConverter.convert(profile, p, q)
      }
    }.toMap

  /** Converts a given profile with a tuple of BigDecimal as data to a ie3's
    * data model time series denoting a tuple of active and reactive power. The
    * SimBench model gives only scaling factors for active and reactive power,
    * which will scale the maximum active and reactive power of a distinct
    * entity.
    *
    * @param profileModel
    *   The profile to convert
    * @param pRated
    *   Reference active power to meet the peak point of the profile
    * @param qRated
    *   Reference reactive power to meet the peak point of the profile
    * @return
    *   A [[IndividualTimeSeries]] with [[SValue]] (active and reactive power)
    *   for each time step
    */
  def convert(
      profileModel: ProfileModel[_ <: ProfileType, (BigDecimal, BigDecimal)],
      pRated: ComparableQuantity[Power],
      qRated: ComparableQuantity[Power]
  ): IndividualTimeSeries[SValue] = {
    val values = profileModel.profile.map { case (zdt, (pScaling, qScaling)) =>
      new TimeBasedValue(
        zdt,
        new SValue(pRated.multiply(pScaling), qRated.multiply(qScaling))
      )
    }.toSet
    new IndividualTimeSeries[SValue](UUID.randomUUID(), values.asJava)
  }

  /** Converts the given profiles with the given model scalings into ie3's data
    * model time series.
    *
    * @param modelScalings
    *   models with active power scaling
    * @param profiles
    *   to convert
    * @return
    *   a map of profile type and scaling factor to [[IndividualTimeSeries]]
    *   with active power for each time step
    */
  def convertP[T <: ProfileType, P <: ProfileModel[T, BigDecimal]](
      modelScalings: Map[T, Set[BigDecimal]],
      profiles: Map[T, P]
  ): Map[(T, BigDecimal), IndividualTimeSeries[PValue]] =
    profiles.keySet.flatMap { key =>
      val profile: P = profiles(key)

      modelScalings(key).map { pLoad =>
        val p = Quantities.getQuantity(pLoad, MEGAWATT)

        /* Flip the sign, as infeed is negative in PowerSystemDataModel */
        val timeSeries = PowerProfileConverter.convert(profile, p.multiply(-1))

        (key, pLoad) -> timeSeries
      }
    }.toMap

  /** Converts a given profile with s single BigDecimal as data to a ie3's data
    * model time series denoting active power. The SimBench model gives only
    * scaling factors for active power, which will scale the maximum active
    * power of a distinct entity.
    *
    * @param profileModel
    *   The profile to convert
    * @param pRated
    *   Reference active power to meet the peak point of the profile
    * @return
    *   A [[IndividualTimeSeries]] with active power for each time step
    */
  def convert(
      profileModel: ProfileModel[_ <: ProfileType, BigDecimal],
      pRated: ComparableQuantity[Power]
  ): IndividualTimeSeries[PValue] = {
    val values = profileModel.profile.map { case (zdt, pScaling) =>
      new TimeBasedValue(zdt, new PValue(pRated.multiply(pScaling)))
    }.toSet
    new IndividualTimeSeries[PValue](UUID.randomUUID(), values.asJava)
  }

  /** Extract one profile from the map from profile type to profile
    *
    * @param profileType
    *   Profile type to extract
    * @param profiles
    *   Map from profile type to profile
    * @tparam T
    *   Type parameter for the profile's type
    * @tparam P
    *   Type parameter for the profile itself
    * @return
    *   The equivalent [[ProfileModel]]
    */
  def getProfile[T <: ProfileType, P <: ProfileModel[T, _]](
      profileType: T,
      profiles: Map[T, P]
  ): P =
    profiles.getOrElse(
      profileType,
      throw ConversionException(s"Cannot find profile for type $profileType")
    )
}
