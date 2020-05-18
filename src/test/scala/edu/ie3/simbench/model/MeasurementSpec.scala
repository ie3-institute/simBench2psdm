package edu.ie3.simbench.model

import edu.ie3.simbench.exception.io.SimbenchDataModelException
import edu.ie3.simbench.model.datamodel.Line.ACLine
import edu.ie3.simbench.model.datamodel.Measurement.{
  LineMeasurement,
  NodeMeasurement,
  TransformerMeasurement
}
import edu.ie3.simbench.model.datamodel.enums.{
  BranchElementPort,
  MeasurementVariable
}
import edu.ie3.simbench.model.datamodel.types.LineType.ACLineType
import edu.ie3.simbench.model.datamodel.types.Transformer2WType
import edu.ie3.simbench.model.datamodel.{Measurement, Substation, Transformer2W}
import edu.ie3.test.common.{ConverterTestData, UnitSpec}

class MeasurementSpec extends UnitSpec with ConverterTestData {
  val nodeMapping = Map(
    "LV1.101 Bus 1" -> getNodePair("LV1.101 Bus 1")._1
  )

  val lines = Map(
    "LV1.101 Line 10" ->
      ACLine(
        "LV1.101 Line 10",
        getNodePair("LV1.101 Bus 4")._1,
        getNodePair("LV1.101 Bus 1")._1,
        getLineTypePair("NAYY 4x150SE 0.6/1kV")._1.asInstanceOf[ACLineType],
        BigDecimal("0.132499"),
        BigDecimal("100"),
        "LV1.101",
        7
      )
  )

  val transformers = Map(
    "MV1.101-LV1.101-Trafo 1" ->
      Transformer2W(
        "MV1.101-LV1.101-Trafo 1",
        getNodePair("MV1.101 Bus 4")._1,
        getNodePair("LV1.101 Bus 4")._1,
        Transformer2WType(
          "0.16 MVA 20/0.4 kV DOTE 160/20  SGB",
          BigDecimal("0.16"),
          BigDecimal("20"),
          BigDecimal("0.4"),
          BigDecimal("150"),
          BigDecimal("4"),
          BigDecimal("2.35"),
          BigDecimal("0.46"),
          BigDecimal("0.28751"),
          tapable = true,
          BranchElementPort.HV,
          BigDecimal("2.5"),
          BigDecimal("0"),
          0,
          -2,
          2
        ),
        0,
        autoTap = true,
        Some(BranchElementPort.HV),
        BigDecimal("100"),
        Some(Substation("substation_1", "LV1.101", 7)),
        "LV1.101",
        6
      )
  )

  val rawData = Vector(
    RawModelData(
      classOf[Measurement],
      Map(
        "id" -> "LV1.101 Measurement 1",
        "element1" -> "LV1.101 Bus 1",
        "element2" -> "NULL",
        "variable" -> "v",
        "subnet" -> "MV4.101",
        "voltLvl" -> "7"
      )
    ),
    RawModelData(
      classOf[Measurement],
      Map(
        "id" -> "LV1.101 Measurement 1",
        "element1" -> "LV1.101 Bus 1",
        "element2" -> "LV1.101 Line 10",
        "variable" -> "p",
        "subnet" -> "MV4.101",
        "voltLvl" -> "7"
      )
    ),
    RawModelData(
      classOf[Measurement],
      Map(
        "id" -> "LV1.101 Measurement 1",
        "element1" -> "LV1.101 Bus 1",
        "element2" -> "MV1.101-LV1.101-Trafo 1",
        "variable" -> "p",
        "subnet" -> "MV4.101",
        "voltLvl" -> "7"
      )
    )
  )

  val expected = Vector(
    NodeMeasurement(
      "LV1.101 Measurement 1",
      getNodePair("LV1.101 Bus 1")._1,
      MeasurementVariable.Voltage,
      "MV4.101",
      7
    ),
    LineMeasurement(
      "LV1.101 Measurement 1",
      ACLine(
        "LV1.101 Line 10",
        getNodePair("LV1.101 Bus 4")._1,
        getNodePair("LV1.101 Bus 1")._1,
        getLineTypePair("NAYY 4x150SE 0.6/1kV")._1.asInstanceOf[ACLineType],
        BigDecimal("0.132499"),
        BigDecimal("100"),
        "LV1.101",
        7
      ),
      getNodePair("LV1.101 Bus 1")._1,
      MeasurementVariable.ActivePower,
      "MV4.101",
      7
    ),
    TransformerMeasurement(
      "LV1.101 Measurement 1",
      Transformer2W(
        "MV1.101-LV1.101-Trafo 1",
        getNodePair("MV1.101 Bus 4")._1,
        getNodePair("LV1.101 Bus 4")._1,
        Transformer2WType(
          "0.16 MVA 20/0.4 kV DOTE 160/20  SGB",
          BigDecimal("0.16"),
          BigDecimal("20"),
          BigDecimal("0.4"),
          BigDecimal("150"),
          BigDecimal("4"),
          BigDecimal("2.35"),
          BigDecimal("0.46"),
          BigDecimal("0.28751"),
          tapable = true,
          BranchElementPort.HV,
          BigDecimal("2.5"),
          BigDecimal("0"),
          0,
          -2,
          2
        ),
        0,
        autoTap = true,
        Some(BranchElementPort.HV),
        BigDecimal("100"),
        Some(Substation("substation_1", "LV1.101", 7)),
        "LV1.101",
        6
      ),
      getNodePair("LV1.101 Bus 1")._1,
      MeasurementVariable.ActivePower,
      "MV4.101",
      7
    )
  )

  "The measurement object" should {
    "throw an exception, when the basic single model creation method is called" in {
      val thrown =
        intercept[SimbenchDataModelException](Measurement.apply(rawData(0)))
      thrown.getMessage shouldBe "No basic implementation of model creation available for Measurement$"
    }

    "throw an exception, when the basic batch model creation method is called" in {
      val thrown =
        intercept[SimbenchDataModelException](Measurement.buildModels(rawData))
      thrown.getMessage shouldBe "No basic implementation of model creation available for Measurement$"
    }

    /* No single model creation, as it would impose a lot of duplicate code */

    "build a correct vector of models" in {
      val actual =
        Measurement.buildModels(rawData, nodeMapping, lines, transformers)
      actual shouldBe expected
    }
  }
}
