package edu.ie3.simbench.convert

import java.util.{Optional, UUID}

import com.vividsolutions.jts.geom.{
  GeometryFactory,
  Point,
  Coordinate => JTSCoordinate
}
import edu.ie3.models.{OperationTime, StandardUnits}
import edu.ie3.models.input.{NodeInput, OperatorInput}
import edu.ie3.simbench.model.datamodel.Line.{ACLine, DCLine}
import edu.ie3.simbench.model.datamodel.{Coordinate, Line, Node}
import edu.ie3.simbench.model.datamodel.enums.{LineStyle, NodeType}
import edu.ie3.test.common.UnitSpec
import edu.ie3.models.GermanVoltageLevel.MV
import edu.ie3.models.input.connector.LineInput
import edu.ie3.models.input.connector.`type`.LineTypeInput
import edu.ie3.simbench.exception.ConversionException
import edu.ie3.simbench.model.datamodel.types.LineType
import edu.ie3.simbench.model.datamodel.types.LineType.{ACLineType, DCLineType}
import tec.uom.se.quantity.Quantities
import edu.ie3.util.quantities.PowerSystemUnits.{KILOMETRE, KILOVOLT, PU}

class LineConverterSpec extends UnitSpec {
  val geometryFactory = new GeometryFactory()
  val expectedCoordinate: Point =
    geometryFactory.createPoint(new JTSCoordinate(7.412262, 51.492689))

  val nodeAIn: Node = Node(
    "slack_node_0",
    NodeType.BusBar,
    Some(BigDecimal("1.3")),
    None,
    BigDecimal("10.0"),
    BigDecimal("0.95"),
    BigDecimal("1.05"),
    None,
    Some(
      Coordinate("random coordinate",
                 BigDecimal("7.412262"),
                 BigDecimal("51.492689"),
                 "subnet_1",
                 7)),
    "subnet_1",
    5
  )
  val nodeAUuid: UUID = UUID.randomUUID()
  val nodeA = new NodeInput(
    nodeAUuid,
    OperationTime.notLimited(),
    OperatorInput.NO_OPERATOR_ASSIGNED,
    "slack_node_0",
    Quantities.getQuantity(1.3, PU),
    Quantities.getQuantity(10.0, KILOVOLT),
    true,
    expectedCoordinate,
    MV,
    1
  )

  val nodeBIn: Node = Node("node_0",
                           NodeType.Node,
                           None,
                           None,
                           BigDecimal("10.0"),
                           BigDecimal("0.95"),
                           BigDecimal("1.05"),
                           None,
                           None,
                           "subnet_2",
                           5)
  val nodeBUuid: UUID = UUID.randomUUID()
  val nodeB = new NodeInput(
    nodeBUuid,
    OperationTime.notLimited(),
    OperatorInput.NO_OPERATOR_ASSIGNED,
    "node_0",
    Quantities.getQuantity(1.0, PU),
    Quantities.getQuantity(10.0, KILOVOLT),
    false,
    expectedCoordinate,
    MV,
    2
  )

  val nodeMap = Map(
    nodeAIn -> nodeA,
    nodeBIn -> nodeB
  )

  val lineTypeIn: ACLineType = ACLineType("NAYY 4x150SE 0.6/1kV",
                                          BigDecimal("0.2067"),
                                          BigDecimal("0.0804248"),
                                          BigDecimal("260.752"),
                                          BigDecimal("270"),
                                          LineStyle.Cable)

  val lineTypeUuid: UUID = UUID.randomUUID()
  val lineType = new LineTypeInput(
    lineTypeUuid,
    "NAYY 4x150SE 0.6/1kV",
    Quantities
      .getQuantity(260.752, StandardUnits.ADMITTANCE_PER_LENGTH),
    Quantities.getQuantity(0d, StandardUnits.ADMITTANCE_PER_LENGTH),
    Quantities.getQuantity(0.2067, StandardUnits.IMPEDANCE_PER_LENGTH),
    Quantities.getQuantity(0.0804248, StandardUnits.IMPEDANCE_PER_LENGTH),
    Quantities.getQuantity(270d, StandardUnits.ELECTRIC_CURRENT_MAGNITUDE),
    Quantities.getQuantity(0.4, StandardUnits.RATED_VOLTAGE_MAGNITUDE)
  )

  val lineTypes: Map[LineType, LineTypeInput] = Map(lineTypeIn -> lineType)

  val invalidInput: DCLine = DCLine(
    "dc line",
    nodeAIn,
    nodeBIn,
    DCLineType("dc line type",
               BigDecimal("0"),
               BigDecimal("0"),
               BigDecimal("0"),
               BigDecimal("0"),
               BigDecimal("0"),
               BigDecimal("0"),
               BigDecimal("0"),
               BigDecimal("0")),
    BigDecimal("100"),
    BigDecimal("100"),
    "subnet 1",
    7
  )

  val validInput: ACLine = ACLine(
    "ac line",
    nodeAIn,
    nodeBIn,
    lineTypeIn,
    BigDecimal("100"),
    BigDecimal("120"),
    "subnet 1",
    7
  )

  val lineUuid: UUID = UUID.randomUUID()
  val expectedLine = new LineInput(
    lineUuid,
    OperationTime.notLimited(),
    OperatorInput.NO_OPERATOR_ASSIGNED,
    "ac line",
    nodeA,
    nodeB,
    1,
    lineType,
    Quantities.getQuantity(100d, KILOMETRE),
    geometryFactory.createLineString(
      Array(nodeA.getGeoPosition.getCoordinate,
            nodeB.getGeoPosition.getCoordinate)),
    Optional.empty()
  )

