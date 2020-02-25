package edu.ie3.simbench.convert

import edu.ie3.models.GermanVoltageLevel._
import edu.ie3.simbench.model.GermanTransformationVoltageLevel.{
  EHV2HV,
  HV2MV,
  MV2LV
}
import edu.ie3.test.common.UnitSpec

class VoltLvlConverterSpec extends UnitSpec {
  "The voltage level converter" should {
    "be able to provide the correct mapping with valid input" in {
      val query = Vector(1, 2, 3, 4, 5, 6, 7)

      val expectedMapping =
        Map(1 -> EHV,
            2 -> EHV2HV,
            3 -> HV,
            4 -> HV2MV,
            5 -> MV,
            6 -> MV2LV,
            7 -> LV)

      val actual = query
        .map(voltLvlId => voltLvlId -> VoltLvlConverter.convert(voltLvlId))
        .toMap
      actual shouldBe expectedMapping
    }

    "throw an exception, if the desired subnet string has not been initialized" in {
      val thrown =
        intercept[IllegalArgumentException](VoltLvlConverter.convert(8))
      thrown.getMessage shouldBe "The desired voltage level id 8 is not covered by the commonly known voltage levels."
    }
  }
}
