package edu.ie3.simbench.convert.profiles

import edu.ie3.models.value.{PValue, SValue}
import edu.ie3.simbench.model.datamodel.profiles.{
  LoadProfile,
  LoadProfileType,
  PowerPlantProfile,
  PowerPlantProfileType
}
import edu.ie3.test.common.UnitSpec
import edu.ie3.util.TimeTools
import edu.ie3.util.quantities.PowerSystemUnits.{KILOVAR, KILOWATT}
import tec.uom.se.quantity.Quantities

import scala.math.abs

class PowerProfileConverterSpec extends UnitSpec {
  private val testingTolerance = 1E-6

  private val sProfile: LoadProfile = LoadProfile(
    "test profile",
    LoadProfileType.H0L,
    Map(
      TimeTools
        .toZonedDateTime("01/01/1990 00:00:00") -> (BigDecimal("0.75"), BigDecimal(
        "0.85")),
      TimeTools
        .toZonedDateTime("01/01/1990 00:15:00") -> (BigDecimal("0.55"), BigDecimal(
        "0.75")),
      TimeTools
        .toZonedDateTime("01/01/1990 00:30:00") -> (BigDecimal("0.35"), BigDecimal(
        "0.65")),
      TimeTools
        .toZonedDateTime("01/01/1990 00:45:00") -> (BigDecimal("0.15"), BigDecimal(
        "0.55"))
    )
  )

  private val pProfile: PowerPlantProfile = PowerPlantProfile(
    "test profile",
    PowerPlantProfileType.PowerPlantProfile1,
    Map(
      TimeTools.toZonedDateTime("01/01/1990 00:00:00") -> BigDecimal("0.75"),
      TimeTools.toZonedDateTime("01/01/1990 00:15:00") -> BigDecimal("0.55"),
      TimeTools.toZonedDateTime("01/01/1990 00:30:00") -> BigDecimal("0.35"),
      TimeTools.toZonedDateTime("01/01/1990 00:45:00") -> BigDecimal("0.15")
    )
  )

  "The power profile converter" should {
    "correctly convert an apparent power time series" in {
      val pRated = Quantities.getQuantity(100d, KILOWATT)
      val qRated = Quantities.getQuantity(10d, KILOVAR)

      val expected = Map(
        TimeTools.toZonedDateTime("01/01/1990 00:00:00") -> new SValue(
          Quantities.getQuantity(75d, KILOWATT),
          Quantities.getQuantity(8.5, KILOVAR)),
        TimeTools.toZonedDateTime("01/01/1990 00:15:00") -> new SValue(
          Quantities.getQuantity(55d, KILOWATT),
          Quantities.getQuantity(7.5, KILOVAR)),
        TimeTools.toZonedDateTime("01/01/1990 00:30:00") -> new SValue(
          Quantities.getQuantity(35d, KILOWATT),
          Quantities.getQuantity(6.5, KILOVAR)),
        TimeTools.toZonedDateTime("01/01/1990 00:45:00") -> new SValue(
          Quantities.getQuantity(15d, KILOWATT),
          Quantities.getQuantity(5.5, KILOVAR))
      )

      val actual = PowerProfileConverter.convert(sProfile, pRated, qRated)

      expected.foreach(
        entry =>
          abs(
            actual
              .getValue(entry._1)
              .getP
              .subtract(entry._2.getP)
              .to(KILOWATT)
              .getValue
              .doubleValue()) < testingTolerance shouldBe abs(
            actual
              .getValue(entry._1)
              .getQ
              .subtract(entry._2.getQ)
              .to(KILOVAR)
              .getValue
              .doubleValue()) < testingTolerance)
    }
  }

  "correctly convert an active power time series" in {
    val pRated = Quantities.getQuantity(100d, KILOWATT)

    val expected = Map(
      TimeTools.toZonedDateTime("01/01/1990 00:00:00") -> new PValue(
        Quantities.getQuantity(75d, KILOWATT)),
      TimeTools.toZonedDateTime("01/01/1990 00:15:00") -> new PValue(
        Quantities.getQuantity(55d, KILOWATT)),
      TimeTools.toZonedDateTime("01/01/1990 00:30:00") -> new PValue(
        Quantities.getQuantity(35d, KILOWATT)),
      TimeTools.toZonedDateTime("01/01/1990 00:45:00") -> new PValue(
        Quantities.getQuantity(15d, KILOWATT))
    )

    val actual = PowerProfileConverter.convert(pProfile, pRated)

    expected.foreach(
      entry =>
        abs(
          actual
            .getValue(entry._1)
            .getP
            .subtract(entry._2.getP)
            .to(KILOWATT)
            .getValue
            .doubleValue()) < testingTolerance)
  }
}
