package edu.ie3.simbench.config

import edu.ie3.simbench.config.SimbenchConfig.Io.Input
import edu.ie3.simbench.exception.CodeValidationException
import edu.ie3.simbench.exception.io.SimbenchConfigException
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

    "throw a SimbenchConfigException if no source is defined" in {
      val method = PrivateMethod[Unit](Symbol("checkFileSource"))
      val config = validIoInputConfig.copy(download = None)

      intercept[SimbenchConfigException] {
        ConfigValidator invokePrivate method(config)
      } match {
        case correctException: SimbenchConfigException =>
          correctException.getMessage shouldBe s"No file sources defined in: $config"
        case _ =>
          fail("The validation did not fail properly.")
      }
    }

    "throw a SimbenchConfigException if too many sources are defined" in {
      val method = PrivateMethod[Unit](Symbol("checkFileSource"))
      val config = validIoInputConfig.copy(localFile =
        Some(
          new Input.LocalFile(
            failOnExistingFiles = false,
            isZipped = true
          )
        )
      )

      intercept[SimbenchConfigException] {
        ConfigValidator invokePrivate method(config)
      } match {
        case correctException: SimbenchConfigException =>
          correctException.getMessage shouldBe s"Too many file sources (2) defined in: $config"
        case _ =>
          fail("The validation did not fail properly.")
      }
    }
  }
}
