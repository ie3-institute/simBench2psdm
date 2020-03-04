package edu.ie3.simbench.model

import edu.ie3.simbench.model.datamodel.Coordinate
import edu.ie3.test.common.UnitSpec

class CoordinateSpec extends UnitSpec {
  val rawData = Vector(
    RawModelData(classOf[Coordinate],
                 Map("id" -> "coord_3",
                     "x" -> "11.4097",
                     "y" -> "53.6413",
                     "subnet" -> "LV1.101",
                     "voltLvl" -> "7")),
    RawModelData(classOf[Coordinate],
                 Map(
                   "id" -> "coord_14",
                   "x" -> "11.4097",
                   "y" -> "53.6413",
                   "subnet" -> "MV1.101_LV1.101_Feeder1",
                   "voltLvl" -> "5"
                 ))
  )

  val expected = Vector(
    Coordinate(
      "coord_3",
      BigDecimal("11.4097"),
      BigDecimal("53.6413"),
      "LV1.101",
      7
    ),
    Coordinate(
      "coord_14",
      BigDecimal("11.4097"),
      BigDecimal("53.6413"),
      "MV1.101_LV1.101_Feeder1",
      5
    )
  )

  "The Coordinate object" should {
    "build the correct single model" in {
      val actual = Coordinate.apply(rawData(0))
      actual shouldBe expected(0)
    }

    "build a correct vector of line types" in {
      val actual = Coordinate.buildModels(rawData)
      actual shouldBe expected
    }
  }
}
