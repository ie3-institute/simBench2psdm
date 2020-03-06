package edu.ie3.simbench.model

import edu.ie3.simbench.exception.io.SimbenchDataModelException
import edu.ie3.simbench.model.datamodel.Line.ACLine
import edu.ie3.simbench.model.datamodel.Measurement.{
  LineMeasurement,
  NodeMeasurement,
  TransformerMeasurement
}
import edu.ie3.simbench.model.datamodel.{
  Coordinate,
  Measurement,
  Node,
  Substation,
  Transformer2W
}
import edu.ie3.simbench.model.datamodel.enums.{
  BranchElementPort,
  LineStyle,
  MeasurementVariable,
  NodeType
}
import edu.ie3.simbench.model.datamodel.types.LineType.ACLineType
import edu.ie3.simbench.model.datamodel.types.Transformer2WType
import edu.ie3.test.common.UnitSpec

class MeasurementSpec extends UnitSpec {
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

  val lines = Map(
    "LV1.101 Line 10" ->
      ACLine(
        "LV1.101 Line 10",
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
            Coordinate("coord_3",
                       BigDecimal("11.4097"),
                       BigDecimal("53.6413"),
                       "LV1.101",
                       7)),
          "LV1.101",
          7
        ),
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
        ACLineType("NAYY 4x150SE 0.6/1kV",
                   BigDecimal("0.2067"),
                   BigDecimal("0.0804248"),
                   BigDecimal("260.752"),
                   BigDecimal("270"),
                   LineStyle.Cable),
        BigDecimal("0.132499"),
        BigDecimal("100"),
        "LV1.101",
        7
      )
  )

  val transformers = Map(
    "MV1.101-LV1.101-Trafo 1" ->
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
      )
  )

  val rawData = Vector(
    RawModelData(
      classOf[Measurement],
      Map(
        "id" -> "LV1.101 Measurement 1",
        "element1" -> "LV1.101 Bus 1",
        "element2" -> "NULL",
        "variable" -> "v",
        "subnet" -> "MV4.101",
        "voltLvl" -> "7"
      )
    ),
    RawModelData(
      classOf[Measurement],
      Map(
        "id" -> "LV1.101 Measurement 1",
        "element1" -> "LV1.101 Bus 1",
        "element2" -> "LV1.101 Line 10",
        "variable" -> "p",
        "subnet" -> "MV4.101",
        "voltLvl" -> "7"
      )
    ),
    RawModelData(
      classOf[Measurement],
      Map(
        "id" -> "LV1.101 Measurement 1",
        "element1" -> "LV1.101 Bus 1",
        "element2" -> "MV1.101-LV1.101-Trafo 1",
        "variable" -> "p",
        "subnet" -> "MV4.101",
        "voltLvl" -> "7"
      )
    )
  )

  val expected = Vector(
    NodeMeasurement(
      "LV1.101 Measurement 1",
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
      MeasurementVariable.Voltage,
      "MV4.101",
      7
    ),
    LineMeasurement(
      "LV1.101 Measurement 1",
      ACLine(
        "LV1.101 Line 10",
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
            Coordinate("coord_3",
                       BigDecimal("11.4097"),
                       BigDecimal("53.6413"),
                       "LV1.101",
                       7)),
          "LV1.101",
          7
        ),
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
        ACLineType("NAYY 4x150SE 0.6/1kV",
                   BigDecimal("0.2067"),
                   BigDecimal("0.0804248"),
                   BigDecimal("260.752"),
                   BigDecimal("270"),
                   LineStyle.Cable),
        BigDecimal("0.132499"),
        BigDecimal("100"),
        "LV1.101",
        7
      ),
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
      MeasurementVariable.ActivePower,
      "MV4.101",
      7
    ),
    TransformerMeasurement(
      "LV1.101 Measurement 1",
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
      MeasurementVariable.ActivePower,
      "MV4.101",
      7
    )
  )

  "The measurement object" should {
    "throw an exception, when the basic single model creation method is called" in {
      val thrown =
        intercept[SimbenchDataModelException](Measurement.apply(rawData(0)))
      thrown.getMessage shouldBe "No basic implementation of model creation available for Measurement$"
    }

    "throw an exception, when the basic batch model creation method is called" in {
      val thrown =
        intercept[SimbenchDataModelException](Measurement.buildModels(rawData))
      thrown.getMessage shouldBe "No basic implementation of model creation available for Measurement$"
    }

    /* No single model creation, as it would impose a lot of duplicate code */

    "build a correct vector of models" in {
      val actual =
        Measurement.buildModels(rawData, nodes, lines, transformers)
      actual shouldBe expected
    }
  }
}
