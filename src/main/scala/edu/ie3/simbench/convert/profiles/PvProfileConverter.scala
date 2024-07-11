package edu.ie3.simbench.convert.profiles

import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.input.system.PvInput
import edu.ie3.datamodel.models.timeseries.individual.{
  IndividualTimeSeries,
  TimeBasedValue
}
import edu.ie3.datamodel.models.value.PValue
import edu.ie3.simbench.convert.profiles.ResProfileConverter.CompassDirection.{
  East,
  North,
  South,
  West
}
import edu.ie3.simbench.model.datamodel.profiles.ResProfileType
import org.locationtech.jts.geom.Coordinate
import tech.units.indriya.ComparableQuantity
import tech.units.indriya.quantity.Quantities

import java.time.ZonedDateTime
import javax.measure.quantity.{Angle, Dimensionless, Power}
import scala.jdk.CollectionConverters._
import scala.jdk.OptionConverters._

object PvProfileConverter {

  /** Calculates the power before the converter using the efficiency of the used
    * converter and the technical influence.
    *
    * @param timeSeries
    *   of power values
    * @param efficiency
    *   efficiency of converter (typically in %)
    * @param kG
    *   generator correction factor merging different technical influences
    * @param kT
    *   generator correction factor merging different technical influences
    * @return
    */
  def calculatePowerBeforeConverter(
      timeSeries: IndividualTimeSeries[PValue],
      efficiency: ComparableQuantity[Dimensionless],
      kG: Double,
      kT: Double
  ): IndividualTimeSeries[PValue] = {
    val correctionFactor = efficiency.getValue.doubleValue() / 100 * kG * kT

    new IndividualTimeSeries(
      timeSeries.getEntries.asScala.map { timeBasedValue =>
        // get the power value
        val powerOption = timeBasedValue.getValue.getP

        val pValue = if (powerOption.isPresent) {
          // if the power value is present, we divide it by the efficiency of the converter
          new PValue(
            powerOption.get().divide(correctionFactor)
          )
        } else timeBasedValue.getValue // else we just return the given value

        new TimeBasedValue(timeBasedValue.getTime, pValue)
      }.asJava
    )
  }

  /** Determine the azimuth (inclination in a compass direction) of the
    * [[PvInput]] using the [[ResProfileType]].
    * @param profile
    *   of the plant
    * @return
    *   the azimuth of the pv input
    */
  def getAzimuth(
      profile: ResProfileType
  ): ComparableQuantity[Angle] = {
    /* extracting some useful information from the given profile type */
    val orientation = ResProfileConverter.getCompassDirection(profile)
    /* find azimuth using the given orientation */
    orientation match {
      case North => Quantities.getQuantity(180d, StandardUnits.AZIMUTH)
      case East  => Quantities.getQuantity(-90d, StandardUnits.AZIMUTH)
      case South => Quantities.getQuantity(0d, StandardUnits.AZIMUTH)
      case West  => Quantities.getQuantity(90d, StandardUnits.AZIMUTH)
    }
  }

  /** Calculates the elevation angle of a [[PvInput]].
    * @param timeSeries
    *   with power values
    * @param sRated
    *   the rated maximum power of the model
    * @param profile
    *   the profile of the pv model that can be used to retrieve the latitude of
    *   location the pv model is installed
    * @param azimuth
    *   of the [[PvInput]]
    * @return
    *   the azimuth and the elevation angle in radians
    */
  def calculateElevationAngle(
      timeSeries: IndividualTimeSeries[PValue],
      sRated: ComparableQuantity[Power],
      profile: ResProfileType,
      azimuth: ComparableQuantity[Angle]
  ): ComparableQuantity[Angle] = {
    val ratedPower = sRated.to(StandardUnits.S_RATED).getValue.doubleValue()

    val values = timeSeries.getEntries.asScala.toList.flatMap {
      timeBasedValue =>
        timeBasedValue.getValue.getP.toScala.map(value =>
          (timeBasedValue.getTime, value)
        )
    }

    val position: Coordinate = ResProfileConverter.getCoordinate(profile)

    val march21 = getMaxFeedIn(values, 3)
    val june21 = getMaxFeedIn(values, 6)
    val september21 = getMaxFeedIn(values, 9)
    val dezember21 = getMaxFeedIn(values, 12)

    Quantities.getQuantity(35d, StandardUnits.SOLAR_ELEVATION_ANGLE)
  }

  private def getMaxFeedIn(
      timeSeriesEntries: Seq[(ZonedDateTime, ComparableQuantity[Power])],
      month: Int,
      dayOfTheMonth: Int = 21
  ): Option[(ZonedDateTime, ComparableQuantity[Power])] = {
    val powerValues = timeSeriesEntries.filter(value =>
      value._1.getMonth.getValue == month && value._1.getDayOfMonth == dayOfTheMonth
    )

    powerValues.minByOption(_._2.getValue.doubleValue())
  }
}
