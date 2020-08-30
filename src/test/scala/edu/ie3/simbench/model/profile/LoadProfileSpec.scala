/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.simbench.model.profile

import edu.ie3.simbench.exception.io.SimbenchDataModelException
import edu.ie3.simbench.model.RawModelData
import edu.ie3.simbench.model.datamodel.profiles.LoadProfile
import edu.ie3.simbench.model.datamodel.profiles.LoadProfileType._
import edu.ie3.test.common.UnitSpec
import edu.ie3.util.TimeUtil

class LoadProfileSpec extends UnitSpec {
  val rawData = Vector(
    RawModelData(
      classOf[LoadProfile],
      Map(
        "time" -> "01.01.2016 00:00",
        "H0-A_qload" -> "-0.067519",
        "H0-A_pload" -> "0.276685",
        "H0-B_qload" -> "-0.014175",
        "H0-B_pload" -> "0.065826",
        "H0-C_qload" -> "0.242253",
        "H0-C_pload" -> "0.237082",
        "L1-A_qload" -> "0.125501",
        "L1-A_pload" -> "0.144231",
        "L2-A_qload" -> "0.326352",
        "L2-A_pload" -> "0.321053"
      )
    ),
    RawModelData(
      classOf[LoadProfile],
      Map(
        "time" -> "01.01.2016 00:15",
        "H0-A_qload" -> "0.060412",
        "H0-A_pload" -> "0.066011",
        "H0-B_qload" -> "0.212622",
        "H0-B_pload" -> "0.190476",
        "H0-C_qload" -> "0.007691",
        "H0-C_pload" -> "0.066869",
        "L1-A_qload" -> "0.587453",
        "L1-A_pload" -> "0.416628",
        "L2-A_qload" -> "0.346238",
        "L2-A_pload" -> "0.317544"
      )
    )
  )

  val expected = Vector(
    LoadProfile(
      "H0A",
      H0A,
      Map(
        TimeUtil.withDefaults
          .toZonedDateTime("2016-01-01 00:00:00") -> (0.276685, -0.067519),
        TimeUtil.withDefaults
          .toZonedDateTime("2016-01-01 00:15:00") -> (0.066011, 0.060412)
      )
    ),
    LoadProfile(
      "H0B",
      H0B,
      Map(
        TimeUtil.withDefaults
          .toZonedDateTime("2016-01-01 00:00:00") -> (0.065826, -0.014175),
        TimeUtil.withDefaults
          .toZonedDateTime("2016-01-01 00:15:00") -> (0.190476, 0.212622)
      )
    ),
    LoadProfile(
      "H0C",
      H0C,
      Map(
        TimeUtil.withDefaults
          .toZonedDateTime("2016-01-01 00:00:00") -> (0.237082, 0.242253),
        TimeUtil.withDefaults
          .toZonedDateTime("2016-01-01 00:15:00") -> (0.066869, 0.007691)
      )
    ),
    LoadProfile(
      "L1A",
      L1A,
      Map(
        TimeUtil.withDefaults
          .toZonedDateTime("2016-01-01 00:00:00") -> (0.144231, 0.125501),
        TimeUtil.withDefaults
          .toZonedDateTime("2016-01-01 00:15:00") -> (0.416628, 0.587453)
      )
    ),
    LoadProfile(
      "L2A",
      L2A,
      Map(
        TimeUtil.withDefaults
          .toZonedDateTime("2016-01-01 00:00:00") -> (0.321053, 0.326352),
        TimeUtil.withDefaults
          .toZonedDateTime("2016-01-01 00:15:00") -> (0.317544, 0.346238)
      )
    )
  )

  "The load profile object" should {
    "throw an exception, when the basic single model creation method is called" in {
      val thrown =
        intercept[SimbenchDataModelException](LoadProfile.apply(rawData(0)))
      thrown.getMessage shouldBe "No basic implementation of model creation available for profiles"
    }

    "build a correct vector of models" in {
      val actual =
        LoadProfile.buildModels(rawData)
      actual.toSet shouldBe expected.toSet
    }
  }
}
