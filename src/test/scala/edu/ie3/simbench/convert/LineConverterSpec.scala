/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.simbench.convert

import java.util.{Objects, UUID}

import edu.ie3.datamodel.models.input.connector.`type`.LineTypeInput
import edu.ie3.simbench.exception.{ConversionException, TestingException}
import edu.ie3.simbench.model.datamodel.Line.DCLine
import edu.ie3.simbench.model.datamodel.types.LineType
import edu.ie3.test.common.{ConverterTestData, UnitSpec}

class LineConverterSpec extends UnitSpec with ConverterTestData {
  val (nodeAIn, nodeA) = getNodePair("slack_node_0")
  val (nodeBIn, nodeB) = getNodePair("node_0")

  val (lineTypeIn, lineType) = getACLineTypes("NAYY 4x150SE 0.6/1kV")
  val lineTypeMapping: Map[LineType, LineTypeInput] = Map(
    lineTypeIn -> lineType
  )

  val invalidInput: DCLine = DCLine(
    "dc line",
    nodeAIn,
    nodeBIn,
    getDCLineTypes("dc line type")._1,
    BigDecimal("100"),
    BigDecimal("100"),
    "subnet 1",
    7
  )

  val (validInput, expectedLine) = getLinePair("ac line")

  val lineUuid: UUID = UUID.randomUUID()

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
      Objects.nonNull(actual.getUuid) shouldBe true
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
