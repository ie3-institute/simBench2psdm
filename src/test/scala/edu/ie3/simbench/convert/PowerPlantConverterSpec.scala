/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
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
          simbenchTimeUtil.toZonedDateTime("01.01.1990 00:00") -> 0.75,
          simbenchTimeUtil.toZonedDateTime("01.01.1990 00:15") -> 0.55,
          simbenchTimeUtil.toZonedDateTime("01.01.1990 00:30") -> 0.35,
          simbenchTimeUtil.toZonedDateTime("01.01.1990 00:45") -> 0.15
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
          simbenchTimeUtil.toZonedDateTime("01.01.1990 00:00") -> 0.75,
          simbenchTimeUtil.toZonedDateTime("01.01.1990 00:15") -> 0.55,
          simbenchTimeUtil.toZonedDateTime("01.01.1990 00:30") -> 0.35,
          simbenchTimeUtil.toZonedDateTime("01.01.1990 00:45") -> 0.15
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
