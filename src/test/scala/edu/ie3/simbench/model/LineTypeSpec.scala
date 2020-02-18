package edu.ie3.simbench.model

import edu.ie3.simbench.model.datamodel.enums.LineStyle
import edu.ie3.simbench.model.datamodel.types.LineType
import edu.ie3.simbench.model.datamodel.types.LineType.ACLineType
import edu.ie3.test.common.UnitSpec

class LineTypeSpec extends UnitSpec {
  val rawData = Vector(
    RawModelData(classOf[LineType],
                 Map(
                   "id" -> "1x630_RM/50",
                   "r" -> "0.122",
                   "x" -> "0.122522",
                   "b" -> "58.7478",
                   "iMax" -> "652",
                   "type" -> "cable"
                 )),
    RawModelData(classOf[LineType],
                 Map("id" -> "24-AL1/4-ST1A 20.0",
                     "r" -> "1.2012",
                     "x" -> "0.394",
                     "b" -> "3.53429",
                     "iMax" -> "140",
                     "type" -> "ohl"))
  )

  "The LineType object" should {
    "build the correct single model" in {
      val actual = LineType.buildModel(rawData(0))
      val expected = ACLineType("1x630_RM/50",
                                BigDecimal("0.122"),
                                BigDecimal("0.122522"),
                                BigDecimal("58.7478"),
                                BigDecimal("652"),
                                LineStyle.Cable)
      actual shouldBe expected
    }

    "build a correct vector of line types" in {
      val actual = LineType.buildModels(rawData)
      val expected = Vector(
        ACLineType("1x630_RM/50",
                   BigDecimal("0.122"),
                   BigDecimal("0.122522"),
                   BigDecimal("58.7478"),
                   BigDecimal("652"),
                   LineStyle.Cable),
        ACLineType("24-AL1/4-ST1A 20.0",
                   BigDecimal("1.2012"),
                   BigDecimal("0.394"),
                   BigDecimal("3.53429"),
                   BigDecimal("140"),
                   LineStyle.OverheadLine)
      )
      actual shouldBe expected
    }
  }
}
