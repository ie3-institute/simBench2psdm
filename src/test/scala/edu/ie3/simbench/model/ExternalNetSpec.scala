/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.simbench.model

import edu.ie3.simbench.exception.io.SimbenchDataModelException
import edu.ie3.simbench.model.datamodel.ExternalNet
import edu.ie3.simbench.model.datamodel.ExternalNet.{Simple, Ward, WardExtended}
import edu.ie3.simbench.model.datamodel.enums.CalculationType
import edu.ie3.test.common.{ConverterTestData, UnitSpec}

class ExternalNetSpec extends UnitSpec with ConverterTestData {
  val nodeMapping = Map(
    "MV1.101 Bus 4" ->
      getNodePair("MV1.101 Bus 4")._1
  )

  val rawData = Vector(
    RawModelData(
      classOf[ExternalNet],
      Map(
        "id" -> "MV1.101 grid at LV1.101",
        "node" -> "MV1.101 Bus 4",
        "calc_type" -> "vavm",
        "dspf" -> "1",
        "pExtNet" -> "NULL",
        "qExtNet" -> "NULL",
        "pWardShunt" -> "NULL",
        "qWardShunt" -> "NULL",
        "rXWard" -> "NULL",
        "xXWard" -> "NULL",
        "vmXWard" -> "NULL",
        "subnet" -> "LV1.101_MV1.101_eq",
        "voltLvl" -> "5"
      )
    ),
    RawModelData(
      classOf[ExternalNet],
      Map(
        "id" -> "MV1.101 grid at LV1.101",
        "node" -> "MV1.101 Bus 4",
        "calc_type" -> "ward",
        "dspf" -> "1",
        "pExtNet" -> "NULL",
        "qExtNet" -> "NULL",
        "pWardShunt" -> "100.0",
        "qWardShunt" -> "200.0",
        "rXWard" -> "NULL",
        "xXWard" -> "NULL",
        "vmXWard" -> "NULL",
        "subnet" -> "LV1.101_MV1.101_eq",
        "voltLvl" -> "5"
      )
    ),
    RawModelData(
      classOf[ExternalNet],
      Map(
        "id" -> "MV1.101 grid at LV1.101",
        "node" -> "MV1.101 Bus 4",
        "calc_type" -> "xWard",
        "dspf" -> "1",
        "pExtNet" -> "NULL",
        "qExtNet" -> "NULL",
        "pWardShunt" -> "NULL",
        "qWardShunt" -> "NULL",
        "rXWard" -> "1.0",
        "xXWard" -> "2.0",
        "vmXWard" -> "3.0",
        "subnet" -> "LV1.101_MV1.101_eq",
        "voltLvl" -> "5"
      )
    )
  )

  val expected = Vector(
    Simple(
      "MV1.101 grid at LV1.101",
      getNodePair("MV1.101 Bus 4")._1,
      CalculationType.VaVm,
      BigDecimal("1"),
      None,
      None,
      "LV1.101_MV1.101_eq",
      5
    ),
    Ward(
      "MV1.101 grid at LV1.101",
      getNodePair("MV1.101 Bus 4")._1,
      BigDecimal("1"),
      None,
      None,
      BigDecimal("100.0"),
      BigDecimal("200.0"),
      "LV1.101_MV1.101_eq",
      5
    ),
    WardExtended(
      "MV1.101 grid at LV1.101",
      getNodePair("MV1.101 Bus 4")._1,
      BigDecimal("1"),
      None,
      None,
      BigDecimal("1.0"),
      BigDecimal("2.0"),
      BigDecimal("3.0"),
      "LV1.101_MV1.101_eq",
      5
    )
  )

  "The external net object" should {
    "throw an exception, when the basic single model creation method is called" in {
      val thrown =
        intercept[SimbenchDataModelException](ExternalNet.apply(rawData(0)))
      thrown.getMessage shouldBe "No basic implementation of model creation available for ExternalNet$"
    }

    "throw an exception, when the basic batch model creation method is called" in {
      val thrown =
        intercept[SimbenchDataModelException](ExternalNet.buildModels(rawData))
      thrown.getMessage shouldBe "No basic implementation of model creation available for ExternalNet$"
    }

    "build the correct single model" in {
      val actual = ExternalNet.buildModel(
        rawData(0),
        nodeMapping.getOrElse(
          "MV1.101 Bus 4",
          throw SimbenchDataModelException(
            "Ooops. This is not supposed to happen"
          )
        )
      )
      actual shouldBe expected(0)
    }

    "build a correct vector of models" in {
      val actual =
        ExternalNet.buildModels(rawData, nodeMapping)
      actual shouldBe expected
    }
  }
}
