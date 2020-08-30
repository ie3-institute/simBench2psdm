/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.simbench.model

import edu.ie3.simbench.exception.io.SimbenchDataModelException
import edu.ie3.simbench.model.datamodel.enums.BranchElementPort
import edu.ie3.simbench.model.datamodel.types.Transformer2WType
import edu.ie3.simbench.model.datamodel.{Node, Substation, Transformer2W}
import edu.ie3.test.common.{ConverterTestData, UnitSpec}

class Transformer2WSpec extends UnitSpec with ConverterTestData {
  val nodeMapping: Map[String, Node] = Map(
    "MV1.101 Bus 4" -> getNodePair("MV1.101 Bus 4")._1,
    "LV1.101 Bus 4" -> getNodePair("LV1.101 Bus 4")._1
  )

  val substations = Map(
    "substation_1" -> Substation("substation_1", "LV1.101", 7)
  )

  val typeMapping = Map(
    "0.16 MVA 20/0.4 kV DOTE 160/20  SGB" -> Transformer2WType(
      "0.16 MVA 20/0.4 kV DOTE 160/20  SGB",
      0.16,
      20,
      0.4,
      150,
      4,
      2.35,
      0.46,
      0.28751,
      tapable = true,
      BranchElementPort.HV,
      2.5,
      0,
      0,
      -2,
      2
    )
  )

  val rawData = Vector(
    RawModelData(
      classOf[Transformer2W],
      Map(
        "id" -> "MV1.101-LV1.101-Trafo 1",
        "nodeHV" -> "MV1.101 Bus 4",
        "nodeLV" -> "LV1.101 Bus 4",
        "type" -> "0.16 MVA 20/0.4 kV DOTE 160/20  SGB",
        "tappos" -> "0",
        "autoTap" -> "1",
        "autoTapSide" -> "HV",
        "loadingMax" -> "100",
        "substation" -> "substation_1",
        "subnet" -> "LV1.101",
        "voltLvl" -> "6"
      )
    ),
    RawModelData(
      classOf[Transformer2W],
      Map(
        "id" -> "MV1.101-LV1.101-Trafo 1",
        "nodeHV" -> "MV1.101 Bus 4",
        "nodeLV" -> "LV1.101 Bus 4",
        "type" -> "0.16 MVA 20/0.4 kV DOTE 160/20  SGB",
        "tappos" -> "0",
        "autoTap" -> "0",
        "autoTapSide" -> "NULL",
        "loadingMax" -> "100",
        "substation" -> "NULL",
        "subnet" -> "LV1.101",
        "voltLvl" -> "6"
      )
    )
  )

  val expected = Vector(
    Transformer2W(
      "MV1.101-LV1.101-Trafo 1",
      getNodePair("MV1.101 Bus 4")._1,
      getNodePair("LV1.101 Bus 4")._1,
      Transformer2WType(
        "0.16 MVA 20/0.4 kV DOTE 160/20  SGB",
        0.16,
        20,
        0.4,
        150,
        4,
        2.35,
        0.46,
        0.28751,
        tapable = true,
        BranchElementPort.HV,
        2.5,
        0,
        0,
        -2,
        2
      ),
      0,
      autoTap = true,
      Some(BranchElementPort.HV),
      100,
      Some(Substation("substation_1", "LV1.101", 7)),
      "LV1.101",
      6
    ),
    Transformer2W(
      "MV1.101-LV1.101-Trafo 1",
      getNodePair("MV1.101 Bus 4")._1,
      getNodePair("LV1.101 Bus 4")._1,
      Transformer2WType(
        "0.16 MVA 20/0.4 kV DOTE 160/20  SGB",
        0.16,
        20,
        0.4,
        150,
        4,
        2.35,
        0.46,
        0.28751,
        tapable = true,
        BranchElementPort.HV,
        2.5,
        0,
        0,
        -2,
        2
      ),
      0,
      autoTap = false,
      None,
      100,
      None,
      "LV1.101",
      6
    )
  )

  "The two winding transformer object" should {
    "throw an exception, when the basic single model creation method is called" in {
      val thrown =
        intercept[SimbenchDataModelException](Transformer2W.apply(rawData(0)))
      thrown.getMessage shouldBe "No basic implementation of model creation available for Transformer2W$"
    }

    "throw an exception, when the basic batch model creation method is called" in {
      val thrown =
        intercept[SimbenchDataModelException](
          Transformer2W.buildModels(rawData)
        )
      thrown.getMessage shouldBe "No basic implementation of model creation available for Transformer2W$"
    }

    "build the correct single model" in {
      val actual = Transformer2W.buildModel(
        rawData(0),
        nodeMapping.getOrElse(
          "MV1.101 Bus 4",
          throw SimbenchDataModelException(
            "Ooops. This is not supposed to happen"
          )
        ),
        nodeMapping.getOrElse(
          "LV1.101 Bus 4",
          throw SimbenchDataModelException(
            "Ooops. This is not supposed to happen"
          )
        ),
        typeMapping.getOrElse(
          "0.16 MVA 20/0.4 kV DOTE 160/20  SGB",
          throw SimbenchDataModelException(
            "Ooops. This is not supposed to happen"
          )
        ),
        substations.get("substation_1")
      )
      actual shouldBe expected(0)
    }

    "build a correct vector of models" in {
      val actual =
        Transformer2W.buildModels(
          rawData,
          nodeMapping,
          typeMapping,
          substations
        )
      actual shouldBe expected
    }
  }
}
