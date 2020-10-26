package edu.ie3.simbench.convert.profiles

import edu.ie3.datamodel.models.value.{PValue, SValue}
import edu.ie3.simbench.exception.ConversionException
import edu.ie3.simbench.model.datamodel.profiles.{
  LoadProfile,
  LoadProfileType,
  PowerPlantProfile,
  PowerPlantProfileType
}
import edu.ie3.test.common.UnitSpec
import edu.ie3.util.TimeUtil
import edu.ie3.util.quantities.PowerSystemUnits.{KILOVAR, KILOWATT}
import tech.units.indriya.quantity.Quantities

import scala.jdk.OptionConverters._
import scala.math.abs

class PowerProfileConverterSpec extends UnitSpec {
  private val testingTolerance = 1e-6

  private val sProfile: LoadProfile = LoadProfile(
    "test profile",
    LoadProfileType.H0L,
    Map(
      TimeUtil.withDefaults
        .toZonedDateTime("1990-01-01 00:00:00") -> (BigDecimal(
        "0.75"
      ), BigDecimal(
        "0.85"
      )),
      TimeUtil.withDefaults
        .toZonedDateTime("1990-01-01 00:15:00") -> (BigDecimal(
        "0.55"
      ), BigDecimal(
        "0.75"
      )),
      TimeUtil.withDefaults
        .toZonedDateTime("1990-01-01 00:30:00") -> (BigDecimal(
        "0.35"
      ), BigDecimal(
        "0.65"
      )),
      TimeUtil.withDefaults
        .toZonedDateTime("1990-01-01 00:45:00") -> (BigDecimal(
        "0.15"
      ), BigDecimal(
        "0.55"
      ))
    )
  )

  private val pProfile: PowerPlantProfile = PowerPlantProfile(
    "test profile",
    PowerPlantProfileType.PowerPlantProfile1,
    Map(
      TimeUtil.withDefaults
        .toZonedDateTime("1990-01-01 00:00:00") -> BigDecimal(
        "0.75"
      ),
      TimeUtil.withDefaults
        .toZonedDateTime("1990-01-01 00:15:00") -> BigDecimal(
        "0.55"
      ),
      TimeUtil.withDefaults
        .toZonedDateTime("1990-01-01 00:30:00") -> BigDecimal(
        "0.35"
      ),
      TimeUtil.withDefaults
        .toZonedDateTime("1990-01-01 00:45:00") -> BigDecimal(
        "0.15"
      )
    )
  )

  "The power profile converter" should {
    "correctly convert an apparent power time series" in {
      val pRated = Quantities.getQuantity(100d, KILOWATT)
      val qRated = Quantities.getQuantity(10d, KILOVAR)

      val expected = Map(
        TimeUtil.withDefaults
          .toZonedDateTime("1990-01-01 00:00:00") -> new SValue(
          Quantities.getQuantity(75d, KILOWATT),
          Quantities.getQuantity(8.5, KILOVAR)
        ),
        TimeUtil.withDefaults
          .toZonedDateTime("1990-01-01 00:15:00") -> new SValue(
          Quantities.getQuantity(55d, KILOWATT),
          Quantities.getQuantity(7.5, KILOVAR)
        ),
        TimeUtil.withDefaults
          .toZonedDateTime("1990-01-01 00:30:00") -> new SValue(
          Quantities.getQuantity(35d, KILOWATT),
          Quantities.getQuantity(6.5, KILOVAR)
        ),
        TimeUtil.withDefaults
          .toZonedDateTime("1990-01-01 00:45:00") -> new SValue(
          Quantities.getQuantity(15d, KILOWATT),
          Quantities.getQuantity(5.5, KILOVAR)
        )
      )

      val actual = PowerProfileConverter.convert(sProfile, pRated, qRated)

      expected.foreach {
        case (time, sValue) =>
          abs(
            actual
              .getValue(time)
              .toScala match {
              case Some(value) =>
                (value.getP.toScala, sValue.getP.toScala) match {
                  case (Some(lhs), Some(rhs)) =>
                    lhs
                      .subtract(rhs)
                      .to(KILOWATT)
                      .getValue
                      .doubleValue()
                  case _ =>
                    throw ConversionException(
                      s"Unable to calculate difference between expected und actual time series, as one of both entries is not available for time $time"
                    )
                }

              case None =>
                throw ConversionException(
                  s"Cannot find a time series entry for $time"
                )
            }
          ) < testingTolerance shouldBe abs(
            actual
              .getValue(time)
              .toScala match {
              case Some(value) =>
                (value.getQ.toScala, sValue.getQ.toScala) match {
                  case (Some(lhs), Some(rhs)) =>
                    lhs
                      .subtract(rhs)
                      .to(KILOVAR)
                      .getValue
                      .doubleValue()
                }
              case None =>
                throw ConversionException(
                  s"Cannot find a time series entry for ${time}"
                )
            }
          ) < testingTolerance
      }
    }
  }

  "correctly convert an active power time series" in {
    val pRated = Quantities.getQuantity(100d, KILOWATT)

    val expected = Map(
      TimeUtil.withDefaults
        .toZonedDateTime("1990-01-01 00:00:00") -> new PValue(
        Quantities.getQuantity(75d, KILOWATT)
      ),
      TimeUtil.withDefaults
        .toZonedDateTime("1990-01-01 00:15:00") -> new PValue(
        Quantities.getQuantity(55d, KILOWATT)
      ),
      TimeUtil.withDefaults
        .toZonedDateTime("1990-01-01 00:30:00") -> new PValue(
        Quantities.getQuantity(35d, KILOWATT)
      ),
      TimeUtil.withDefaults
        .toZonedDateTime("1990-01-01 00:45:00") -> new PValue(
        Quantities.getQuantity(15d, KILOWATT)
      )
    )

    val actual = PowerProfileConverter.convert(pProfile, pRated)

    expected.foreach {
      case (time, pValue) =>
        abs(
          actual
            .getValue(time)
            .toScala match {
            case Some(value) =>
              (value.getP.toScala, pValue.getP.toScala) match {
                case (Some(lhs), Some(rhs)) =>
                  lhs
                    .subtract(rhs)
                    .to(KILOWATT)
                    .getValue
                    .doubleValue()
                case _ =>
                  throw ConversionException(
                    s"Unable to calculate difference between expected und actual time series, as one of both entries is not available for time $time"
                  )
              }
            case None =>
              throw ConversionException(
                s"Cannot find a time series entry for $time"
              )
          }
        ) < testingTolerance
    }
  }
}
