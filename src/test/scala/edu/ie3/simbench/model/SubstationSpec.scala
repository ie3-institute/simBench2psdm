package edu.ie3.simbench.model

import edu.ie3.simbench.model.datamodel.{Node, Substation}
import edu.ie3.test.common.UnitSpec

class SubstationSpec extends UnitSpec {
  val rawData = Vector(
    RawModelData(
      classOf[Node],
      Map(
        "id" -> "MV1.101 Bus 4",
        "type" -> "busbar",
        "vmSetp" -> "1.025",
        "vaSetp" -> "0.0",
        "vmR" -> "20",
        "vmRMin" -> "0.965",
        "vmRMax" -> "1.055",
        "substation" -> "NULL",
        "coordID" -> "coord_14",
        "subnet" -> "MV1.101_LV1.101_Feeder1",
        "voltLvl" -> "5"
      )
    ),
    RawModelData(
      classOf[Node],
      Map(
        "id" -> "LV1.101 Bus 1",
        "type" -> "busbar",
        "vmSetp" -> "NULL",
        "vaSetp" -> "NULL",
        "vmR" -> "0.4",
        "vmRMin" -> "0.9",
        "vmRMax" -> "1.1",
        "substation" -> "substation_1",
        "coordID" -> "coord_0",
        "subnet" -> "LV1.101",
        "voltLvl" -> "7"
      )
    )
  )

  val expected = Vector(
    Substation("substation_1", "LV1.101", 7)
  )

  "The Substation object" should {
    "build the correct single model" in {
      val actual = Substation.buildModel(rawData(1))
      actual shouldBe expected(0)
    }

    "build a correct vector of line types" in {
      val actual = Substation.buildModels(rawData)
      actual shouldBe expected
    }
  }
}
