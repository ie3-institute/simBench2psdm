package edu.ie3.simbench.convert

import edu.ie3.simbench.model.datamodel.profiles.{
  PowerPlantProfile,
  PowerPlantProfileType
}
import edu.ie3.test.common.{ConverterTestData, UnitSpec}

class PowerPlantConverterSpec extends UnitSpec with ConverterTestData {
  "The power plant converter" should {
    "convert a power plant without reactive power information correctly" in {
      val (input, expected) = getPowerPlantPair("EHV Gen 1")
      val node = getNodePair("EHV Bus 177")._2
      val pProfile: PowerPlantProfile = PowerPlantProfile(
        "test profile",
        PowerPlantProfileType.PowerPlantProfile1,
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
      val (actual, _) = PowerPlantConverter.convert(input, node, pProfile)
      actual.getId shouldBe expected.getId
      actual.getNode shouldBe expected.getNode
      actual.getOperator shouldBe expected.getOperator
      actual.getqCharacteristics shouldBe expected.getqCharacteristics
      actual.getsRated shouldBe expected.getsRated
      actual.getCosPhiRated shouldBe expected.getCosPhiRated
    }

    "convert a power plant with reactive power information correctly" in {
      val (input, expected) = getPowerPlantPair("EHV Gen 1_withQ")
      val node = getNodePair("EHV Bus 177")._2
      val pProfile: PowerPlantProfile = PowerPlantProfile(
        "test profile",
        PowerPlantProfileType.PowerPlantProfile1,
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
      val (actual, _) = PowerPlantConverter.convert(input, node, pProfile)
      actual.getId shouldBe expected.getId
      actual.getNode shouldBe expected.getNode
      actual.getOperator shouldBe expected.getOperator
      actual.getqCharacteristics shouldBe expected.getqCharacteristics
      actual.getsRated shouldBe expected.getsRated
      actual.getCosPhiRated shouldBe expected.getCosPhiRated
    }
  }
}
