package edu.ie3.simbench.convert

import edu.ie3.test.common.UnitSpec

class SubnetConverterSpec extends UnitSpec {
  "The subnet converter" should {
    val simbenchSubnets = Vector("d", "a", "e", "c", "b")
    val converter = SubnetConverter(simbenchSubnets)

    "be able to provide the correct mapping with valid input" in {
      val query = Vector("a", "b", "c", "d", "e")

      val expectedMapping =
        Map("a" -> 1, "b" -> 2, "c" -> 3, "d" -> 4, "e" -> 5)

      val actual = query
        .map(subnetString => subnetString -> converter.convert(subnetString))
        .toMap
      actual shouldBe expectedMapping
    }

    "throw an exception, if the desired subnet string has not been initialized" in {
      val thrown = intercept[IllegalArgumentException](
        converter.convert("totally random String")
      )
      thrown.getMessage shouldBe "The simbench subnet totally random String has not been initialized with the converter."
    }
  }
}
