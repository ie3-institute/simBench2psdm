package edu.ie3.simbench.model.profile

import edu.ie3.simbench.exception.io.SimbenchDataModelException
import edu.ie3.simbench.model.RawModelData
import edu.ie3.simbench.model.datamodel.profiles.LoadProfile
import edu.ie3.simbench.model.datamodel.profiles.LoadProfileType.{
  H0A,
  H0B,
  H0C,
  L1A,
  L2A
}
import edu.ie3.test.common.UnitSpec
import edu.ie3.util.TimeTools

class LoadProfileSpec extends UnitSpec {
  TimeTools.initialize(
    TimeTools.DEFAULT_ZONE_ID,
    TimeTools.DEFAULT_LOCALE,
    "dd.MM.yyyy HH:mm"
  )

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
        TimeTools.toZonedDateTime("01.01.2016 00:00") -> (BigDecimal(
          "0.276685"
        ), BigDecimal("-0.067519")),
        TimeTools.toZonedDateTime("01.01.2016 00:15") -> (BigDecimal(
          "0.066011"
        ), BigDecimal("0.060412"))
      )
    ),
    LoadProfile(
      "H0B",
      H0B,
      Map(
        TimeTools.toZonedDateTime("01.01.2016 00:00") -> (BigDecimal(
          "0.065826"
        ), BigDecimal("-0.014175")),
        TimeTools.toZonedDateTime("01.01.2016 00:15") -> (BigDecimal(
          "0.190476"
        ), BigDecimal("0.212622"))
      )
    ),
    LoadProfile(
      "H0C",
      H0C,
      Map(
        TimeTools.toZonedDateTime("01.01.2016 00:00") -> (BigDecimal(
          "0.237082"
        ), BigDecimal("0.242253")),
        TimeTools.toZonedDateTime("01.01.2016 00:15") -> (BigDecimal(
          "0.066869"
        ), BigDecimal("0.007691"))
      )
    ),
    LoadProfile(
      "L1A",
      L1A,
      Map(
        TimeTools.toZonedDateTime("01.01.2016 00:00") -> (BigDecimal(
          "0.144231"
        ), BigDecimal("0.125501")),
        TimeTools.toZonedDateTime("01.01.2016 00:15") -> (BigDecimal(
          "0.416628"
        ), BigDecimal("0.587453"))
      )
    ),
    LoadProfile(
      "L2A",
      L2A,
      Map(
        TimeTools.toZonedDateTime("01.01.2016 00:00") -> (BigDecimal(
          "0.321053"
        ), BigDecimal("0.326352")),
        TimeTools.toZonedDateTime("01.01.2016 00:15") -> (BigDecimal(
          "0.317544"
        ), BigDecimal("0.346238"))
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
