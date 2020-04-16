package edu.ie3.simbench.convert

import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.simbench.model.datamodel.profiles.LoadProfile
import edu.ie3.test.common.{ConverterTestData, UnitSpec}
import edu.ie3.util.quantities.PowerSystemUnits.KILOWATTHOUR
import tec.uom.se.quantity.Quantities

import scala.math.abs

class LoadConverterSpec extends UnitSpec with ConverterTestData {
  val testingTolerance = 1e-3

  val (input, expected) = getLoadPair("LV1.101 Load 8")
  val node: NodeInput = getNodePair("LV1.101 Bus 1")._2
  val inputProfile: LoadProfile = getLoadProfile("test profile")

  "The load converter" should {
    "convert a single model correctly" in {
      /* Time series conversion is tested in a single test */
      val (actual, _) = LoadConverter.convert(input, node, inputProfile)

      actual.getId shouldBe actual.getId
      actual.getNode shouldBe expected.getNode
      actual.getOperationTime shouldBe expected.getOperationTime
      actual.getOperator shouldBe expected.getOperator
      actual.getqCharacteristics() shouldBe expected.getqCharacteristics()
      abs(actual.getCosphiRated - expected.getCosphiRated) < testingTolerance shouldBe true
      actual
        .getsRated()
        .subtract(expected.getsRated())
        .to(StandardUnits.S_RATED)
        .getValue
        .doubleValue() < testingTolerance shouldBe true
      actual.geteConsAnnual() shouldBe Quantities.getQuantity(0d, KILOWATTHOUR)
    }
  }
}
