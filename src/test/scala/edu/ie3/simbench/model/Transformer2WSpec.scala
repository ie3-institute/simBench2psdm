package edu.ie3.simbench.model

import edu.ie3.simbench.exception.io.SimbenchDataModelException
import edu.ie3.simbench.model.datamodel.enums.{BranchElementPort, NodeType}
import edu.ie3.simbench.model.datamodel.types.Transformer2WType
import edu.ie3.simbench.model.datamodel.{
  Coordinate,
  Node,
  Substation,
  Transformer2W
}
import edu.ie3.test.common.UnitSpec

class Transformer2WSpec extends UnitSpec {
  val nodes = Map(
    "MV1.101 Bus 4" -> Node(
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
          "coord_14",
          BigDecimal("11.4097"),
          BigDecimal("53.6413"),
          "MV1.101_LV1.101_Feeder1",
          5
        )),
      "LV1.101",
      7
    )
  )

  val substations = Map(
    "substation_1" -> Substation("substation_1", "LV1.101", 7))

  val transformerTypes = Map(
    "0.16 MVA 20/0.4 kV DOTE 160/20  SGB" -> Transformer2WType(
      "0.16 MVA 20/0.4 kV DOTE 160/20  SGB",
      BigDecimal("0.16"),
      BigDecimal("20"),
      BigDecimal("0.4"),
      BigDecimal("150"),
      BigDecimal("4"),
      BigDecimal("2.35"),
      BigDecimal("0.46"),
      BigDecimal("0.28751"),
      tapable = true,
      BranchElementPort.HV,
      BigDecimal("2.5"),
      BigDecimal("0"),
      0,
      -2,
      2
    )
  )

  val rawData = Vector(
    RawModelData(
      classOf[Transformer2W],
      Map(
        "id" -> "MV1.101-LV1.101-Trafo 1",
        "nodeHV" -> "MV1.101 Bus 4",
        "nodeLV" -> "LV1.101 Bus 4",
        "type" -> "0.16 MVA 20/0.4 kV DOTE 160/20  SGB",
        "tappos" -> "0",
        "autoTap" -> "1",
        "autoTapSide" -> "HV",
        "loadingMax" -> "100",
        "substation" -> "substation_1",
        "subnet" -> "LV1.101",
        "voltLvl" -> "6"
      )
    ),
    RawModelData(
      classOf[Transformer2W],
      Map(
        "id" -> "MV1.101-LV1.101-Trafo 1",
        "nodeHV" -> "MV1.101 Bus 4",
        "nodeLV" -> "LV1.101 Bus 4",
        "type" -> "0.16 MVA 20/0.4 kV DOTE 160/20  SGB",
        "tappos" -> "0",
        "autoTap" -> "0",
        "autoTapSide" -> "NULL",
        "loadingMax" -> "100",
        "substation" -> "NULL",
        "subnet" -> "LV1.101",
        "voltLvl" -> "6"
      )
    )
  )

  val expected = Vector(
    Transformer2W(
      "MV1.101-LV1.101-Trafo 1",
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
            "coord_14",
            BigDecimal("11.4097"),
            BigDecimal("53.6413"),
            "MV1.101_LV1.101_Feeder1",
            5
          )),
        "LV1.101",
        7
      ),
      Transformer2WType(
        "0.16 MVA 20/0.4 kV DOTE 160/20  SGB",
        BigDecimal("0.16"),
        BigDecimal("20"),
        BigDecimal("0.4"),
        BigDecimal("150"),
        BigDecimal("4"),
        BigDecimal("2.35"),
        BigDecimal("0.46"),
        BigDecimal("0.28751"),
        tapable = true,
        BranchElementPort.HV,
        BigDecimal("2.5"),
        BigDecimal("0"),
        0,
        -2,
        2
      ),
      0,
      autoTap = true,
      Some(BranchElementPort.HV),
      BigDecimal("100"),
      Some(Substation("substation_1", "LV1.101", 7)),
      "LV1.101",
      6
    ),
    Transformer2W(
      "MV1.101-LV1.101-Trafo 1",
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
            "coord_14",
            BigDecimal("11.4097"),
            BigDecimal("53.6413"),
            "MV1.101_LV1.101_Feeder1",
            5
          )),
        "LV1.101",
        7
      ),
      Transformer2WType(
        "0.16 MVA 20/0.4 kV DOTE 160/20  SGB",
        BigDecimal("0.16"),
        BigDecimal("20"),
        BigDecimal("0.4"),
        BigDecimal("150"),
        BigDecimal("4"),
        BigDecimal("2.35"),
        BigDecimal("0.46"),
        BigDecimal("0.28751"),
        tapable = true,
        BranchElementPort.HV,
        BigDecimal("2.5"),
        BigDecimal("0"),
        0,
        -2,
        2
      ),
      0,
      autoTap = false,
      None,
      BigDecimal("100"),
      None,
      "LV1.101",
      6
    )
  )

  "The two winding transformer object" should {
    "throw an exception, when the basic single model creation method is called" in {
      val thrown =
        intercept[SimbenchDataModelException](
          Transformer2W.buildModel(rawData(0)))
      thrown.getMessage shouldBe "No basic implementation of model creation available for Transformer2W$"
    }

    "throw an exception, when the basic batch model creation method is called" in {
      val thrown =
        intercept[SimbenchDataModelException](
          Transformer2W.buildModels(rawData))
      thrown.getMessage shouldBe "No basic implementation of model creation available for Transformer2W$"
    }

    "build the correct single model" in {
      val actual = Transformer2W.buildModel(
        rawData(0),
        nodes.getOrElse("MV1.101 Bus 4",
                        throw SimbenchDataModelException(
                          "Ooops. This is not supposed to happen")),
        nodes.getOrElse("LV1.101 Bus 4",
                        throw SimbenchDataModelException(
                          "Ooops. This is not supposed to happen")),
        transformerTypes.getOrElse("0.16 MVA 20/0.4 kV DOTE 160/20  SGB",
                                   throw SimbenchDataModelException(
                                     "Ooops. This is not supposed to happen")),
        substations.get("substation_1")
      )
      actual shouldBe expected(0)
    }

    "build a correct vector of models" in {
      val actual =
        Transformer2W.buildModels(rawData, nodes, transformerTypes, substations)
      actual shouldBe expected
    }
  }
}
