/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.simbench.config

import edu.ie3.simbench.exception.CodeValidationException
import edu.ie3.test.common.{ConfigTestData, UnitSpec}

class ConfigValidatorSpec extends UnitSpec with ConfigTestData {
  "The ConfigValidator" should {
    "recognise a correct config" in {
      ConfigValidator.checkValidity(validConfig)
    }

    "recognise a correct IO config" in {
      val method = PrivateMethod[Unit](Symbol("checkValidity"))
      ConfigValidator invokePrivate method(validIo)
    }

    "throw an CodeValidationException on an io config with invalid SimBench codes" in {
      val method = PrivateMethod[Unit](Symbol("checkValidity"))
      intercept[CodeValidationException] {
        ConfigValidator invokePrivate method(ioWithInvalidSimbenchCode)
      } match {
        case correctException: CodeValidationException =>
          correctException.getMessage shouldBe "blabla is no valid SimbenchCode"
        case _ =>
          fail(
            "The validation of an IO config with invalid SimbenchCode did not fail properly."
          )
      }
    }
  }
}
