package edu.ie3.simbench.convert

import edu.ie3.datamodel.exceptions.VoltageLevelException
import edu.ie3.datamodel.models.voltagelevels.GermanVoltageLevelUtils
import edu.ie3.test.common.UnitSpec
import edu.ie3.util.quantities.PowerSystemUnits.KILOVOLT
import tec.uom.se.quantity.Quantities

class VoltLvlConverterSpec extends UnitSpec {
  "The voltage level converter" should {
    "be able to provide the correct mapping with valid input" in {
      val actual2expected = Map(
        (1, Quantities.getQuantity(380d, KILOVOLT)) -> GermanVoltageLevelUtils.EHV_380KV,
        (1, Quantities.getQuantity(220d, KILOVOLT)) -> GermanVoltageLevelUtils.EHV_220KV,
        (3, Quantities.getQuantity(110d, KILOVOLT)) -> GermanVoltageLevelUtils.HV,
        (5, Quantities.getQuantity(30d, KILOVOLT)) -> GermanVoltageLevelUtils.MV_30KV,
        (5, Quantities.getQuantity(20d, KILOVOLT)) -> GermanVoltageLevelUtils.MV_20KV,
        (5, Quantities.getQuantity(10d, KILOVOLT)) -> GermanVoltageLevelUtils.MV_10KV,
        (7, Quantities.getQuantity(0.4, KILOVOLT)) -> GermanVoltageLevelUtils.LV
      )

      actual2expected.map(entry => {
        val idx = entry._1._1
        val vRated = entry._1._2
        val actual = VoltLvlConverter.convert(idx, vRated)
        entry._1 -> actual
      }) shouldBe actual2expected
    }

    "throw exceptions, when the voltage level is not a known german one" in {
      val actual2expected = Map(
        (2, Quantities.getQuantity(295d, KILOVOLT)) -> "The id ehv2hv in combination with the rated voltage 295.0 kV is not covered by none of the commonly known german voltage levels.",
        (4, Quantities.getQuantity(60d, KILOVOLT)) -> "The id hv2mv in combination with the rated voltage 60.0 kV is not covered by none of the commonly known german voltage levels.",
        (6, Quantities.getQuantity(5.2, KILOVOLT)) -> "The id mv2lv in combination with the rated voltage 5.2 kV is not covered by none of the commonly known german voltage levels."
      )

      actual2expected.foreach(entry => {
        val idx = entry._1._1
        val vRated = entry._1._2
        val expectedMessage = entry._2

        intercept[VoltageLevelException] {
          VoltLvlConverter.convert(idx, vRated)
        }.getMessage shouldBe expectedMessage
      })
    }

    "throw an exception, if the desired subnet string has not been initialized" in {
      val thrown =
        intercept[IllegalArgumentException](
          VoltLvlConverter.convert(8, Quantities.getQuantity(0d, KILOVOLT))
        )
      thrown.getMessage shouldBe "The desired voltage level id 8 is not covered by the commonly known voltage levels."
    }
  }
}
