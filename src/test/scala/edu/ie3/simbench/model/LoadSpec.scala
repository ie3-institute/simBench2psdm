package edu.ie3.simbench.model

import edu.ie3.simbench.exception.io.SimbenchDataModelException
import edu.ie3.simbench.model.datamodel.profiles.LoadProfileType
import edu.ie3.simbench.model.datamodel.Load
import edu.ie3.test.common.{ConverterTestData, UnitSpec}

class LoadSpec extends UnitSpec with ConverterTestData {
  val nodeMapping = Map(
    "LV1.101 Bus 1" -> getNodePair("LV1.101 Bus 1")._1
  )

  val rawData = Vector(
    RawModelData(
      classOf[Load],
      Map(
        "id" -> "LV1.101 Load 8",
        "node" -> "LV1.101 Bus 1",
        "profile" -> "L2-A",
        "pLoad" -> "0.014",
        "qLoad" -> "0.005533",
        "sR" -> "0.0150538",
        "subnet" -> "LV1.101",
        "voltLvl" -> "7"
      )
    ),
    RawModelData(
      classOf[Load],
      Map(
        "id" -> "LV1.101 Load 9",
        "node" -> "LV1.101 Bus 1",
        "profile" -> "H0-G",
        "pLoad" -> "0.014",
        "qLoad" -> "0.005533",
        "sR" -> "0.0150538",
        "subnet" -> "LV1.101",
        "voltLvl" -> "7"
      )
    )
  )

  val expected = Vector(
    Load(
      "LV1.101 Load 8",
      getNodePair("LV1.101 Bus 1")._1,
      LoadProfileType.L2A,
      BigDecimal("0.014"),
      BigDecimal("0.005533"),
      BigDecimal("0.0150538"),
      "LV1.101",
      7
    ),
    Load(
      "LV1.101 Load 9",
      getNodePair("LV1.101 Bus 1")._1,
      LoadProfileType.H0G,
      BigDecimal("0.014"),
      BigDecimal("0.005533"),
      BigDecimal("0.0150538"),
      "LV1.101",
      7
    )
  )

  "The load object" should {
    "throw an exception, when the basic single model creation method is called" in {
      val thrown =
        intercept[SimbenchDataModelException](Load.buildModel(rawData(0)))
      thrown.getMessage shouldBe "No basic implementation of model creation available for Load$"
    }

    "throw an exception, when the basic batch model creation method is called" in {
      val thrown =
        intercept[SimbenchDataModelException](Load.buildModels(rawData))
      thrown.getMessage shouldBe "No basic implementation of model creation available for Load$"
    }

    "build the correct single model" in {
      val actual = Load.buildModel(
        rawData(0),
        nodeMapping.getOrElse("LV1.101 Bus 1",
                              throw SimbenchDataModelException(
                                "Ooops. This is not supposed to happen"))
      )
      actual shouldBe expected(0)
    }

    "build a correct vector of models" in {
      val actual =
        Load.buildModels(rawData, nodeMapping)
      actual shouldBe expected
    }
  }
}
