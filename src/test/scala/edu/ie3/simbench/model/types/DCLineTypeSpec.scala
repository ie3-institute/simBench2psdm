/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.simbench.model.types

import edu.ie3.simbench.model.RawModelData
import edu.ie3.simbench.model.datamodel.types.LineType
import edu.ie3.simbench.model.datamodel.types.LineType.{ACLineType, DCLineType}
import edu.ie3.test.common.{ConverterTestData, UnitSpec}

class DCLineTypeSpec extends UnitSpec with ConverterTestData {
  val rawData = Vector(
    RawModelData(
      classOf[LineType],
      Map(
        "id" -> "dcline 1_type",
        "pDCLine" -> "0",
        "relPLosses" -> "1.2",
        "fixPLosses" -> "0",
        "pMax" -> "NULL",
        "qMinA" -> "NULL",
        "qMaxA" -> "NULL",
        "qMinB" -> "NULL",
        "qMaxB" -> "NULL"
      )
    ),
    RawModelData(
      classOf[LineType],
      Map(
        "id" -> "dcline 2_type",
        "pDCLine" -> "0",
        "relPLosses" -> "1.2",
        "fixPLosses" -> "0",
        "pMax" -> "100.1",
        "qMinA" -> "-50.5",
        "qMaxA" -> "50.5",
        "qMinB" -> "-25.6",
        "qMaxB" -> "25.6"
      )
    )
  )

  "The DCLineType object" should {
    "build the correct single model" in {
      val actual = DCLineType.apply(rawData(0))
      val expected = getDCLineTypes("dcline 1_type")._1
      actual shouldBe expected
    }

    "build a correct vector of line types" in {
      val actual = DCLineType.buildModels(rawData)
      val expected = Vector(
        getDCLineTypes("dcline 1_type")._1,
        getDCLineTypes("dcline 2_type")._1
      )
      actual shouldBe expected
    }
  }
}
