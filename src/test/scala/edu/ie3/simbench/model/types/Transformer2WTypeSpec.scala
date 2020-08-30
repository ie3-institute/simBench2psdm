/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.simbench.model.types

import edu.ie3.simbench.model.RawModelData
import edu.ie3.simbench.model.datamodel.enums.BranchElementPort
import edu.ie3.simbench.model.datamodel.types.Transformer2WType
import edu.ie3.test.common.UnitSpec

class Transformer2WTypeSpec extends UnitSpec {
  val rawData = Vector(
    RawModelData(
      classOf[Transformer2WType],
      Map(
        "id" -> "0.63 MVA 20/0.4 kV Dyn5 ASEA",
        "sR" -> "0.63",
        "vmHV" -> "20",
        "vmLV" -> "0.4",
        "va0" -> "150",
        "vmImp" -> "6",
        "pCu" -> "6.9",
        "pFe" -> "1.65",
        "iNoLoad" -> "0.261915",
        "tapable" -> "1",
        "tapside" -> "HV",
        "dVm" -> "2.5",
        "dVa" -> "0",
        "tapNeutr" -> "0",
        "tapMin" -> "-2",
        "tapMax" -> "2"
      )
    ),
    RawModelData(
      classOf[Transformer2WType],
      Map(
        "id" -> "25 MVA 110/20 kV YNd5",
        "sR" -> "25",
        "vmHV" -> "110",
        "vmLV" -> "20",
        "va0" -> "150",
        "vmImp" -> "12",
        "pCu" -> "102.5",
        "pFe" -> "14",
        "iNoLoad" -> "0.07",
        "tapable" -> "1",
        "tapside" -> "HV",
        "dVm" -> "1.5",
        "dVa" -> "0",
        "tapNeutr" -> "0",
        "tapMin" -> "-9",
        "tapMax" -> "9"
      )
    )
  )

  val expected = Vector(
    Transformer2WType(
      "0.63 MVA 20/0.4 kV Dyn5 ASEA",
      0.63,
      20,
      0.4,
      150,
      6,
      6.9,
      1.65,
      0.261915,
      tapable = true,
      BranchElementPort.HV,
      2.5,
      0,
      0,
      -2,
      2
    ),
    Transformer2WType(
      "25 MVA 110/20 kV YNd5",
      25,
      110,
      20,
      150,
      12,
      102.5,
      14,
      0.07,
      tapable = true,
      BranchElementPort.HV,
      1.5,
      0,
      0,
      -9,
      9
    )
  )

  "The Transformer2W object" should {
    "build the correct single model" in {
      val actual = Transformer2WType.apply(rawData(0))
      actual shouldBe expected(0)
    }
    "build a correct vector of line types" in {
      val actual = Transformer2WType.buildModels(rawData)
      actual shouldBe expected
    }
  }
}
