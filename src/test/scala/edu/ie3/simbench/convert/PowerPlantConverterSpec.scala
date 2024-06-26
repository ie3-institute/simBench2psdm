package edu.ie3.simbench.convert

import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.simbench.model.datamodel.profiles.{
  PowerPlantProfile,
  PowerPlantProfileType
}
import edu.ie3.test.common.{ConverterTestData, TestTimeUtils, UnitSpec}
import edu.ie3.test.matchers.QuantityMatchers
import tech.units.indriya.quantity.Quantities

import scala.jdk.OptionConverters.RichOptional

class PowerPlantConverterSpec
    extends UnitSpec
    with QuantityMatchers
    with ConverterTestData {
  "The power plant converter" when {
    "converting a power plant without reactive power information" should {
      val (input, expected) = getPowerPlantPair("EHV Gen 1")
      val node = getNodePair("EHV Bus 177")._2
      val pProfile: PowerPlantProfile = PowerPlantProfile(
        "test profile",
        PowerPlantProfileType.PowerPlantProfile1,
        Map(
          TestTimeUtils.simbench.toZonedDateTime(
            "01.01.1990 00:00"
          ) -> BigDecimal(
            "0.75"
          ),
          TestTimeUtils.simbench.toZonedDateTime(
            "01.01.1990 00:15"
          ) -> BigDecimal(
            "0.55"
          ),
          TestTimeUtils.simbench.toZonedDateTime(
            "01.01.1990 00:30"
          ) -> BigDecimal(
            "0.35"
          ),
          TestTimeUtils.simbench.toZonedDateTime(
            "01.01.1990 00:45"
          ) -> BigDecimal(
            "0.15"
          )
        )
      )
      val (actual, actualTimeSeries) =
        PowerPlantConverter.convert(input, node, pProfile)

      "bring up the correct input model" in {
        actual.getId shouldBe expected.getId
        actual.getNode shouldBe expected.getNode
        actual.getOperator shouldBe expected.getOperator
        actual.getqCharacteristics shouldBe expected.getqCharacteristics
        actual.getsRated shouldBe expected.getsRated
        actual.getCosPhiRated shouldBe expected.getCosPhiRated
      }

      "lead to the correct time series" in {
        val expected = Map(
          TestTimeUtils.simbench.toZonedDateTime(
            "01.01.1990 00:00"
          ) -> Quantities
            .getQuantity(-222750.0, StandardUnits.ACTIVE_POWER_IN),
          TestTimeUtils.simbench.toZonedDateTime(
            "01.01.1990 00:15"
          ) -> Quantities
            .getQuantity(-163350.0, StandardUnits.ACTIVE_POWER_IN),
          TestTimeUtils.simbench.toZonedDateTime(
            "01.01.1990 00:30"
          ) -> Quantities
            .getQuantity(-103950.0, StandardUnits.ACTIVE_POWER_IN),
          TestTimeUtils.simbench.toZonedDateTime(
            "01.01.1990 00:45"
          ) -> Quantities
            .getQuantity(-44550.0, StandardUnits.ACTIVE_POWER_IN)
        )

        actualTimeSeries.getEntries.forEach { timeBasedValue =>
          val time = timeBasedValue.getTime
          val value = timeBasedValue.getValue

          expected.get(time) match {
            case Some(expectedValue) =>
              value.getP.toScala match {
                case Some(p) => p should equalWithTolerance(expectedValue)
                case None =>
                  fail(s"Unable to get expected active power for time '$time'")
              }
            case None =>
              fail(s"Unable to get expected time series entry for time '$time'")
          }
        }
      }
    }

    "converting a power plant with reactive power information" should {
      val (input, expected) = getPowerPlantPair("EHV Gen 1_withQ")
      val node = getNodePair("EHV Bus 177")._2
      val pProfile: PowerPlantProfile = PowerPlantProfile(
        "test profile",
        PowerPlantProfileType.PowerPlantProfile1,
        Map(
          TestTimeUtils.simbench.toZonedDateTime(
            "01.01.1990 00:00"
          ) -> BigDecimal(
            "0.75"
          ),
          TestTimeUtils.simbench.toZonedDateTime(
            "01.01.1990 00:15"
          ) -> BigDecimal(
            "0.55"
          ),
          TestTimeUtils.simbench.toZonedDateTime(
            "01.01.1990 00:30"
          ) -> BigDecimal(
            "0.35"
          ),
          TestTimeUtils.simbench.toZonedDateTime(
            "01.01.1990 00:45"
          ) -> BigDecimal(
            "0.15"
          )
        )
      )
      val (actual, actualTimeSeries) =
        PowerPlantConverter.convert(input, node, pProfile)

      "bring up the correct input model" in {
        actual.getId shouldBe expected.getId
        actual.getNode shouldBe expected.getNode
        actual.getOperator shouldBe expected.getOperator
        actual.getqCharacteristics shouldBe expected.getqCharacteristics
        actual.getsRated shouldBe expected.getsRated
        actual.getCosPhiRated shouldBe expected.getCosPhiRated
      }

      "lead to the correct time series" in {
        val expected = Map(
          TestTimeUtils.simbench.toZonedDateTime(
            "01.01.1990 00:00"
          ) -> Quantities
            .getQuantity(-222750.0, StandardUnits.ACTIVE_POWER_IN),
          TestTimeUtils.simbench.toZonedDateTime(
            "01.01.1990 00:15"
          ) -> Quantities
            .getQuantity(-163350.0, StandardUnits.ACTIVE_POWER_IN),
          TestTimeUtils.simbench.toZonedDateTime(
            "01.01.1990 00:30"
          ) -> Quantities
            .getQuantity(-103950.0, StandardUnits.ACTIVE_POWER_IN),
          TestTimeUtils.simbench.toZonedDateTime(
            "01.01.1990 00:45"
          ) -> Quantities
            .getQuantity(-44550.0, StandardUnits.ACTIVE_POWER_IN)
        )

        actualTimeSeries.getEntries.forEach { timeBasedValue =>
          val time = timeBasedValue.getTime
          val value = timeBasedValue.getValue

          expected.get(time) match {
            case Some(expectedP) =>
              value.getP.toScala match {
                case Some(p) => p should equalWithTolerance(expectedP)
                case None =>
                  fail(s"Unable to get expected active power for time '$time'")
              }
            case None =>
              fail(s"Unable to get expected time series entry for time '$time'")
          }
        }
      }
    }
  }
}
