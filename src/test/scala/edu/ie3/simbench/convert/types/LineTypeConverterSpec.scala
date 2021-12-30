package edu.ie3.simbench.convert.types

import java.util.UUID

import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.simbench.exception.ConversionException
import edu.ie3.simbench.exception.io.SimbenchDataModelException
import edu.ie3.simbench.model.datamodel.Line.ACLine
import edu.ie3.simbench.model.datamodel.types.LineType
import edu.ie3.simbench.model.datamodel.types.LineType.DCLineType
import edu.ie3.test.common.{ConverterTestData, UnitSpec}
import edu.ie3.util.quantities.PowerSystemUnits.KILOVOLT
import javax.measure.quantity.ElectricPotential
import tech.units.indriya.ComparableQuantity
import tech.units.indriya.quantity.Quantities

class LineTypeConverterSpec extends UnitSpec with ConverterTestData {
  val invalidLine: ACLine = ACLine(
    "LV1.101 Line 10",
    getNodePair("LV1.101 Bus 4")._1,
    getNodePair("LV1.101 Bus 1")._1.copy(vmR = BigDecimal("10")),
    getACLineTypes("NAYY 4x150SE 0.6/1kV")._1,
    BigDecimal("0.132499"),
    BigDecimal("100"),
    "LV1.101",
    7
  )

  val lineVec = Vector(
    ACLine(
      "LV1.101 Line 10",
      getNodePair("LV1.101 Bus 4")._1,
      getNodePair("LV1.101 Bus 1")._1,
      getACLineTypes("NAYY 4x150SE 0.6/1kV")._1,
      BigDecimal("0.132499"),
      BigDecimal("100"),
      "LV1.101",
      7
    ),
    ACLine(
      "LV1.101 Line 11",
      getNodePair("LV1.101 Bus 4")._1,
      getNodePair("LV1.101 Bus 1")._1,
      getACLineTypes("NAYY 4x150SE 0.6/1kV")._1,
      BigDecimal("0.132499"),
      BigDecimal("100"),
      "LV1.101",
      7
    ),
    ACLine(
      "LV1.101 Line 10",
      getNodePair("LV1.101 Bus 1")._1,
      getNodePair("LV1.101 Bus 4")._1,
      getACLineTypes("24-AL1/4-ST1A 20.0")._1,
      BigDecimal("0.132499"),
      BigDecimal("100"),
      "LV1.101",
      7
    )
  )
  val EHVlines = Vector(
   ACLine(
      "EHV Line 4",
      getNodePair("EHV Bus 49")._1,
      getNodePair("EHV Bus 59")._1,
      getACLineTypes("LineType_1")._1,
      BigDecimal("194.581"),
      BigDecimal("100"),
      "EHV 1",
     1
    ),
     ACLine(
      "EHV Line 273",
      getNodePair("EHV Bus 1433")._1,
      getNodePair("EHV Bus 1435")._1,
      getACLineTypes("LineType_1")._1,
      BigDecimal("43.396"),
      BigDecimal("100"),
       "EHV 1",
       1
    )
  )

  val uuid: UUID = UUID.randomUUID()

  val invalidInput: DCLineType = getDCLineTypes("dc line type")._1

  "The line type converter" should {
    val determineRatedVoltageMethod =
      PrivateMethod[(LineType, ComparableQuantity[ElectricPotential])](
        Symbol("determineRatedVoltage")
      )
    "extract the rated voltage of one line correctly" in {
      val actual = LineTypeConverter invokePrivate determineRatedVoltageMethod(
        lineVec(0)
      )
      actual shouldBe (getACLineTypes("NAYY 4x150SE 0.6/1kV")._1, Quantities
        .getQuantity(0.4, KILOVOLT))
    }

    "throw an exception, if the rated voltage is ambiguous" in {
      val thrown = intercept[SimbenchDataModelException](
        LineTypeConverter invokePrivate determineRatedVoltageMethod(invalidLine)
      )
      thrown.getMessage shouldBe "The line LV1.101 Line 10 connects two nodes with different rated voltages, which " +
        "physically is not possible"
    }

    "build line type to rated voltage mapping correctly" in {
      val actual = LineTypeConverter.assignRatedVoltages(lineVec)
      val expected = Map(
        getACLineTypes("NAYY 4x150SE 0.6/1kV")._1 -> Quantities
          .getQuantity(0.4, KILOVOLT),
        getACLineTypes("24-AL1/4-ST1A 20.0")._1 -> Quantities
         .getQuantity(0.4, KILOVOLT)
      )
      actual.toSet shouldBe expected.toSet
    }

    "convert a valid input correctly" in {
      val actual =
        LineTypeConverter.convert(
          getACLineTypes("NAYY 4x150SE 0.6/1kV")._1,
          Quantities.getQuantity(0.4, KILOVOLT),
          uuid
        )

      actual.getUuid shouldBe uuid
      actual.getId shouldBe "NAYY 4x150SE 0.6/1kV"
      actual.getB shouldBe Quantities
        .getQuantity(260.752, StandardUnits.ADMITTANCE_PER_LENGTH)
      actual.getG shouldBe Quantities.getQuantity(
        0d,
        StandardUnits.ADMITTANCE_PER_LENGTH
      )
      actual.getR shouldBe Quantities.getQuantity(
        0.2067,
        StandardUnits.IMPEDANCE_PER_LENGTH
      )
      actual.getX shouldBe Quantities.getQuantity(
        0.0804248,
        StandardUnits.IMPEDANCE_PER_LENGTH
      )
      actual.getiMax shouldBe Quantities.getQuantity(
        270d,
        StandardUnits.ELECTRIC_CURRENT_MAGNITUDE
      )
      actual.getvRated shouldBe Quantities.getQuantity(
        0.4,
        StandardUnits.RATED_VOLTAGE_MAGNITUDE
      )
    }

    "throw an exception on wrong input type" in {
      val thrown = intercept[ConversionException](
        LineTypeConverter
          .convert(invalidInput, Quantities.getQuantity(0.4, KILOVOLT), uuid)
      )
      thrown.getMessage shouldBe "DC line types are currently not supported by ie3's data model."
    }

    "extract the rated voltage of lines with same line type but different rated voltages correctly" in {
      val actual = LineTypeConverter invokePrivate determineRatedVoltageMethod(
        EHVlines(0)
      )
      actual shouldBe (getACLineTypes("LineType_1")._1, Quantities
        .getQuantity(380, KILOVOLT))
      val actualVal = LineTypeConverter invokePrivate determineRatedVoltageMethod(
        EHVlines(1)
      )
      actualVal shouldBe (getACLineTypes("LineType_1")._1, Quantities
        .getQuantity(220, KILOVOLT))
    }

  }
}
