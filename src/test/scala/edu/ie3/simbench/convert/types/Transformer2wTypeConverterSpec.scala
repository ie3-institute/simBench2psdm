package edu.ie3.simbench.convert.types

import java.util.UUID
import edu.ie3.simbench.exception.ConversionException
import edu.ie3.simbench.model.datamodel.types.Transformer2WType
import edu.ie3.test.common.{ConverterTestData, UnitSpec}
import edu.ie3.test.matchers.QuantityMatchers
import edu.ie3.util.quantities.PowerSystemUnits.{
  DEGREE_GEOM,
  KILOVOLT,
  KILOVOLTAMPERE
}
import tech.units.indriya.quantity.Quantities

import javax.measure.MetricPrefix
import tech.units.indriya.unit.Units.{OHM, PERCENT, SIEMENS}

class Transformer2wTypeConverterSpec
    extends UnitSpec
    with ConverterTestData
    with QuantityMatchers {
  implicit val quantityMatchingTolerance: Double = 1e-3

  val uuid: UUID = UUID.randomUUID()
  val validInput: Transformer2WType = getTransformer2WTypePair("test type")._1

  "The two winding transformer converter" should {
    "convert a valid input correctly" in {
      val actual = Transformer2wTypeConverter.convert(validInput, uuid)

      actual.getUuid shouldBe uuid
      actual.getId shouldBe "test type"
      actual.getrSc() should equalWithTolerance(
        Quantities.getQuantity(45.375, MetricPrefix.MILLI(OHM)).to(OHM)
      )
      actual.getxSc() should equalWithTolerance(
        Quantities.getQuantity(15.1249319, OHM)
      )
      actual.getsRated() should equalWithTolerance(
        Quantities.getQuantity(40000d, KILOVOLTAMPERE)
      )
      actual.getvRatedA() should equalWithTolerance(
        Quantities.getQuantity(110d, KILOVOLT)
      )
      actual.getvRatedB() should equalWithTolerance(
        Quantities.getQuantity(10d, KILOVOLT)
      )
      actual.getgM() should equalWithTolerance(
        Quantities.getQuantity(826.44628, MetricPrefix.NANO(SIEMENS))
      )
      actual.getbM() should equalWithTolerance(
        Quantities
          .getQuantity(33.0475190, MetricPrefix.MICRO(SIEMENS))
          .to(MetricPrefix.NANO(SIEMENS))
      )
      actual.getdV() should equalWithTolerance(
        Quantities.getQuantity(2.5, PERCENT)
      )
      actual.getdPhi() should equalWithTolerance(
        Quantities.getQuantity(5d, DEGREE_GEOM)
      )
      actual.isTapSide shouldBe false
      actual.getTapNeutr shouldBe 0
      actual.getTapMin shouldBe -10
      actual.getTapMax shouldBe 10
    }

    "throw an exception, when the input model does not allow to calculate short circuit parameters correctly" in {
      val invalidInput = validInput.copy(pCu = BigDecimal("3000"))
      val thrown = intercept[ConversionException](
        Transformer2wTypeConverter.convert(invalidInput, uuid)
      )
      thrown.getMessage shouldBe "Cannot convert two winding transformer type test type into ie3 type, as the " +
        "" +
        "conversion of short circuit parameters is not possible."
    }

    "throw an exception, when the input model does not allow to calculate no load circuit parameters correctly" in {
      val invalidInput = validInput.copy(pFe = BigDecimal("1000"))
      val thrown = intercept[ConversionException](
        Transformer2wTypeConverter.convert(invalidInput, uuid)
      )
      thrown.getMessage shouldBe "Cannot convert two winding transformer type test type into ie3 type, as the " +
        "conversion of no load parameters is not possible."
    }
  }
}
