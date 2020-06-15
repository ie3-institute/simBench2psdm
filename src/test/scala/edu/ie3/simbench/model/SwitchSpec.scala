package edu.ie3.simbench.model

import edu.ie3.simbench.exception.io.SimbenchDataModelException
import edu.ie3.simbench.model.datamodel.enums.{NodeType, SwitchType}
import edu.ie3.simbench.model.datamodel.{Coordinate, Node, Substation, Switch}
import edu.ie3.test.common.UnitSpec

class SwitchSpec extends UnitSpec {
  val nodes = Map(
    "LV1.101 Bus 1" -> Node(
      "LV1.101 Bus 1",
      NodeType.BusBar,
      None,
      None,
      BigDecimal("0.4"),
      BigDecimal("0.9"),
      BigDecimal("1.1"),
      None,
      Some(
        Coordinate(
          "coord_0",
          BigDecimal("11.411"),
          BigDecimal("53.6407"),
          "LV1.101",
          7
        )
      ),
      "LV1.101",
      7
    ),
    "LV1.101 Bus 4" -> Node(
      "LV1.101 Bus 4",
      NodeType.BusBar,
      None,
      None,
      BigDecimal("0.4"),
      BigDecimal("0.9"),
      BigDecimal("1.1"),
      None,
      Some(
        Coordinate(
          "coord_3",
          BigDecimal("11.4097"),
          BigDecimal("53.6413"),
          "LV1.101",
          7
        )
      ),
      "LV1.101",
      7
    )
  )

  val substations = Map(
    "substation_1" -> Substation("substation_1", "LV1.101", 7)
  )

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
    Switch(
      "LV1.101 Switch 1",
      Node(
        "LV1.101 Bus 1",
        NodeType.BusBar,
        None,
        None,
        BigDecimal("0.4"),
        BigDecimal("0.9"),
        BigDecimal("1.1"),
        None,
        Some(
          Coordinate(
            "coord_0",
            BigDecimal("11.411"),
            BigDecimal("53.6407"),
            "LV1.101",
            7
          )
        ),
        "LV1.101",
        7
      ),
      Node(
        "LV1.101 Bus 4",
        NodeType.BusBar,
        None,
        None,
        BigDecimal("0.4"),
        BigDecimal("0.9"),
        BigDecimal("1.1"),
        None,
        Some(
          Coordinate(
            "coord_3",
            BigDecimal("11.4097"),
            BigDecimal("53.6413"),
            "LV1.101",
            7
          )
        ),
        "LV1.101",
        7
      ),
      SwitchType.LoadSwitch,
      cond = true,
      Some(Substation("substation_1", "LV1.101", 7)),
      "LV1.101",
      7
    ),
    Switch(
      "LV1.101 Switch 1",
      Node(
        "LV1.101 Bus 1",
        NodeType.BusBar,
        None,
        None,
        BigDecimal("0.4"),
        BigDecimal("0.9"),
        BigDecimal("1.1"),
        None,
        Some(
          Coordinate(
            "coord_0",
            BigDecimal("11.411"),
            BigDecimal("53.6407"),
            "LV1.101",
            7
          )
        ),
        "LV1.101",
        7
      ),
      Node(
        "LV1.101 Bus 4",
        NodeType.BusBar,
        None,
        None,
        BigDecimal("0.4"),
        BigDecimal("0.9"),
        BigDecimal("1.1"),
        None,
        Some(
          Coordinate(
            "coord_3",
            BigDecimal("11.4097"),
            BigDecimal("53.6413"),
            "LV1.101",
            7
          )
        ),
        "LV1.101",
        7
      ),
      SwitchType.LoadSwitch,
      cond = false,
      None,
      "LV1.101",
      7
    )
  )

  "The switch object" should {
    "throw an exception, when the basic single model creation method is called" in {
      val thrown =
        intercept[SimbenchDataModelException](Switch.apply(rawData(0)))
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
        nodes.getOrElse(
          "LV1.101 Bus 1",
          throw SimbenchDataModelException(
            "Ooops. This is not supposed to happen"
          )
        ),
        nodes.getOrElse(
          "LV1.101 Bus 4",
          throw SimbenchDataModelException(
            "Ooops. This is not supposed to happen"
          )
        ),
        substations.get("substation_1")
      )
      actual shouldBe expected(0)
    }

    "build a correct vector of models" in {
      val actual =
        Switch.buildModels(rawData, nodes, substations)
      actual shouldBe expected
    }
  }
}
