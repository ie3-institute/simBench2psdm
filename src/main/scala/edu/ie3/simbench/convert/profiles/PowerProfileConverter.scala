package edu.ie3.simbench.convert.profiles

import edu.ie3.models.timeseries.{PTimeSeries, STimeSeries}
import edu.ie3.models.value.{PValue, SValue}
import edu.ie3.simbench.exception.ConversionException
import edu.ie3.simbench.model.datamodel.profiles.{ProfileModel, ProfileType}
import javax.measure.quantity.Power
import tec.uom.se.ComparableQuantity

import scala.jdk.CollectionConverters._

case object PowerProfileConverter {

  /**
    * Converts a given profile with a tuple of BigDecimal as data to a ie³'s data model time series denoting a tuple of
    * active and reactive power. The SimBench model gives only scaling factors for active and reactive power, which will
    * scale the maximum active and reactive power of a distinct entity.
    *
    * @param profile  The profile to convert
    * @param pRated   Reference active power to meet the peak point of the profile
    * @param qRated   Reference reactive power to meet the peak point of the profile
    * @return         A [[STimeSeries]] with active and reactive power for each time step
    */
  def convert(profile: ProfileModel[_ <: ProfileType, (BigDecimal, BigDecimal)],
              pRated: ComparableQuantity[Power],
              qRated: ComparableQuantity[Power]): STimeSeries = {
    val values = profile.profile.map(entry => {
      val zdt = entry._1
      val pScaling = entry._2._1
      val qScaling = entry._2._2
      zdt -> new SValue(pRated.multiply(pScaling), qRated.multiply(qScaling))
    })
    val timeSeries = new STimeSeries()
    timeSeries.addAll(values.asJava)
    timeSeries
  }

  /**
    * Converts a given profile with s single BigDecimal as data to a ie³'s data model time series denoting active power.
    * The SimBench model gives only scaling factors for active power, which will scale the maximum active power of a
    * distinct entity.
    *
    * @param profile  The profile to convert
    * @param pRated   Reference active power to meet the peak point of the profile
    * @return         A [[STimeSeries]] with active and reactive power for each time step
    */
  def convert(profile: ProfileModel[_ <: ProfileType, BigDecimal],
              pRated: ComparableQuantity[Power]): PTimeSeries = {
    val values = profile.profile.map(entry => {
      val zdt = entry._1
      val pScaling = entry._2
      zdt -> new PValue(pRated.multiply(pScaling))
    })
    val timeSeries = new PTimeSeries()
    timeSeries.addAll(values.asJava)
    timeSeries
  }

  /**
    * Extract one profile from the map from profile type to profile
    *
    * @param profileType  Profile type to extract
    * @param profiles     Map from profile type to profile
    * @tparam T           Type parameter for the profile's type
    * @tparam P           Type parameter for the profile itself
    * @return             The equivalent [[ProfileModel]]
    */
  def getProfile[T <: ProfileType, P <: ProfileModel[T, _]](
      profileType: T,
      profiles: Map[T, P]): P =
    profiles.getOrElse(
      profileType,
      throw ConversionException(s"Cannot find profile for type $profileType"))
}
