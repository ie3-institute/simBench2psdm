/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.simbench.model

import edu.ie3.simbench.model.datamodel.StudyCase
import edu.ie3.test.common.UnitSpec

class StudyCaseSpec extends UnitSpec {
  val rawData = Vector(
    RawModelData(
      classOf[StudyCase],
      Map(
        "Study Case" -> "hW",
        "pload" -> "1",
        "qload" -> "1",
        "Wind_p" -> "1",
        "PV_p" -> "0.8",
        "RES_p" -> "1",
        "Slack_vm" -> "0.965"
      )
    ),
    RawModelData(
      classOf[StudyCase],
      Map(
        "Study Case" -> "hPV",
        "pload" -> "1",
        "qload" -> "1",
        "Wind_p" -> "0.85",
        "PV_p" -> "0.95",
        "RES_p" -> "1",
        "Slack_vm" -> "0.965"
      )
    )
  )

  val expected = Vector(
    StudyCase(
      "hW",
      1,
      1,
      1,
      0.8,
      1,
      0.965
    ),
    StudyCase(
      "hPV",
      1,
      1,
      0.85,
      0.95,
      1,
      0.965
    )
  )

  "The study case object" should {
    "build the correct single model" in {
      val actual = StudyCase.apply(rawData(0))
      actual shouldBe expected(0)
    }

    "build a correct vector of models" in {
      val actual =
        StudyCase.buildModels(rawData)
      actual shouldBe expected
    }
  }
}
