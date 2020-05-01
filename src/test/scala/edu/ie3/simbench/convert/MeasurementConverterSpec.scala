package edu.ie3.simbench.convert

import java.util.{Objects, UUID}

import edu.ie3.datamodel.models.OperationTime
import edu.ie3.datamodel.models.input.{
  MeasurementUnitInput,
  NodeInput,
  OperatorInput
}
import edu.ie3.simbench.model.datamodel.Measurement.NodeMeasurement
import edu.ie3.simbench.model.datamodel.enums.MeasurementVariable.{
  Current,
  ReactivePower,
  Voltage
}
import edu.ie3.test.common.{ConverterTestData, UnitSpec}

class MeasurementConverterSpec extends UnitSpec with ConverterTestData {
  "The measurement unit converter" should {
    "convert a valid node measurement model correctly" in {
      val (input, expected) = getMeasurementPair("MV1.102 Measurement 1")
      val node = getNodePair("HV1 Bus 30")._2
      (MeasurementConverter.convert(input, node), Option(expected)) match {
        case (Some(actual), Some(expected)) =>
          Objects.isNull(actual.getUuid) shouldBe false
          actual.getId == expected.getId
          actual.getOperator == expected.getOperator
          actual.getOperationTime == expected.getOperationTime
          actual.getVMag == expected.getVMag
          actual.getVAng == expected.getVAng
          actual.getP == expected.getP
          actual.getQ == expected.getQ
        case (None, _) => fail("Successful conversion was expected.")
      }
    }

    "convert a valid line measurement model correctly" in {
      val (input, expected) = getMeasurementPair("MV1.102 Measurement 3")
      val node = mock[NodeInput]

      MeasurementConverter.convert(input, node) shouldBe Option(expected)
    }

    "convert a valid transformer measurement model correctly" in {
      val (input, expected) = getMeasurementPair("MV1.102 Measurement 28")
      val node = mock[NodeInput]

      MeasurementConverter.convert(input, node) shouldBe Option(expected)
    }

    "join a set of measurements correctly" in {
      val input = Vector(
        NodeMeasurement(
          "MV1.102 Measurement 1",
          getNodePair("HV1 Bus 30")._1,
          Voltage,
          "HV1_MV1.102",
          3
        ),
        NodeMeasurement(
          "MV1.102 Measurement 2",
          getNodePair("HV1 Bus 30")._1,
          ReactivePower,
          "HV1_MV1.102",
          3
        )
      )
      val nodes =
        Map(getNodePair("HV1 Bus 30")._1 -> getNodePair("HV1 Bus 30")._2)

      val actual = MeasurementConverter.convert(input, nodes)

      actual.size shouldBe 1
      actual.headOption match {
        case Some(measurement) =>
          measurement.getId shouldBe "MV1.102 Measurement 1"
          measurement.getVMag shouldBe true
          measurement.getVAng shouldBe true
          measurement.getP shouldBe false
          measurement.getQ shouldBe true
        case None => fail("At leas one converted measurement is expected")
      }
    }

    "join a set of measurements correctly, that are able to measure power by current and voltage" in {
      val input = Vector(
        NodeMeasurement(
          "MV1.102 Measurement 1",
          getNodePair("HV1 Bus 30")._1,
          Voltage,
          "HV1_MV1.102",
          3
        ),
        NodeMeasurement(
          "MV1.102 Measurement 2",
          getNodePair("HV1 Bus 30")._1,
          Current,
          "HV1_MV1.102",
          3
        )
      )
      val nodes =
        Map(getNodePair("HV1 Bus 30")._1 -> getNodePair("HV1 Bus 30")._2)

      val actual = MeasurementConverter.convert(input, nodes)

      actual.size shouldBe 1
      actual.headOption match {
        case Some(measurement) =>
          measurement.getId shouldBe "MV1.102 Measurement 1"
          measurement.getVMag shouldBe true
          measurement.getVAng shouldBe true
          measurement.getP shouldBe true
          measurement.getQ shouldBe true
        case None => fail("At leas one converted measurement is expected")
      }
    }
  }
}
