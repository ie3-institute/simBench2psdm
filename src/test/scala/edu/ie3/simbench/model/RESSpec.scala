package edu.ie3.simbench.model

import edu.ie3.simbench.exception.io.SimbenchDataModelException
import edu.ie3.simbench.model.datamodel.RES
import edu.ie3.simbench.model.datamodel.enums.{CalculationType, ResType}
import edu.ie3.simbench.model.datamodel.profiles.ResProfileType
import edu.ie3.test.common.{ConverterTestData, UnitSpec}

class RESSpec extends UnitSpec with ConverterTestData {
  val nodeMapping = Map(
    "LV1.101 Bus 4" -> getNodePair("LV1.101 Bus 4")._1
  )

  val rawData = Vector(
    RawModelData(
      classOf[RES],
      Map(
        "id" -> "LV1.101 SGen 3",
        "node" -> "LV1.101 Bus 4",
        "type" -> "PV",
        "profile" -> "PV5",
        "calc_type" -> "pq",
        "pRES" -> "0.023",
        "qRES" -> "0",
        "sR" -> "0.023",
        "subnet" -> "LV1.101",
        "voltLvl" -> "7"
      )
    ),
    RawModelData(
      classOf[RES],
      Map(
        "id" -> "LV1.101 SGen 4",
        "node" -> "LV1.101 Bus 4",
        "type" -> "WindMV",
        "profile" -> "WP4",
        "calc_type" -> "vavm",
        "pRES" -> "0.023",
        "qRES" -> "0",
        "sR" -> "0.023",
        "subnet" -> "LV1.101",
        "voltLvl" -> "7"
      )
    )
  )

  val expected = Vector(
    RES(
      "LV1.101 SGen 3",
      getNodePair("LV1.101 Bus 4")._1,
      ResType.PV,
      ResProfileType.PV5,
      CalculationType.PQ,
      BigDecimal("0.023"),
      BigDecimal("0"),
      BigDecimal("0.023"),
      "LV1.101",
      7
    ),
    RES(
      "LV1.101 SGen 4",
      getNodePair("LV1.101 Bus 4")._1,
      ResType.WindMv,
      ResProfileType.WP4,
      CalculationType.VaVm,
      BigDecimal("0.023"),
      BigDecimal("0"),
      BigDecimal("0.023"),
      "LV1.101",
      7
    )
  )

  "The RES object" should {
    "throw an exception, when the basic single model creation method is called" in {
      val thrown =
        intercept[SimbenchDataModelException](RES.apply(rawData(0)))
      thrown.getMessage shouldBe "No basic implementation of model creation available for RES$"
    }

    "throw an exception, when the basic batch model creation method is called" in {
      val thrown =
        intercept[SimbenchDataModelException](RES.buildModels(rawData))
      thrown.getMessage shouldBe "No basic implementation of model creation available for RES$"
    }

    "build the correct single model" in {
      val actual = RES.buildModel(
        rawData(0),
        nodeMapping.getOrElse(
          "LV1.101 Bus 4",
          throw SimbenchDataModelException(
            "Ooops. This is not supposed to happen"
          )
        )
      )
      actual shouldBe expected(0)
    }

    "build a correct vector of models" in {
      val actual =
        RES.buildModels(rawData, nodeMapping)
      actual shouldBe expected
    }
  }
}
