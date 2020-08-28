/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.simbench.convert

import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.simbench.model.datamodel.Node
import edu.ie3.test.common.{ConverterTestData, UnitSpec}

class SwitchConverterSpec extends UnitSpec with ConverterTestData {
  val (input, expected) = getSwitchPair("LV1.101 Switch 1")
  val nodeMapping: Map[Node, NodeInput] = Vector(
    getNodePair("LV1.101 Bus 1"),
    getNodePair("LV1.101 Bus 4")
  ).map(entry => entry._1 -> entry._2).toMap

  "The switch converter" should {
    "convert a single model correctly" in {
      val actual = SwitchConverter.convert(
        input,
        getNodePair("LV1.101 Bus 1")._2,
        getNodePair("LV1.101 Bus 4")._2
      )

      actual.getId shouldBe expected.getId
      actual.getNodeA shouldBe expected.getNodeA
      actual.getNodeB shouldBe expected.getNodeB
      actual.getParallelDevices shouldBe expected.getParallelDevices
      actual.getOperationTime shouldBe expected.getOperationTime
      actual.getOperator shouldBe expected.getOperator
    }

    "convert a batch of models correctly" in {
      val actual = SwitchConverter.convert(Vector(input), nodeMapping)

      actual.length shouldBe 1
      actual(0).getId shouldBe expected.getId
      actual(0).getNodeA shouldBe expected.getNodeA
      actual(0).getNodeB shouldBe expected.getNodeB
      actual(0).getParallelDevices shouldBe expected.getParallelDevices
      actual(0).getOperationTime shouldBe expected.getOperationTime
      actual(0).getOperator shouldBe expected.getOperator
    }
  }
}
