/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.simbench.model

import edu.ie3.simbench.exception.CodeValidationException
import edu.ie3.test.common.UnitSpec

import scala.util.{Failure, Success}

class SimbenchCodeSpec extends UnitSpec {
  "A SimbenchCode validation" should {
    "fail on an invalid code" in {
      val invalid = SimbenchCode("bla")

      invalid match {
        case Failure(exception: CodeValidationException) =>
          exception.getMessage shouldBe "bla is no valid SimbenchCode"
        case _ => fail("Checking simbench code bla did not fail properly.")
      }
    }

    "return a valid case class instance on a valid code" in {
      val validCode = "1-MVLV-urban-6.309-1-sw"
      val valid = SimbenchCode(validCode)

      valid match {
        case Success(value) => value.code shouldBe validCode
        case _ =>
          fail(
            s"Checking simbench code $validCode did not result in a valid instance."
          )
      }
    }
  }
}
