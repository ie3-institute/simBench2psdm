package edu.ie3.simbench.convert

import edu.ie3.simbench.actor.PowerPlantConverter
import edu.ie3.simbench.model.datamodel.profiles.{
  PowerPlantProfile,
  PowerPlantProfileType
}
import edu.ie3.test.common.{ConverterTestData, UnitSpec}
import edu.ie3.test.matchers.QuantityMatchers

class PowerPlantConverterSpec
    extends UnitSpec
    with QuantityMatchers
    with ConverterTestData {
  "The power plant converter" when {
    "converting a power plant without reactive power information" should {
      val (input, expected) = getPowerPlantPair("EHV Gen 1")
      val node = getNodePair("EHV Bus 177")._2
      val actual =
        PowerPlantConverter.Worker.convertModel(input, node)

      "bring up the correct input model" in {
        actual.getId shouldBe expected.getId
        actual.getNode shouldBe expected.getNode
        actual.getOperator shouldBe expected.getOperator
        actual.getqCharacteristics shouldBe expected.getqCharacteristics
        actual.getsRated shouldBe expected.getsRated
        actual.getCosPhiRated shouldBe expected.getCosPhiRated
      }
    }

    "converting a power plant with reactive power information" should {
      val (input, expected) = getPowerPlantPair("EHV Gen 1_withQ")
      val node = getNodePair("EHV Bus 177")._2
      val actual =
        PowerPlantConverter.Worker.convertModel(input, node)

      "bring up the correct input model" in {
        actual.getId shouldBe expected.getId
        actual.getNode shouldBe expected.getNode
        actual.getOperator shouldBe expected.getOperator
        actual.getqCharacteristics shouldBe expected.getqCharacteristics
        actual.getsRated shouldBe expected.getsRated
        actual.getCosPhiRated shouldBe expected.getCosPhiRated
      }
    }
  }
}
