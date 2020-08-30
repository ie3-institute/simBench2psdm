/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.simbench.model.profile

import edu.ie3.simbench.exception.io.SimbenchDataModelException
import edu.ie3.simbench.model.RawModelData
import edu.ie3.simbench.model.datamodel.profiles.{ResProfile, ResProfileType}
import edu.ie3.test.common.UnitSpec
import edu.ie3.util.TimeUtil

class ResProfileSpec extends UnitSpec {
  val rawData = Vector(
    RawModelData(
      classOf[ResProfile],
      Map(
        "time" -> "02.01.2016 10:15",
        "PV6" -> "0.078013",
        "PV8" -> "0.0550869"
      )
    ),
    RawModelData(
      classOf[ResProfile],
      Map(
        "time" -> "02.01.2016 10:30",
        "PV6" -> "0.0770834",
        "PV8" -> "0.055754"
      )
    )
  )

  val expected = Vector(
    ResProfile(
      "PV6",
      ResProfileType.PV6,
      Map(
        TimeUtil.withDefaults
          .toZonedDateTime("2016-01-02 10:15:00") -> 0.078013,
        TimeUtil.withDefaults
          .toZonedDateTime("2016-01-02 10:30:00") -> 0.0770834
      )
    ),
    ResProfile(
      "PV8",
      ResProfileType.PV8,
      Map(
        TimeUtil.withDefaults
          .toZonedDateTime("2016-01-02 10:15:00") -> 0.0550869,
        TimeUtil.withDefaults
          .toZonedDateTime("2016-01-02 10:30:00") -> 0.055754
      )
    )
  )

  "The RES profile object" should {
    "throw an exception, when the basic single model creation method is called" in {
      val thrown =
        intercept[SimbenchDataModelException](ResProfile.apply(rawData(0)))
      thrown.getMessage shouldBe "No basic implementation of model creation available for profiles"
    }

    "build a correct vector of models" in {
      val actual =
        ResProfile.buildModels(rawData)
      actual.toSet shouldBe expected.toSet
    }
  }
}
