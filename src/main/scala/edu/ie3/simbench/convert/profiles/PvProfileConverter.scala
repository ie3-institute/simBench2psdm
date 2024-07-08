package edu.ie3.simbench.convert.profiles

import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries
import edu.ie3.datamodel.models.value.PValue
import tech.units.indriya.ComparableQuantity
import tech.units.indriya.quantity.Quantities

import javax.measure.quantity.{Angle, Dimensionless}

object PvProfileConverter {

  def convert(
      timeSeries: IndividualTimeSeries[PValue]
  ): (
      Double,
      ComparableQuantity[Angle],
      ComparableQuantity[Dimensionless],
      ComparableQuantity[Angle],
      Double,
      Double
  ) = {

    val albedo: Double = 0.0
    val azimuth = Quantities.getQuantity(0.0, StandardUnits.AZIMUTH)
    val elevationAngle =
      Quantities.getQuantity(0.0, StandardUnits.SOLAR_ELEVATION_ANGLE)

    // TODO: Check these default values
    val etaConv = Quantities.getQuantity(
      95,
      StandardUnits.EFFICIENCY
    ) // efficiency of the converter
    val kG: Double = 0.8999999761581421 // see vn_simona
    val kT: Double = 1.0 // see vn_simona

    (albedo, azimuth, etaConv, elevationAngle, kG, kT)
  }

}
