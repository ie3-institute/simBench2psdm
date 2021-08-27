package edu.ie3.simbench.convert

import edu.ie3.simbench.actor.ResConverter

import java.util.Objects
import edu.ie3.test.common.{ConverterTestData, UnitSpec}
import edu.ie3.test.matchers.QuantityMatchers

class ResConverterSpec
    extends UnitSpec
    with QuantityMatchers
    with ConverterTestData {
  implicit val quantityMatchingTolerance: Double = 1e-4

  "The RES converter" should {
    val (_, node) = getNodePair("MV1.101 Bus 4")
    val (input, expected) = getResPair("MV1.101 SGen 2")
    val actual = ResConverter.Worker.convertModel(input, node)

    "bring up the correct input model" in {
      Objects.nonNull(actual.getUuid) shouldBe true
      actual.getId shouldBe expected.getId
      actual.getOperator shouldBe expected.getOperator
      actual.getOperationTime shouldBe expected.getOperationTime
      actual.getNode shouldBe expected.getNode
      actual.getqCharacteristics shouldBe expected.getqCharacteristics
      actual.getsRated shouldBe expected.getsRated
      actual.getCosPhiRated shouldBe expected.getCosPhiRated
    }
  }
}
