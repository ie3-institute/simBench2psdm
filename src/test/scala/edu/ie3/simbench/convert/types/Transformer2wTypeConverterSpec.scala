/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.simbench.convert.types

import java.util.UUID

import edu.ie3.simbench.exception.ConversionException
import edu.ie3.simbench.model.datamodel.types.Transformer2WType
import edu.ie3.test.common.{ConverterTestData, UnitSpec}
import edu.ie3.util.quantities.dep.PowerSystemUnits.{
  DEGREE_GEOM,
  KILOVOLT,
  KILOVOLTAMPERE
}
import tec.uom.se.quantity.Quantities
import tec.uom.se.unit.MetricPrefix
import tec.uom.se.unit.Units.{OHM, PERCENT, SIEMENS}

class Transformer2wTypeConverterSpec extends UnitSpec with ConverterTestData {
  val testingTolerance = 1e-3

  val uuid: UUID = UUID.randomUUID()
  val validInput: Transformer2WType = getTransformer2WTypePair("test type")._1

  "The two winding transformer converter" should {
    "convert a valid input correctly" in {
      val actual = Transformer2wTypeConverter.convert(validInput, uuid)

      actual.getUuid shouldBe uuid
      actual.getId shouldBe "test type"
      actual
        .getrSc()
        .subtract(Quantities.getQuantity(45.375, MetricPrefix.MILLI(OHM)))
        .to(MetricPrefix.MILLI(OHM))
        .getValue
        .doubleValue() < testingTolerance shouldBe true
      actual
        .getxSc()
        .subtract(Quantities.getQuantity(15.1249319, OHM))
        .to(OHM)
        .getValue
        .doubleValue() < testingTolerance shouldBe true
      actual
        .getsRated()
        .subtract(Quantities.getQuantity(40000d, KILOVOLTAMPERE))
        .to(KILOVOLTAMPERE)
        .getValue
        .doubleValue() < testingTolerance shouldBe true
      actual
        .getvRatedA()
        .subtract(Quantities.getQuantity(110d, KILOVOLT))
        .to(KILOVOLT)
        .getValue
        .doubleValue() < testingTolerance shouldBe true
      actual
        .getvRatedB()
        .subtract(Quantities.getQuantity(10d, KILOVOLT))
        .to(KILOVOLT)
        .getValue
        .doubleValue() < testingTolerance shouldBe true
      actual
        .getgM()
        .subtract(Quantities.getQuantity(2480.5790, MetricPrefix.NANO(SIEMENS)))
        .to(MetricPrefix.NANO(SIEMENS))
        .getValue
        .doubleValue() < testingTolerance shouldBe true
      actual
        .getbM()
        .subtract(
          Quantities.getQuantity(33047.51904649951, MetricPrefix.NANO(SIEMENS))
        )
        .to(MetricPrefix.NANO(SIEMENS))
        .getValue
        .doubleValue() < testingTolerance shouldBe true
      actual
        .getdV()
        .subtract(Quantities.getQuantity(2.5, PERCENT))
        .to(PERCENT)
        .getValue
        .doubleValue() < testingTolerance shouldBe true
      actual
        .getdPhi()
        .subtract(Quantities.getQuantity(5d, DEGREE_GEOM))
        .to(DEGREE_GEOM)
        .getValue
        .doubleValue() < testingTolerance shouldBe true
      actual.isTapSide shouldBe false
      actual.getTapNeutr shouldBe 0
      actual.getTapMin shouldBe -10
      actual.getTapMax shouldBe 10
    }

    "throw an exception, when the input model does not allow to calculate short circuit parameters correctly" in {
      val invalidInput = validInput.copy(pCu = 3000)
      val thrown = intercept[ConversionException](
        Transformer2wTypeConverter.convert(invalidInput, uuid)
      )
      thrown.getMessage shouldBe "Cannot convert two winding transformer type test type into ie3 type, as the " +
        "" +
        "conversion of short circuit parameters is not possible."
    }

    "throw an exception, when the input model does not allow to calculate no load circuit parameters correctly" in {
      val invalidInput = validInput.copy(pFe = 1000)
      val thrown = intercept[ConversionException](
        Transformer2wTypeConverter.convert(invalidInput, uuid)
      )
      thrown.getMessage shouldBe "Cannot convert two winding transformer type test type into ie3 type, as the " +
        "conversion of no load parameters is not possible."
    }
  }
}
