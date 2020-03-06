package edu.ie3.simbench.model

import edu.ie3.simbench.exception.io.SimbenchDataModelException
import edu.ie3.simbench.model.datamodel.Node.NodeKey
import edu.ie3.simbench.model.datamodel.{Node, Substation}
import edu.ie3.test.common.{ConverterTestData, UnitSpec}

class NodeSpec extends UnitSpec with ConverterTestData {
  val coordinateMapping = Map(
    "coordinate_14" -> getCoordinatePair("coordinate_14")._1)

  val substations = Map(
    "substation_1" -> Substation("substation_1", "LV1.101", 7))

  val rawData = Vector(
    RawModelData(
      classOf[Node],
      Map(
        "id" -> "MV1.101 Bus 4",
        "type" -> "busbar",
        "vmSetp" -> "1.025",
        "vaSetp" -> "0.0",
        "vmR" -> "20",
        "vmMin" -> "0.965",
        "vmMax" -> "1.055",
        "substation" -> "substation_1",
        "coordID" -> "coordinate_14",
        "subnet" -> "MV1.101_LV1.101_Feeder1",
        "voltLvl" -> "5"
      )
    ),
    RawModelData(
      classOf[Node],
      Map(
        "id" -> "MV1.101 Bus 4",
        "type" -> "busbar",
        "vmSetp" -> "NULL",
        "vaSetp" -> "",
        "vmR" -> "20",
        "vmMin" -> "0.965",
        "vmMax" -> "1.055",
        "substation" -> "NULL",
        "coordID" -> "",
        "subnet" -> "MV1.101_LV1.101_Feeder1",
        "voltLvl" -> "5"
      )
    )
  )

  val expected = Vector(
    getNodePair("MV1.101 Bus 4")._1,
    getNodePair("MV1.101 Bus 4")._1
      .copy(vmSetp = None, vaSetp = None, substation = None, coordinate = None),
  )

  "The node class" should {
    "generate the correct key" in {
      expected(0).getKey shouldBe NodeKey("MV1.101 Bus 4",
                                          "MV1.101_LV1.101_Feeder1",
                                          5)
    }
  }

  "The Node object" should {
    "throw an exception, when the basic single model creation method is called" in {
      val thrown =
        intercept[SimbenchDataModelException](Node.apply(rawData(0)))
      thrown.getMessage shouldBe "No basic implementation of model creation available for Node$"
    }

    "throw an exception, when the basic batch model creation method is called" in {
      val thrown =
        intercept[SimbenchDataModelException](Node.buildModels(rawData))
      thrown.getMessage shouldBe "No basic implementation of model creation available for Node$"
    }

    "build the correct single model" in {
      val actual = Node.buildModel(rawData(0),
                                   coordinateMapping.get("coordinate_14"),
                                   substations.get("substation_1"))
      actual shouldBe expected(0)
    }

    "build a correct vector of nodes" in {
      val actual = Node.buildModels(rawData, coordinateMapping, substations)
      actual shouldBe expected
    }
  }
}
