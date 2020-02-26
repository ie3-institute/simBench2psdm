package edu.ie3.simbench.model

import edu.ie3.simbench.exception.io.SimbenchDataModelException
import edu.ie3.simbench.model.datamodel.Node.NodeKey
import edu.ie3.simbench.model.datamodel.{Coordinate, Node, Substation}
import edu.ie3.simbench.model.datamodel.enums.NodeType
import edu.ie3.test.common.UnitSpec

class NodeSpec extends UnitSpec {
  val coordinates = Map(
    "coord_14" -> Coordinate(
      "coord_14",
      BigDecimal("11.4097"),
      BigDecimal("53.6413"),
      "MV1.101_LV1.101_Feeder1",
      5
    ))

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
        "coordID" -> "coord_14",
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
    Node(
      "MV1.101 Bus 4",
      NodeType.BusBar,
      Some(BigDecimal("1.025")),
      Some(BigDecimal("0.0")),
      BigDecimal("20"),
      BigDecimal("0.965"),
      BigDecimal("1.055"),
      Some(Substation("substation_1", "LV1.101", 7)),
      Some(
        Coordinate(
          "coord_14",
          BigDecimal("11.4097"),
          BigDecimal("53.6413"),
          "MV1.101_LV1.101_Feeder1",
          5
        )),
      "MV1.101_LV1.101_Feeder1",
      5
    ),
    Node(
      "MV1.101 Bus 4",
      NodeType.BusBar,
      None,
      None,
      BigDecimal("20"),
      BigDecimal("0.965"),
      BigDecimal("1.055"),
      None,
      None,
      "MV1.101_LV1.101_Feeder1",
      5
    )
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
        intercept[SimbenchDataModelException](Node.buildModel(rawData(0)))
      thrown.getMessage shouldBe "No basic implementation of model creation available for Node$"
    }

    "throw an exception, when the basic batch model creation method is called" in {
      val thrown =
        intercept[SimbenchDataModelException](Node.buildModels(rawData))
      thrown.getMessage shouldBe "No basic implementation of model creation available for Node$"
    }

    "build the correct single model" in {
      val actual = Node.buildModel(rawData(0),
                                   coordinates.get("coord_14"),
                                   substations.get("substation_1"))
      actual shouldBe expected(0)
    }

    "build a correct vector of line types" in {
      val actual = Node.buildModels(rawData, coordinates, substations)
      actual shouldBe expected
    }
  }
}
