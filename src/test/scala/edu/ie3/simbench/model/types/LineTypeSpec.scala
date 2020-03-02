package edu.ie3.simbench.model.types

import edu.ie3.simbench.model.RawModelData
import edu.ie3.simbench.model.datamodel.types.LineType
import edu.ie3.simbench.model.datamodel.types.LineType.ACLineType
import edu.ie3.test.common.{ConverterTestData, UnitSpec}

class LineTypeSpec extends UnitSpec with ConverterTestData {
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
      val expected = getLineTypePair("1x630_RM/50")._1.asInstanceOf[ACLineType]
      actual shouldBe expected
    }

    "build a correct vector of line types" in {
      val actual = LineType.buildModels(rawData)
      val expected = Vector(
        getLineTypePair("1x630_RM/50")._1.asInstanceOf[ACLineType],
        getLineTypePair("24-AL1/4-ST1A 20.0")._1.asInstanceOf[ACLineType]
      )
      actual shouldBe expected
    }
  }
}
