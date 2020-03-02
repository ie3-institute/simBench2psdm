package edu.ie3.simbench.convert

import java.util.{Optional, UUID}

import edu.ie3.models.GermanVoltageLevel.MV
import edu.ie3.models.OperationTime
import edu.ie3.models.input.connector.LineInput
import edu.ie3.models.input.connector.`type`.LineTypeInput
import edu.ie3.models.input.{NodeInput, OperatorInput}
import edu.ie3.simbench.exception.ConversionException
import edu.ie3.simbench.model.datamodel.Line.{ACLine, DCLine}
import edu.ie3.simbench.model.datamodel.types.LineType
import edu.ie3.simbench.model.datamodel.types.LineType.{ACLineType, DCLineType}
import edu.ie3.test.common.{ConverterTestData, UnitSpec}
import edu.ie3.util.quantities.PowerSystemUnits.{KILOMETRE, KILOVOLT, PU}
import tec.uom.se.quantity.Quantities

class LineConverterSpec extends UnitSpec with ConverterTestData {
  val (nodeAIn, nodeA) = getNodePair("slack_node_0")
  val (nodeBIn, nodeB) = getNodePair("node_0")

  val nodeMap = Map(
    nodeAIn -> nodeA,
    nodeBIn -> nodeB
  )

  val (lineTypeIn, lineType) = getLineTypePair("NAYY 4x150SE 0.6/1kV")
  val lineTypeMapping: Map[LineType, LineTypeInput] = Map(
    lineTypeIn -> lineType)

  val invalidInput: DCLine = DCLine(
    "dc line",
    nodeAIn,
    nodeBIn,
    getLineTypePair("dc line type")._1.asInstanceOf[DCLineType],
    BigDecimal("100"),
    BigDecimal("100"),
    "subnet 1",
    7
  )

  val validInput: ACLine = ACLine(
    "ac line",
    nodeAIn,
    nodeBIn,
    lineTypeIn.asInstanceOf[ACLineType],
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
        this.nodeA.getUuid,
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
        this.nodeB.getUuid,
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
        this.nodeA.getUuid,
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
        this.nodeB.getUuid,
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
