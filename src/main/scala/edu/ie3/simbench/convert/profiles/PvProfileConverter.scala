package edu.ie3.simbench.convert.profiles

import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.input.system.PvInput
import edu.ie3.datamodel.models.timeseries.individual.{
  IndividualTimeSeries,
  TimeBasedValue
}
import edu.ie3.datamodel.models.value.PValue
import edu.ie3.simbench.model.datamodel.profiles.ResProfileType
import tech.units.indriya.ComparableQuantity
import tech.units.indriya.quantity.Quantities

import javax.measure.quantity.{Angle, Dimensionless, Power}
import scala.jdk.CollectionConverters._

object PvProfileConverter {

  /** Calculates the power before the converter using the efficiency of the used
    * converter
    * @param timeSeries
    *   of power values
    * @param efficiency
    *   of the converter
    * @return
    */
  def calculatePowerBeforeConverter(
      timeSeries: IndividualTimeSeries[PValue],
      efficiency: ComparableQuantity[Dimensionless]
  ): IndividualTimeSeries[PValue] = new IndividualTimeSeries(
    timeSeries.getEntries.asScala.map { timeBasedValue =>
      // get the power value
      val powerOption = timeBasedValue.getValue.getP

      val pValue = if (powerOption.isPresent) {
        // if the power value is present, we divide it by the efficiency of the converter
        new PValue(
          powerOption.get().divide(efficiency.getValue.doubleValue())
        )
      } else timeBasedValue.getValue // else we just return the given value

      new TimeBasedValue(timeBasedValue.getTime, pValue)
    }.asJava
  )

  /** Calculates the azimuth and the elevation angle of a [[PvInput]].
    * @param timeSeries
    *   with power values
    * @param sRated
    *   the rated maximum power of the model
    * @param profile
    *   the profile of the pv model that can be used to retrieve the latitude of
    *   location the pv model is installed
    * @return
    *   the azimuth and the elevation angle in radians
    */
  def calculateAngles(
      timeSeries: IndividualTimeSeries[PValue],
      sRated: ComparableQuantity[Power],
      profile: ResProfileType
  ): (ComparableQuantity[Angle], ComparableQuantity[Angle]) = {
    // azimuth of 0° -> pv is facing south
    (
      Quantities.getQuantity(0.0, StandardUnits.AZIMUTH),
      Quantities.getQuantity(45.0, StandardUnits.SOLAR_ELEVATION_ANGLE)
    )
  }
}
