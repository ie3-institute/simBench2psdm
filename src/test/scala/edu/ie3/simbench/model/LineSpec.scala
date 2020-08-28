/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.simbench.model

import edu.ie3.simbench.exception.io.SimbenchDataModelException
import edu.ie3.simbench.model.datamodel.Line
import edu.ie3.simbench.model.datamodel.Line.ACLine
import edu.ie3.test.common.{ConverterTestData, UnitSpec}

class LineSpec extends UnitSpec with ConverterTestData {
  val nodeMapping = Map(
    "LV1.101 Bus 1" -> getNodePair("LV1.101 Bus 1")._1,
    "LV1.101 Bus 4" -> getNodePair("LV1.101 Bus 4")._1
  )

  val lineTypeMapping = Map(
    "NAYY 4x150SE 0.6/1kV" -> getACLineTypes("NAYY 4x150SE 0.6/1kV")._1,
    "24-AL1/4-ST1A 20.0" -> getACLineTypes("24-AL1/4-ST1A 20.0")._1
  )

  val rawData = Vector(
    RawModelData(
      classOf[Line[_]],
      Map(
        "id" -> "LV1.101 Line 10",
        "nodeA" -> "LV1.101 Bus 4",
        "nodeB" -> "LV1.101 Bus 1",
        "type" -> "NAYY 4x150SE 0.6/1kV",
        "length" -> "0.132499",
        "loadingMax" -> "100",
        "subnet" -> "LV1.101",
        "voltLvl" -> "7"
      )
    ),
    RawModelData(
      classOf[Line[_]],
      Map(
        "id" -> "LV1.101 Line 10",
        "nodeA" -> "LV1.101 Bus 1",
        "nodeB" -> "LV1.101 Bus 4",
        "type" -> "24-AL1/4-ST1A 20.0",
        "length" -> "0.132499",
        "loadingMax" -> "100",
        "subnet" -> "LV1.101",
        "voltLvl" -> "7"
      )
    )
  )

  val expected = Vector(
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

  "The Line object" should {
    "throw an exception, when the basic single model creation method is called" in {
      val thrown =
        intercept[SimbenchDataModelException](Line.apply(rawData(0)))
      thrown.getMessage shouldBe "No basic implementation of model creation available for Line$"
    }

    "throw an exception, when the basic batch model creation method is called" in {
      val thrown =
        intercept[SimbenchDataModelException](Line.buildModels(rawData))
      thrown.getMessage shouldBe "No basic implementation of model creation available for Line$"
    }

    "build the correct single model" in {
      val actual = Line.buildModel(
        rawData(0),
        nodeMapping.getOrElse(
          "LV1.101 Bus 4",
          throw SimbenchDataModelException(
            "Ooops. This is not supposed to happen"
          )
        ),
        nodeMapping.getOrElse(
          "LV1.101 Bus 1",
          throw SimbenchDataModelException(
            "Ooops. This is not supposed to happen"
          )
        ),
        lineTypeMapping.getOrElse(
          "NAYY 4x150SE 0.6/1kV",
          throw SimbenchDataModelException(
            "Ooops. This is not supposed to happen"
          )
        )
      )
      actual shouldBe expected(0)
    }

    "build a correct vector of models" in {
      val actual = Line.buildModels(rawData, nodeMapping, lineTypeMapping)
      actual shouldBe expected
    }
  }
}
