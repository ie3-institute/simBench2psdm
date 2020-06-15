package edu.ie3.simbench.convert

import java.util.UUID

import edu.ie3.datamodel.models.OperationTime
import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.datamodel.models.input.connector.LineInput
import edu.ie3.datamodel.models.input.connector.`type`.LineTypeInput
import edu.ie3.datamodel.models.input.system.characteristic.OlmCharacteristicInput
import edu.ie3.simbench.exception.ConversionException
import edu.ie3.simbench.model.datamodel.Line.{ACLine, DCLine}
import edu.ie3.simbench.model.datamodel.types.LineType
import edu.ie3.simbench.model.datamodel.types.LineType.{ACLineType, DCLineType}
import edu.ie3.test.common.{ConverterTestData, UnitSpec}
import edu.ie3.util.quantities.PowerSystemUnits.KILOMETRE
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
    lineTypeIn -> lineType
  )

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
    "ac line",
    OperatorInput.NO_OPERATOR_ASSIGNED,
    OperationTime.notLimited(),
    nodeA,
    nodeB,
    1,
    lineType,
    Quantities.getQuantity(100d, KILOMETRE),
    geometryFactory.createLineString(
      Array(
        nodeA.getGeoPosition.getCoordinate,
        nodeB.getGeoPosition.getCoordinate
      )
    ),
    OlmCharacteristicInput.CONSTANT_CHARACTERISTIC
  )

  "The line converter" should {
    "throw an exception, if a DCLine should be converted" in {
      val thrown = intercept[ConversionException](
        LineConverter.convert(invalidInput, lineType, nodeA, nodeB, lineUuid)
      )
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
      actual.getParallelDevices shouldBe expectedLine.getParallelDevices
      actual.getOperationTime shouldBe expectedLine.getOperationTime
      actual.getOperator shouldBe expectedLine.getOperator
      actual.getGeoPosition shouldBe expectedLine.getGeoPosition
    }
  }
}
