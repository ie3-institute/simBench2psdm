package edu.ie3.simbench.convert.types

import java.util.UUID

import edu.ie3.models.StandardUnits
import edu.ie3.models.input.connector.`type`.LineTypeInput
import edu.ie3.simbench.exception.io.SimbenchDataModelException
import edu.ie3.simbench.model.datamodel.Line.ACLine
import edu.ie3.simbench.model.datamodel.{Coordinate, Node}
import edu.ie3.simbench.model.datamodel.enums.{LineStyle, NodeType}
import edu.ie3.simbench.model.datamodel.types.LineType
import edu.ie3.simbench.model.datamodel.types.LineType.{ACLineType, DCLineType}
import edu.ie3.test.common.UnitSpec
import edu.ie3.util.quantities.PowerSystemUnits.KILOVOLT
import javax.measure.quantity.ElectricPotential
import tec.uom.se.ComparableQuantity
import tec.uom.se.quantity.Quantities

class LineTypeConverterSpec extends UnitSpec {
  val invalidLine: ACLine = ACLine(
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
      BigDecimal("10.0"),
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

  val lines = Vector(
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
      "LV1.101 Line 11",
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

  val uuid: UUID = UUID.randomUUID()

  val input: ACLineType = ACLineType("NAYY 4x150SE 0.6/1kV",
                                     BigDecimal("0.2067"),
                                     BigDecimal("0.0804248"),
                                     BigDecimal("260.752"),
                                     BigDecimal("270"),
                                     LineStyle.Cable)
  val invalidInput: DCLineType = DCLineType("invalid type",
                                            BigDecimal("0"),
                                            BigDecimal("0"),
                                            BigDecimal("0"),
                                            BigDecimal("0"),
                                            BigDecimal("0"),
                                            BigDecimal("0"),
                                            BigDecimal("0"),
                                            BigDecimal("0"))

  "The line type converter" should {
    val determineRatedVoltageMethod =
      PrivateMethod[(LineType, ComparableQuantity[ElectricPotential])](
        Symbol("determineRatedVoltage"))
    "extract the rated voltage of one line correctly" in {
      val actual = LineTypeConverter invokePrivate determineRatedVoltageMethod(
        lines(0))
      actual shouldBe (ACLineType(
        "NAYY 4x150SE 0.6/1kV",
        BigDecimal("0.2067"),
        BigDecimal("0.0804248"),
        BigDecimal("260.752"),
        BigDecimal("270"),
        LineStyle.Cable), Quantities.getQuantity(0.4, KILOVOLT))
    }

    "throw an exception, if the rated voltage is ambiguous" in {
      val thrown = intercept[SimbenchDataModelException](
        LineTypeConverter invokePrivate determineRatedVoltageMethod(
          invalidLine))
      thrown.getMessage shouldBe "The line LV1.101 Line 10 connects two nodes with different rated voltages, which " +
        "physically is not possible"
    }

    "build line type to rated voltage mapping correctly" in {
      val actual = LineTypeConverter.getRatedVoltages(lines)
      val expected = Map(
        ACLineType("NAYY 4x150SE 0.6/1kV",
                   BigDecimal("0.2067"),
                   BigDecimal("0.0804248"),
                   BigDecimal("260.752"),
                   BigDecimal("270"),
                   LineStyle.Cable) -> Quantities.getQuantity(0.4, KILOVOLT),
        ACLineType("24-AL1/4-ST1A 20.0",
                   BigDecimal("1.2012"),
                   BigDecimal("0.394"),
                   BigDecimal("3.53429"),
                   BigDecimal("140"),
                   LineStyle.OverheadLine) -> Quantities.getQuantity(0.4,
                                                                     KILOVOLT)
      )
      actual shouldBe expected
    }

    "convert a valid input correctly" in {
      val actual =
        LineTypeConverter.convert(input,
                                  Quantities.getQuantity(0.4, KILOVOLT),
                                  uuid)
      val expected = new LineTypeInput(
        uuid,
        "NAYY 4x150SE 0.6/1kV",
        Quantities
          .getQuantity(260752.0, StandardUnits.ADMITTANCE_PER_LENGTH), // TODO Wrong implementation of StandardUnit...
        Quantities.getQuantity(0d, StandardUnits.ADMITTANCE_PER_LENGTH),
        Quantities.getQuantity(0.2067, StandardUnits.IMPEDANCE_PER_LENGTH),
        Quantities.getQuantity(0.0804248, StandardUnits.IMPEDANCE_PER_LENGTH),
        Quantities.getQuantity(270d, StandardUnits.ELECTRIC_CURRENT_MAGNITUDE),
        Quantities.getQuantity(0.4, StandardUnits.RATED_VOLTAGE_MAGNITUDE)
      )

      actual shouldBe expected
    }

    "throw an exception on wrong input type" in {
      val thrown = intercept[IllegalArgumentException](
        LineTypeConverter
          .convert(invalidInput, Quantities.getQuantity(0.4, KILOVOLT), uuid))
      thrown.getMessage shouldBe "DC line types are currently not supported by ie³'s data model."
    }
  }
}
