package edu.ie3.simbench.convert

import edu.ie3.test.common.UnitSpec
import org.scalatest.prop.TableDrivenPropertyChecks

class SubnetConverterSpec extends UnitSpec with TableDrivenPropertyChecks {
  "The subnet converter" should {
    val ratedVoltageIdPairs = Vector(
      (BigDecimal("0.4"), "LV"),
      (BigDecimal("380"), "EHV"),
      (BigDecimal("20"), "MV2"),
      (BigDecimal("20"), "MV1"),
      (BigDecimal("10"), "MV"),
      (BigDecimal("0.4"), "LV1"),
      (BigDecimal("20"), "MV1_LV1")
    )
    val converter = SubnetConverter(ratedVoltageIdPairs)

    "be able to provide the correct mapping with valid input" in {
      val expectedMapping =
        Map(
          (BigDecimal("380"), "EHV") -> 1,
          (BigDecimal("20"), "MV1") -> 2,
          (BigDecimal("20"), "MV2") -> 3,
          (BigDecimal("10"), "MV") -> 4,
          (BigDecimal("0.4"), "LV") -> 5,
          (BigDecimal("0.4"), "LV1") -> 6
        )

      val actual = converter.mapping
      actual shouldBe expectedMapping
    }

    "throw an exception, if the desired subnet identifier has not been initialized" in {
      val thrown = intercept[IllegalArgumentException](
        converter.convert(BigDecimal("110"), "totally random String")
      )
      thrown.getMessage shouldBe "The SimBench subnet 'totally random String' with rated voltage 110 kV has not been " +
        "initialized with the converter."
    }

    "be able to provide the correct converted subnet id" in {
      val testData = Table(
        ("ratedVoltage", "id", "expected"),
        (BigDecimal("380"), "EHV", 1),
        (BigDecimal("20"), "MV1", 2),
        (BigDecimal("0.4"), "LV1", 6),
        (BigDecimal("20"), "MV1_LV1", 2),
        (BigDecimal("0.4"), "MV1_LV1", 6)
      )

      forAll(testData) {
        (ratedVoltage: BigDecimal, id: String, expected: Int) =>
          converter.convert(ratedVoltage, id) shouldBe expected
      }
    }
  }
}
