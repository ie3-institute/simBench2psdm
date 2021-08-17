package edu.ie3.simbench.convert

import edu.ie3.datamodel.models.StandardUnits

import java.util.Objects
import edu.ie3.simbench.model.datamodel.profiles.{ResProfile, ResProfileType}
import edu.ie3.test.common.{ConverterTestData, UnitSpec}
import edu.ie3.test.matchers.QuantityMatchers
import tech.units.indriya.quantity.Quantities

import scala.jdk.OptionConverters.RichOptional

class ResConverterSpec
    extends UnitSpec
    with QuantityMatchers
    with ConverterTestData {
  implicit val quantityMatchingTolerance: Double = 1e-4

  "The RES converter" should {
    val (_, node) = getNodePair("MV1.101 Bus 4")
    val (input, expected) = getResPair("MV1.101 SGen 2")
    val pProfile: ResProfile = ResProfile(
      "test profile",
      ResProfileType.LvRural1,
      Map(
        simbenchTimeUtil.toZonedDateTime("01.01.1990 00:00") -> BigDecimal(
          "0.75"
        ),
        simbenchTimeUtil.toZonedDateTime("01.01.1990 00:15") -> BigDecimal(
          "0.55"
        ),
        simbenchTimeUtil.toZonedDateTime("01.01.1990 00:30") -> BigDecimal(
          "0.35"
        ),
        simbenchTimeUtil.toZonedDateTime("01.01.1990 00:45") -> BigDecimal(
          "0.15"
        )
      )
    )
    val (actual, actualTimeSeries) = ResConverter.convert(input, node, pProfile)

    "bring up the correct input model" in {
      Objects.nonNull(actual.getUuid) shouldBe true
      actual.getId shouldBe expected.getId
      actual.getOperator shouldBe expected.getOperator
      actual.getOperationTime shouldBe expected.getOperationTime
      actual.getNode shouldBe expected.getNode
      actual.getqCharacteristics shouldBe expected.getqCharacteristics
      actual.getsRated shouldBe expected.getsRated
      actual.getCosPhiRated shouldBe expected.getCosPhiRated
    }

    "lead to the correct time series" in {
      val expected = Map(
        simbenchTimeUtil.toZonedDateTime("01.01.1990 00:00") -> Quantities
          .getQuantity(-0.12, StandardUnits.ACTIVE_POWER_RESULT),
        simbenchTimeUtil.toZonedDateTime("01.01.1990 00:15") -> Quantities
          .getQuantity(-0.088, StandardUnits.ACTIVE_POWER_RESULT),
        simbenchTimeUtil.toZonedDateTime("01.01.1990 00:30") -> Quantities
          .getQuantity(-0.056, StandardUnits.ACTIVE_POWER_RESULT),
        simbenchTimeUtil.toZonedDateTime("01.01.1990 00:45") -> Quantities
          .getQuantity(-0.024, StandardUnits.ACTIVE_POWER_RESULT)
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
}
