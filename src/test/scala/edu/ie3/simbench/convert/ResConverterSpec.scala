/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.simbench.convert

import java.util.Objects

import edu.ie3.simbench.model.datamodel.profiles.{ResProfile, ResProfileType}
import edu.ie3.test.common.{ConverterTestData, UnitSpec}

class ResConverterSpec extends UnitSpec with ConverterTestData {
  "The RES converter" should {
    "convert a valid RES correctly" in {
      val (_, node) = getNodePair("MV1.101 Bus 4")
      val (input, expected) = getResPair("MV1.101 SGen 2")
      val pProfile: ResProfile = ResProfile(
        "test profile",
        ResProfileType.LvRural1,
        Map(
          simbenchTimeUtil.toZonedDateTime("01.01.1990 00:00") -> 0.75,
          simbenchTimeUtil.toZonedDateTime("01.01.1990 00:15") -> 0.55,
          simbenchTimeUtil.toZonedDateTime("01.01.1990 00:30") -> 0.35,
          simbenchTimeUtil.toZonedDateTime("01.01.1990 00:45") -> 0.15
        )
      )

      val (actual, _) = ResConverter.convert(input, node, pProfile)
      Objects.nonNull(actual.getUuid) shouldBe true
      actual.getId shouldBe expected.getId
      actual.getOperator shouldBe expected.getOperator
      actual.getOperationTime shouldBe expected.getOperationTime
      actual.getNode shouldBe expected.getNode
      actual.getqCharacteristics shouldBe expected.getqCharacteristics
      actual.getsRated shouldBe expected.getsRated
      actual.getCosPhiRated shouldBe expected.getCosPhiRated
    }
  }
}