  "The line converter" should {
    "throw an exception, if a DCLine should be converted" in {
      val thrown = intercept[ConversionException](
        LineConverter.convert(invalidInput, lineType, nodeA, nodeB, lineUuid))
      thrown.getMessage shouldBe "Conversion of DC lines is currently not supported."
    }

    "convert a correct input model correctly" in {
      val actual =
        LineConverter.convert(validInput, lineType, nodeA, nodeB, lineUuid)
      actual.getUuid shouldBe expectedLine.getUuid
      actual.getId shouldBe expectedLine.getId
      actual.getNodeA shouldBe expectedLine.getNodeA
      actual.getNodeB shouldBe expectedLine.getNodeB
      actual.getType shouldBe expectedLine.getType
      actual.getLength shouldBe expectedLine.getLength
      actual.getOlmCharacteristic shouldBe expectedLine.getOlmCharacteristic
      actual.getNoOfParallelDevices shouldBe expectedLine.getNoOfParallelDevices
      actual.getOperationTime shouldBe expectedLine.getOperationTime
      actual.getOperator shouldBe expectedLine.getOperator
      actual.getGeoPosition shouldBe expectedLine.getGeoPosition
    }

    "convert a correct input model without geo positions correctly" in {
      val nodeA = new NodeInput(
        nodeAUuid,
        OperationTime.notLimited(),
        OperatorInput.NO_OPERATOR_ASSIGNED,
        "slack_node_0",
        Quantities.getQuantity(1.3, PU),
        Quantities.getQuantity(10.0, KILOVOLT),
        true,
        null,
        MV,
        1
      )
      val nodeB = new NodeInput(
        nodeBUuid,
        OperationTime.notLimited(),
        OperatorInput.NO_OPERATOR_ASSIGNED,
        "node_0",
        Quantities.getQuantity(1.0, PU),
        Quantities.getQuantity(10.0, KILOVOLT),
        false,
        null,
        MV,
        2
      )

      val actual =
        LineConverter.convert(validInput, lineType, nodeA, nodeB, lineUuid)
      actual.getUuid shouldBe expectedLine.getUuid
      actual.getId shouldBe expectedLine.getId
      actual.getNodeA shouldBe nodeA
      actual.getNodeB shouldBe nodeB
      actual.getType shouldBe expectedLine.getType
      actual.getLength shouldBe expectedLine.getLength
      actual.getOlmCharacteristic shouldBe expectedLine.getOlmCharacteristic
      actual.getNoOfParallelDevices shouldBe expectedLine.getNoOfParallelDevices
      actual.getOperationTime shouldBe expectedLine.getOperationTime
      actual.getOperator shouldBe expectedLine.getOperator
      actual.getGeoPosition shouldBe null
    }

    "convert a correct input model with geo positions at node a missing correctly" in {
      val nodeA = new NodeInput(
        nodeAUuid,
        OperationTime.notLimited(),
        OperatorInput.NO_OPERATOR_ASSIGNED,
        "slack_node_0",
        Quantities.getQuantity(1.3, PU),
        Quantities.getQuantity(10.0, KILOVOLT),
        true,
        null,
        MV,
        1
      )

      val actual =
        LineConverter.convert(validInput, lineType, nodeA, nodeB, lineUuid)
      actual.getUuid shouldBe expectedLine.getUuid
      actual.getId shouldBe expectedLine.getId
      actual.getNodeA shouldBe nodeA
      actual.getNodeB shouldBe expectedLine.getNodeB
      actual.getType shouldBe expectedLine.getType
      actual.getLength shouldBe expectedLine.getLength
      actual.getOlmCharacteristic shouldBe expectedLine.getOlmCharacteristic
      actual.getNoOfParallelDevices shouldBe expectedLine.getNoOfParallelDevices
      actual.getOperationTime shouldBe expectedLine.getOperationTime
      actual.getOperator shouldBe expectedLine.getOperator
      actual.getGeoPosition shouldBe null
    }

    "convert a correct input model with geo positions at node b missing correctly" in {
      val nodeB = new NodeInput(
        nodeBUuid,
        OperationTime.notLimited(),
        OperatorInput.NO_OPERATOR_ASSIGNED,
        "node_0",
        Quantities.getQuantity(1.0, PU),
        Quantities.getQuantity(10.0, KILOVOLT),
        false,
        null,
        MV,
        2
      )

      val actual =
        LineConverter.convert(validInput, lineType, nodeA, nodeB, lineUuid)
      actual.getUuid shouldBe expectedLine.getUuid
      actual.getId shouldBe expectedLine.getId
      actual.getNodeA shouldBe expectedLine.getNodeA
      actual.getNodeB shouldBe nodeB
      actual.getType shouldBe expectedLine.getType
      actual.getLength shouldBe expectedLine.getLength
      actual.getOlmCharacteristic shouldBe expectedLine.getOlmCharacteristic
      actual.getNoOfParallelDevices shouldBe expectedLine.getNoOfParallelDevices
      actual.getOperationTime shouldBe expectedLine.getOperationTime
      actual.getOperator shouldBe expectedLine.getOperator
      actual.getGeoPosition shouldBe null
    }
  }
}
