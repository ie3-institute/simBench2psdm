/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.simbench.convert

import java.util.UUID

import edu.ie3.datamodel.models.OperationTime
import edu.ie3.datamodel.models.input.connector.`type`.Transformer2WTypeInput
import edu.ie3.datamodel.models.input.{NodeInput, OperatorInput}
import edu.ie3.simbench.model.datamodel.enums.BranchElementPort.HV
import edu.ie3.simbench.model.datamodel.types.Transformer2WType
import edu.ie3.simbench.model.datamodel.{Node, Transformer2W}
import edu.ie3.test.common.{ConverterTestData, UnitSpec}
import org.locationtech.jts.geom.{Point, Coordinate => JTSCoordinate}

class Transformer2wConverterSpec extends UnitSpec with ConverterTestData {
  val (inputType, resultType) = getTransformer2WTypePair("test type")
  val types: Map[Transformer2WType, Transformer2WTypeInput] = Map(
    inputType -> resultType
  )

  val (nodeAInput, nodeA) = getNodePair("MV1.101 Bus 4")
  val (nodeBInput, nodeB) = getNodePair("LV1.101 Bus 4")
  val nodeMapping: Map[Node, NodeInput] = Map(
    nodeAInput -> nodeA,
    nodeBInput -> nodeB
  )

  val (input, expected) = getTransformer2WPair("MV1.101-LV1.101-Trafo 1")

  "The two winding transformer converter" should {
    "convert a single input correctly" in {
      val actual =
        Transformer2wConverter.convert(input, resultType, nodeA, nodeB)

      actual.getId shouldBe "MV1.101-LV1.101-Trafo 1"
      actual.getNodeA shouldBe nodeA
      actual.getNodeB shouldBe nodeB
      actual.getType shouldBe resultType
      actual.getParallelDevices shouldBe 1
      actual.getOperationTime shouldBe OperationTime.notLimited()
      actual.getOperator shouldBe OperatorInput.NO_OPERATOR_ASSIGNED
      actual.getTapPos shouldBe 10
    }
  }

  "convert a set of input models correctly" in {
    val actual =
      Transformer2wConverter.convert(Vector(input), types, nodeMapping)

    actual.length shouldBe 1

    actual(0).getId shouldBe "MV1.101-LV1.101-Trafo 1"
    actual(0).getNodeA shouldBe nodeA
    actual(0).getNodeB shouldBe nodeB
    actual(0).getType shouldBe resultType
    actual(0).getParallelDevices shouldBe 1
    actual(0).getOperationTime shouldBe OperationTime.notLimited()
    actual(0).getOperator shouldBe OperatorInput.NO_OPERATOR_ASSIGNED
    actual(0).getTapPos shouldBe 10
  }
}
