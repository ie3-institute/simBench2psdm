package edu.ie3.simbench.model

import edu.ie3.simbench.exception.io.SimbenchDataModelException
import edu.ie3.simbench.model.datamodel.Line.ACLine
import edu.ie3.simbench.model.datamodel.enums.{LineStyle, NodeType}
import edu.ie3.simbench.model.datamodel.types.LineType.ACLineType
import edu.ie3.simbench.model.datamodel.{Coordinate, Line, Node}
import edu.ie3.test.common.UnitSpec

class LineSpec extends UnitSpec {
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
        Coordinate("coord_3",
                   BigDecimal("11.4097"),
                   BigDecimal("53.6413"),
                   "LV1.101",
                   7)),
      "LV1.101",
      7
    )
  )

  val lineTypes = Map(
    "NAYY 4x150SE 0.6/1kV" -> ACLineType("NAYY 4x150SE 0.6/1kV",
                                         BigDecimal("0.2067"),
                                         BigDecimal("0.0804248"),
                                         BigDecimal("260.752"),
                                         BigDecimal("270"),
                                         LineStyle.Cable),
    "24-AL1/4-ST1A 20.0" -> ACLineType("24-AL1/4-ST1A 20.0",
                                       BigDecimal("1.2012"),
                                       BigDecimal("0.394"),
                                       BigDecimal("3.53429"),
                                       BigDecimal("140"),
                                       LineStyle.OverheadLine)
  )

  val rawData = Vector(
    RawModelData(
      classOf[Line[_]],
      Map(
        "id" -> "LV1.101 Line 10",
        "nodeA" -> "LV1.101 Bus 4",
        "nodeB" -> "LV1.101 Bus 1",
        "type" -> "NAYY 4x150SE 0.6/1kV",
        "length" -> "0.132499",
        "loadingMax" -> "100",
        "subnet" -> "LV1.101",
        "voltLvl" -> "7"
      )
    ),
    RawModelData(
      classOf[Line[_]],
      Map(
        "id" -> "LV1.101 Line 10",
        "nodeA" -> "LV1.101 Bus 1",
        "nodeB" -> "LV1.101 Bus 4",
        "type" -> "24-AL1/4-ST1A 20.0",
        "length" -> "0.132499",
        "loadingMax" -> "100",
        "subnet" -> "LV1.101",
        "voltLvl" -> "7"
      )
    )
  )

  val expected = Vector(
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
    ACLine(
      "LV1.101 Line 10",
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
      ACLineType("24-AL1/4-ST1A 20.0",
                 BigDecimal("1.2012"),
                 BigDecimal("0.394"),
                 BigDecimal("3.53429"),
                 BigDecimal("140"),
                 LineStyle.OverheadLine),
      BigDecimal("0.132499"),
      BigDecimal("100"),
      "LV1.101",
      7
    )
  )

  "The Line object" should {
    "throw an exception, when the basic single model creation method is called" in {
      val thrown =
        intercept[SimbenchDataModelException](Line.apply(rawData(0)))
      thrown.getMessage shouldBe "No basic implementation of model creation available for Line$"
    }

    "throw an exception, when the basic batch model creation method is called" in {
      val thrown =
        intercept[SimbenchDataModelException](Line.buildModels(rawData))
      thrown.getMessage shouldBe "No basic implementation of model creation available for Line$"
    }

    "build the correct single model" in {
      val actual = Line.buildModel(
        rawData(0),
        nodes.getOrElse("LV1.101 Bus 4",
                        throw SimbenchDataModelException(
                          "Ooops. This is not supposed to happen")),
        nodes.getOrElse("LV1.101 Bus 1",
                        throw SimbenchDataModelException(
                          "Ooops. This is not supposed to happen")),
        lineTypes.getOrElse("NAYY 4x150SE 0.6/1kV",
                            throw SimbenchDataModelException(
                              "Ooops. This is not supposed to happen"))
      )
      actual shouldBe expected(0)
    }

    "build a correct vector of models" in {
      val actual = Line.buildModels(rawData, nodes, lineTypes)
      actual shouldBe expected
    }
  }
}
