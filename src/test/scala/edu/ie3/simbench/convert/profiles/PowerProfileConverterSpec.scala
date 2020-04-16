package edu.ie3.simbench.convert.profiles

import edu.ie3.datamodel.models.value.{PValue, SValue}
import edu.ie3.simbench.exception.ConversionException
import edu.ie3.simbench.model.datamodel.profiles.{
  LoadProfile,
  LoadProfileType,
  PowerPlantProfile,
  PowerPlantProfileType
}
import edu.ie3.simbench.util.SimbenchTimeUtil
import edu.ie3.test.common.UnitSpec
import edu.ie3.util.quantities.PowerSystemUnits.{KILOVAR, KILOWATT}
import tec.uom.se.quantity.Quantities

import scala.math.abs
import scala.jdk.OptionConverters._

class PowerProfileConverterSpec extends UnitSpec {
  private val testingTolerance = 1e-6

  private val sProfile: LoadProfile = LoadProfile(
    "test profile",
    LoadProfileType.H0L,
    Map(
      SimbenchTimeUtil
        .toZonedDateTime("01.01.1990 00:00") -> (BigDecimal("0.75"), BigDecimal(
        "0.85"
      )),
      SimbenchTimeUtil
        .toZonedDateTime("01.01.1990 00:15") -> (BigDecimal("0.55"), BigDecimal(
        "0.75"
      )),
      SimbenchTimeUtil
        .toZonedDateTime("01.01.1990 00:30") -> (BigDecimal("0.35"), BigDecimal(
        "0.65"
      )),
      SimbenchTimeUtil
        .toZonedDateTime("01.01.1990 00:45") -> (BigDecimal("0.15"), BigDecimal(
        "0.55"
      ))
    )
  )

  private val pProfile: PowerPlantProfile = PowerPlantProfile(
    "test profile",
    PowerPlantProfileType.PowerPlantProfile1,
    Map(
      SimbenchTimeUtil.toZonedDateTime("01.01.1990 00:00") -> BigDecimal(
        "0.75"
      ),
      SimbenchTimeUtil.toZonedDateTime("01.01.1990 00:15") -> BigDecimal(
        "0.55"
      ),
      SimbenchTimeUtil.toZonedDateTime("01.01.1990 00:30") -> BigDecimal(
        "0.35"
      ),
      SimbenchTimeUtil.toZonedDateTime("01.01.1990 00:45") -> BigDecimal(
        "0.15"
      )
    )
  )

  "The power profile converter" should {
    "correctly convert an apparent power time series" in {
      val pRated = Quantities.getQuantity(100d, KILOWATT)
      val qRated = Quantities.getQuantity(10d, KILOVAR)

      val expected = Map(
        SimbenchTimeUtil.toZonedDateTime("01.01.1990 00:00") -> new SValue(
          Quantities.getQuantity(75d, KILOWATT),
          Quantities.getQuantity(8.5, KILOVAR)
        ),
        SimbenchTimeUtil.toZonedDateTime("01.01.1990 00:15") -> new SValue(
          Quantities.getQuantity(55d, KILOWATT),
          Quantities.getQuantity(7.5, KILOVAR)
        ),
        SimbenchTimeUtil.toZonedDateTime("01.01.1990 00:30") -> new SValue(
          Quantities.getQuantity(35d, KILOWATT),
          Quantities.getQuantity(6.5, KILOVAR)
        ),
        SimbenchTimeUtil.toZonedDateTime("01.01.1990 00:45") -> new SValue(
          Quantities.getQuantity(15d, KILOWATT),
          Quantities.getQuantity(5.5, KILOVAR)
        )
      )

      val actual = PowerProfileConverter.convert(sProfile, pRated, qRated)

      expected.foreach(
        entry =>
          abs(
            actual
              .getValue(entry._1)
              .toScala match {
              case Some(value) =>
                value.getP
                  .subtract(entry._2.getP)
                  .to(KILOWATT)
                  .getValue
                  .doubleValue()
              case None =>
                throw ConversionException(
                  s"Cannot find a time series entry for ${entry._1}"
                )
            }
          ) < testingTolerance shouldBe abs(
            actual
              .getValue(entry._1)
              .toScala match {
              case Some(value) =>
                value.getQ
                  .subtract(entry._2.getQ)
                  .to(KILOVAR)
                  .getValue
                  .doubleValue()
              case None =>
                throw ConversionException(
                  s"Cannot find a time series entry for ${entry._1}"
                )
            }
          ) < testingTolerance
      )
    }
  }

  "correctly convert an active power time series" in {
    val pRated = Quantities.getQuantity(100d, KILOWATT)

    val expected = Map(
      SimbenchTimeUtil.toZonedDateTime("01.01.1990 00:00") -> new PValue(
        Quantities.getQuantity(75d, KILOWATT)
      ),
      SimbenchTimeUtil.toZonedDateTime("01.01.1990 00:15") -> new PValue(
        Quantities.getQuantity(55d, KILOWATT)
      ),
      SimbenchTimeUtil.toZonedDateTime("01.01.1990 00:30") -> new PValue(
        Quantities.getQuantity(35d, KILOWATT)
      ),
      SimbenchTimeUtil.toZonedDateTime("01.01.1990 00:45") -> new PValue(
        Quantities.getQuantity(15d, KILOWATT)
      )
    )

    val actual = PowerProfileConverter.convert(pProfile, pRated)

    expected.foreach(
      entry =>
        abs(
          actual
            .getValue(entry._1)
            .toScala match {
            case Some(value) =>
              value.getP
                .subtract(entry._2.getP)
                .to(KILOWATT)
                .getValue
                .doubleValue()
            case None =>
              throw ConversionException(
                s"Cannot find a time series entry for ${entry._1}"
              )
          }
        ) < testingTolerance
    )
  }
}
