package edu.ie3.simbench.model

import edu.ie3.simbench.exception.io.SimbenchDataModelException
import edu.ie3.simbench.model.datamodel.enums.{
  CalculationType,
  NodeType,
  PowerPlantType
}
import edu.ie3.simbench.model.datamodel.profiles.PowerPlantProfileType
import edu.ie3.simbench.model.datamodel.{
  Coordinate,
  Node,
  PowerPlant,
  Substation
}
import edu.ie3.test.common.UnitSpec

class PowerPlantSpec extends UnitSpec {
  val nodes = Map(
    "MV1.101 Bus 4" -> Node(
      "MV1.101 Bus 4",
      NodeType.BusBar,
      Some(BigDecimal("1.025")),
      Some(BigDecimal("0.0")),
      BigDecimal("20"),
      BigDecimal("0.965"),
      BigDecimal("1.055"),
      Some(Substation("substation_1", "LV1.101", 7)),
      Some(
        Coordinate(
          "coord_14",
          BigDecimal("11.4097"),
          BigDecimal("53.6413"),
          "MV1.101_LV1.101_Feeder1",
          5
        )
      ),
      "MV1.101_LV1.101_Feeder1",
      5
    )
  )

  val rawData = Vector(
    RawModelData(
      classOf[PowerPlant],
      Map(
        "id" -> "EHV Gen 75",
        "node" -> "MV1.101 Bus 4",
        "type" -> "hard coal",
        "profile" -> "pp_75",
        "calc_type" -> "pvm",
        "dspf" -> "1",
        "pPP" -> "778",
        "qPP" -> "NULL",
        "sR" -> "818.947",
        "pMin" -> "50",
        "pMax" -> "778",
        "qMin" -> "-255.716",
        "qMax" -> "255.716",
        "subnet" -> "EHV1",
        "voltLvl" -> "1"
      )
    ),
    RawModelData(
      classOf[PowerPlant],
      Map(
        "id" -> "EHV Gen 76",
        "node" -> "MV1.101 Bus 4",
        "type" -> "hard coal",
        "profile" -> "pp_76",
        "calc_type" -> "pvm",
        "dspf" -> "1",
        "pPP" -> "449",
        "qPP" -> "-42",
        "sR" -> "472.632",
        "pMin" -> "50",
        "pMax" -> "449",
        "qMin" -> "-147.579",
        "qMax" -> "147.579",
        "subnet" -> "EHV1",
        "voltLvl" -> "1"
      )
    )
  )

  val expected = Vector(
    PowerPlant(
      "EHV Gen 75",
      Node(
        "MV1.101 Bus 4",
        NodeType.BusBar,
        Some(BigDecimal("1.025")),
        Some(BigDecimal("0.0")),
        BigDecimal("20"),
        BigDecimal("0.965"),
        BigDecimal("1.055"),
        Some(Substation("substation_1", "LV1.101", 7)),
        Some(
          Coordinate(
            "coord_14",
            BigDecimal("11.4097"),
            BigDecimal("53.6413"),
            "MV1.101_LV1.101_Feeder1",
            5
          )
        ),
        "MV1.101_LV1.101_Feeder1",
        5
      ),
      PowerPlantType("hard coal"),
      PowerPlantProfileType("pp_75"),
      CalculationType("pvm"),
      BigDecimal("1"),
      BigDecimal("778"),
      None,
      BigDecimal("818.947"),
      BigDecimal("50"),
      BigDecimal("778"),
      BigDecimal("-255.716"),
      BigDecimal("255.716"),
      "EHV1",
      1
    ),
    PowerPlant(
      "EHV Gen 76",
      Node(
        "MV1.101 Bus 4",
        NodeType.BusBar,
        Some(BigDecimal("1.025")),
        Some(BigDecimal("0.0")),
        BigDecimal("20"),
        BigDecimal("0.965"),
        BigDecimal("1.055"),
        Some(Substation("substation_1", "LV1.101", 7)),
        Some(
          Coordinate(
            "coord_14",
            BigDecimal("11.4097"),
            BigDecimal("53.6413"),
            "MV1.101_LV1.101_Feeder1",
            5
          )
        ),
        "MV1.101_LV1.101_Feeder1",
        5
      ),
      PowerPlantType("hard coal"),
      PowerPlantProfileType("pp_76"),
      CalculationType("pvm"),
      BigDecimal("1"),
      BigDecimal("449"),
      Some(BigDecimal("-42")),
      BigDecimal("472.632"),
      BigDecimal("50"),
      BigDecimal("449"),
      BigDecimal("-147.579"),
      BigDecimal("147.579"),
      "EHV1",
      1
    )
  )

  "The power plant object" should {
    "throw an exception, when the basic single model creation method is called" in {
      val thrown =
        intercept[SimbenchDataModelException](PowerPlant.apply(rawData(0)))
      thrown.getMessage shouldBe "No basic implementation of model creation available for PowerPlant$"
    }

    "throw an exception, when the basic batch model creation method is called" in {
      val thrown =
        intercept[SimbenchDataModelException](PowerPlant.buildModels(rawData))
      thrown.getMessage shouldBe "No basic implementation of model creation available for PowerPlant$"
    }

    "build the correct single model" in {
      val actual = PowerPlant.buildModel(
        rawData(0),
        nodes.getOrElse(
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
        PowerPlant.buildModels(rawData, nodes)
      actual shouldBe expected
    }
  }
}
