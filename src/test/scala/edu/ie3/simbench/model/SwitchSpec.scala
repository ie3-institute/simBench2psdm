package edu.ie3.simbench.model

import edu.ie3.simbench.exception.io.SimbenchDataModelException
import edu.ie3.simbench.model.datamodel.{Substation, Switch}
import edu.ie3.test.common.{ConverterTestData, UnitSpec}

class SwitchSpec extends UnitSpec with ConverterTestData {
  val nodeMapping = Map(
    "LV1.101 Bus 1" -> getNodePair("LV1.101 Bus 1")._1,
    "LV1.101 Bus 4" -> getNodePair("LV1.101 Bus 4")._1
  )

  val substations = Map(
    "substation_1" -> Substation("substation_1", "LV1.101", 7))

  val rawData = Vector(
    RawModelData(
      classOf[Switch],
      Map(
        "id" -> "LV1.101 Switch 1",
        "nodeA" -> "LV1.101 Bus 1",
        "nodeB" -> "LV1.101 Bus 4",
        "type" -> "LS",
        "cond" -> "1",
        "substation" -> "substation_1",
        "subnet" -> "LV1.101",
        "voltLvl" -> "7"
      )
    ),
    RawModelData(
      classOf[Switch],
      Map(
        "id" -> "LV1.101 Switch 1",
        "nodeA" -> "LV1.101 Bus 1",
        "nodeB" -> "LV1.101 Bus 4",
        "type" -> "LS",
        "cond" -> "0",
        "substation" -> "NULL",
        "subnet" -> "LV1.101",
        "voltLvl" -> "7"
      )
    )
  )

  val expected = Vector(
    getSwitchPair("LV1.101 Switch 1")._1,
    getSwitchPair("LV1.101 Switch 1")._1.copy(cond = false, substation = None)
  )

  "The switch object" should {
    "throw an exception, when the basic single model creation method is called" in {
      val thrown =
        intercept[SimbenchDataModelException](Switch.buildModel(rawData(0)))
      thrown.getMessage shouldBe "No basic implementation of model creation available for Switch$"
    }

    "throw an exception, when the basic batch model creation method is called" in {
      val thrown =
        intercept[SimbenchDataModelException](Switch.buildModels(rawData))
      thrown.getMessage shouldBe "No basic implementation of model creation available for Switch$"
    }

    "build the correct single model" in {
      val actual = Switch.buildModel(
        rawData(0),
        nodeMapping.getOrElse("LV1.101 Bus 1",
                              throw SimbenchDataModelException(
                                "Ooops. This is not supposed to happen")),
        nodeMapping.getOrElse("LV1.101 Bus 4",
                              throw SimbenchDataModelException(
                                "Ooops. This is not supposed to happen")),
        substations.get("substation_1")
      )
      actual shouldBe expected(0)
    }

    "build a correct vector of models" in {
      val actual =
        Switch.buildModels(rawData, nodeMapping, substations)
      actual shouldBe expected
    }
  }
}
