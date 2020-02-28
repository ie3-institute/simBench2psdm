package edu.ie3.simbench.convert.types

import java.util.UUID

import edu.ie3.simbench.exception.ConversionException
import edu.ie3.simbench.model.datamodel.enums.BranchElementPort.HV
import edu.ie3.simbench.model.datamodel.types.Transformer2WType
import edu.ie3.test.common.UnitSpec
import edu.ie3.util.quantities.PowerSystemUnits.{
  DEGREE_GEOM,
  KILOVOLT,
  KILOVOLTAMPERE
}
import tec.uom.se.quantity.Quantities
import tec.uom.se.unit.MetricPrefix
import tec.uom.se.unit.Units.{OHM, PERCENT, SIEMENS}

class Transformer2wTypeConverterSpec extends UnitSpec {
  val testingTolerance = 1E-3

  val uuid: UUID = UUID.randomUUID()
  val validInput: Transformer2WType = Transformer2WType(
    "test type",
    BigDecimal("40"),
    BigDecimal("110"),
    BigDecimal("10"),
    BigDecimal("150"),
    BigDecimal("5"),
    BigDecimal("6"),
    BigDecimal("10"),
    BigDecimal("1"),
    tapable = true,
    HV,
    BigDecimal("0.025"),
    BigDecimal("5"),
    0,
    -10,
    10
  )

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
          Quantities.getQuantity(32972.94113, MetricPrefix.NANO(SIEMENS)))
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
      val invalidInput = validInput.copy(pCu = BigDecimal("3000"))
      val thrown = intercept[ConversionException](
        Transformer2wTypeConverter.convert(invalidInput, uuid))
      thrown.getMessage shouldBe "Cannot convert two winding transformer type test type into ie³ type, as the " +
        "" +
        "conversion of short circuit parameters is not possible."
    }

    "throw an exception, when the input model does not allow to calculate no load circuit parameters correctly" in {
      val invalidInput = validInput.copy(pFe = BigDecimal("150"))
      val thrown = intercept[ConversionException](
        Transformer2wTypeConverter.convert(invalidInput, uuid))
      thrown.getMessage shouldBe "Cannot convert two winding transformer type test type into ie³ type, as the " +
        "conversion of no load parameters is not possible."
    }
  }
}
