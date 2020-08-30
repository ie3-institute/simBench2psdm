/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.simbench.model

import edu.ie3.simbench.exception.io.SimbenchDataModelException
import edu.ie3.simbench.model.datamodel.{Load, NodePFResult, Substation}
import edu.ie3.simbench.model.datamodel.profiles.LoadProfileType
import edu.ie3.test.common.{ConverterTestData, UnitSpec}

class NodePFResultSpec extends UnitSpec with ConverterTestData {
  val nodeMapping = Map(
    "LV1.101 Bus 1" -> getNodePair("LV1.101 Bus 1")._1
  )

  val substations = Map(
    "substation_1" -> Substation("substation_1", "LV1.101", 7)
  )

  val rawData = Vector(
    RawModelData(
      classOf[NodePFResult],
      Map(
        "node" -> "LV1.101 Bus 1",
        "vm" -> "1.04913",
        "va" -> "-144.292",
        "substation" -> "substation_1",
        "subnet" -> "LV1.101",
        "voltLvl" -> "7"
      )
    ),
    RawModelData(
      classOf[NodePFResult],
      Map(
        "node" -> "LV1.101 Bus 1",
        "vm" -> "1.04729",
        "va" -> "-144.168",
        "substation" -> "NULL",
        "subnet" -> "LV1.101",
        "voltLvl" -> "7"
      )
    )
  )

  val expected = Vector(
    NodePFResult(
      getNodePair("LV1.101 Bus 1")._1,
      1.04913,
      -144.292,
      Some(Substation("substation_1", "LV1.101", 7)),
      "LV1.101",
      7
    ),
    NodePFResult(
      getNodePair("LV1.101 Bus 1")._1,
      1.04729,
      -144.168,
      None,
      "LV1.101",
      7
    )
  )

  "The node result object" should {
    "throw an exception, when the basic single model creation method is called" in {
      val thrown =
        intercept[SimbenchDataModelException](NodePFResult.apply(rawData(0)))
      thrown.getMessage shouldBe "No basic implementation of model creation available for NodePFResult$"
    }

    "throw an exception, when the basic batch model creation method is called" in {
      val thrown =
        intercept[SimbenchDataModelException](NodePFResult.buildModels(rawData))
      thrown.getMessage shouldBe "No basic implementation of model creation available for NodePFResult$"
    }

    "build the correct single model" in {
      val actual = NodePFResult.buildModel(
        rawData(0),
        nodeMapping.getOrElse(
          "LV1.101 Bus 1",
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
        NodePFResult.buildModels(rawData, nodeMapping, substations)
      actual shouldBe expected
    }
  }
}
