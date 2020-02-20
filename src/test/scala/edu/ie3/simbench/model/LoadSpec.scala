package edu.ie3.simbench.model

import edu.ie3.simbench.exception.io.SimbenchDataModelException
import edu.ie3.simbench.model.datamodel.enums.NodeType
import edu.ie3.simbench.model.datamodel.profiles.LoadProfileType
import edu.ie3.simbench.model.datamodel.{Coordinate, Load, Node}
import edu.ie3.test.common.UnitSpec

class LoadSpec extends UnitSpec {
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
        Coordinate("coord_0",
                   BigDecimal("11.411"),
                   BigDecimal("53.6407"),
                   "LV1.101",
                   7)),
      "LV1.101",
      7
    )
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
          Coordinate("coord_0",
                     BigDecimal("11.411"),
                     BigDecimal("53.6407"),
                     "LV1.101",
                     7)),
        "LV1.101",
        7
      ),
      LoadProfileType.L2A,
      BigDecimal("0.014"),
      BigDecimal("0.005533"),
      BigDecimal("0.0150538"),
      "LV1.101",
      7
    ),
    Load(
      "LV1.101 Load 9",
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
          Coordinate("coord_0",
                     BigDecimal("11.411"),
                     BigDecimal("53.6407"),
                     "LV1.101",
                     7)),
        "LV1.101",
        7
      ),
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
        nodes.getOrElse("LV1.101 Bus 1",
                        throw SimbenchDataModelException(
                          "Ooops. This is not supposed to happen"))
      )
      actual shouldBe expected(0)
    }

    "build a correct vector of models" in {
      val actual =
        Load.buildModels(rawData, nodes)
      actual shouldBe expected
    }
  }
}
