package edu.ie3.simbench.model

import edu.ie3.simbench.exception.io.SimbenchDataModelException
import edu.ie3.simbench.model.datamodel.ExternalNet.{Simple, Ward, WardExtended}
import edu.ie3.simbench.model.datamodel.enums.{CalculationType, NodeType}
import edu.ie3.simbench.model.datamodel.{
  Coordinate,
  ExternalNet,
  Node,
  Substation
}
import edu.ie3.test.common.UnitSpec

class ExternalNetSpec extends UnitSpec {
  val nodes = Map(
    "MV1.101 Bus 4" ->
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
          )
        ),
        "MV1.101_LV1.101_Feeder1",
        5
      )
  )

  val rawData = Vector(
    RawModelData(
      classOf[ExternalNet],
      Map(
        "id" -> "MV1.101 grid at LV1.101",
        "node" -> "MV1.101 Bus 4",
        "calc_type" -> "vavm",
        "dspf" -> "1",
        "pExtNet" -> "NULL",
        "qExtNet" -> "NULL",
        "pWardShunt" -> "NULL",
        "qWardShunt" -> "NULL",
        "rXWard" -> "NULL",
        "xXWard" -> "NULL",
        "vmXWard" -> "NULL",
        "subnet" -> "LV1.101_MV1.101_eq",
        "voltLvl" -> "5"
      )
    ),
    RawModelData(
      classOf[ExternalNet],
      Map(
        "id" -> "MV1.101 grid at LV1.101",
        "node" -> "MV1.101 Bus 4",
        "calc_type" -> "ward",
        "dspf" -> "1",
        "pExtNet" -> "NULL",
        "qExtNet" -> "NULL",
        "pWardShunt" -> "100.0",
        "qWardShunt" -> "200.0",
        "rXWard" -> "NULL",
        "xXWard" -> "NULL",
        "vmXWard" -> "NULL",
        "subnet" -> "LV1.101_MV1.101_eq",
        "voltLvl" -> "5"
      )
    ),
    RawModelData(
      classOf[ExternalNet],
      Map(
        "id" -> "MV1.101 grid at LV1.101",
        "node" -> "MV1.101 Bus 4",
        "calc_type" -> "xWard",
        "dspf" -> "1",
        "pExtNet" -> "NULL",
        "qExtNet" -> "NULL",
        "pWardShunt" -> "NULL",
        "qWardShunt" -> "NULL",
        "rXWard" -> "1.0",
        "xXWard" -> "2.0",
        "vmXWard" -> "3.0",
        "subnet" -> "LV1.101_MV1.101_eq",
        "voltLvl" -> "5"
      )
    )
  )

  val expected = Vector(
    Simple(
      "MV1.101 grid at LV1.101",
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
          )
        ),
        "MV1.101_LV1.101_Feeder1",
        5
      ),
      CalculationType.VaVm,
      BigDecimal("1"),
      None,
      None,
      "LV1.101_MV1.101_eq",
      5
    ),
    Ward(
      "MV1.101 grid at LV1.101",
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
          )
        ),
        "MV1.101_LV1.101_Feeder1",
        5
      ),
      BigDecimal("1"),
      None,
      None,
      BigDecimal("100.0"),
      BigDecimal("200.0"),
      "LV1.101_MV1.101_eq",
      5
    ),
    WardExtended(
      "MV1.101 grid at LV1.101",
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
          )
        ),
        "MV1.101_LV1.101_Feeder1",
        5
      ),
      BigDecimal("1"),
      None,
      None,
      BigDecimal("1.0"),
      BigDecimal("2.0"),
      BigDecimal("3.0"),
      "LV1.101_MV1.101_eq",
      5
    )
  )

  "The external net object" should {
    "throw an exception, when the basic single model creation method is called" in {
      val thrown =
        intercept[SimbenchDataModelException](ExternalNet.apply(rawData(0)))
      thrown.getMessage shouldBe "No basic implementation of model creation available for ExternalNet$"
    }

    "throw an exception, when the basic batch model creation method is called" in {
      val thrown =
        intercept[SimbenchDataModelException](ExternalNet.buildModels(rawData))
      thrown.getMessage shouldBe "No basic implementation of model creation available for ExternalNet$"
    }

    "build the correct single model" in {
      val actual = ExternalNet.buildModel(
        rawData(0),
        nodes.getOrElse(
          "MV1.101 Bus 4",
          throw SimbenchDataModelException(
            "Ooops. This is not supposed to happen"
          )
        )
      )
      actual shouldBe expected(0)
    }

    "build a correct vector of models" in {
      val actual =
        ExternalNet.buildModels(rawData, nodes)
      actual shouldBe expected
    }
  }
}
