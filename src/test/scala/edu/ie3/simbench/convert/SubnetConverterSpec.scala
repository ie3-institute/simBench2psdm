/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.simbench.convert

import edu.ie3.test.common.UnitSpec
import org.scalatest.prop.TableDrivenPropertyChecks

class SubnetConverterSpec extends UnitSpec with TableDrivenPropertyChecks {
  "The subnet converter" should {
    val ratedVoltageIdPairs = Vector(
      (0.4, "LV"),
      (380d, "EHV"),
      (20d, "MV2"),
      (20d, "MV1"),
      (10d, "MV"),
      (0.4, "LV1"),
      (20d, "MV1_LV1")
    )
    val converter = SubnetConverter(ratedVoltageIdPairs)

    "be able to provide the correct mapping with valid input" in {
      val expectedMapping =
        Map(
          (380d, "EHV") -> 1,
          (20d, "MV1") -> 2,
          (20d, "MV2") -> 3,
          (10d, "MV") -> 4,
          (0.4, "LV") -> 5,
          (0.4, "LV1") -> 6
        )

      val actual = converter.mapping
      actual shouldBe expectedMapping
    }

    "throw an exception, if the desired subnet identifier has not been initialized" in {
      val thrown = intercept[IllegalArgumentException](
        converter.convert(110d, "totally random String")
      )
      thrown.getMessage shouldBe "The provided id 'totally random String' is no valid subnet identifier."
    }

    "be able to provide the correct converted subnet id" in {
      val testData = Table(
        ("ratedVoltage", "id", "expected"),
        (380d, "EHV", 1),
        (20d, "MV1", 2),
        (0.4, "LV1", 6),
        (20d, "MV1_LV1", 2),
        (0.4, "MV1_LV1", 6)
      )

      forAll(testData) { (ratedVoltage: Double, id: String, expected: Int) =>
        converter.convert(ratedVoltage, id) shouldBe expected
      }
    }
  }
}
