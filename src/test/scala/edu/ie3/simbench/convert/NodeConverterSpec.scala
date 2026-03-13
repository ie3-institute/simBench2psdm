package edu.ie3.simbench.convert

import java.util.UUID

import edu.ie3.simbench.model.datamodel.ExternalNet.{Simple, WardExtended}
import edu.ie3.simbench.model.datamodel.Node.NodeKey
import edu.ie3.simbench.model.datamodel.enums.{
  CalculationType,
  PowerPlantType,
  ResType
}
import edu.ie3.simbench.model.datamodel.profiles.{
  PowerPlantProfileType,
  ResProfileType
}
import edu.ie3.simbench.model.datamodel.{ExternalNet, PowerPlant, RES}
import edu.ie3.test.common.{ConverterTestData, UnitSpec}

class NodeConverterSpec extends UnitSpec with ConverterTestData {
  val uuid: UUID = UUID.randomUUID()

  val subnetConverter: SubnetConverter = SubnetConverter(
    Vector((BigDecimal("10.0"), "MV0"), (BigDecimal("10.0"), "MV1"))
  )

  val (slackNode, slackNodeExpected) = getNodePair("slack_node_0")
  val (otherNode, otherNodeExpected) = getNodePair("node_0")

  val slackNodeKeys = Vector(NodeKey("slack_node_0", "subnet_1", 5))

  val externalNets = Vector(
    Simple(
      "MV1.101 grid at LV1.101",
      getNodePair("MV1.101 Bus 4")._1,
      CalculationType.VaVm,
      BigDecimal("1"),
      None,
      None,
      "LV1.101_MV1.101_eq",
      5
    ),
    WardExtended(
      "MV1.101 grid at LV1.101",
      getNodePair("MV1.101 Bus 5")._1,
      BigDecimal("1"),
      None,
      None,
      BigDecimal("1.0"),
      BigDecimal("2.0"),
      BigDecimal("3.0"),
      "LV1.101_MV1.101_eq",
      5
    )
  )

  val powerPlantsVec = Vector(
    PowerPlant(
      "EHV Gen 75",
      getNodePair("MV1.101 Bus 4")._1,
      PowerPlantType("hard coal"),
      PowerPlantProfileType("pp_75"),
      CalculationType.VaVm,
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
      getNodePair("MV1.101 Bus 4")._1,
      PowerPlantType("hard coal"),
      PowerPlantProfileType("pp_76"),
      CalculationType.PVm,
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

  val resVec = Vector(
    RES(
      "LV1.101 SGen 3",
      getNodePair("LV1.101 Bus 4")._1,
      ResType.PV,
      ResProfileType.PV5,
      CalculationType.PQ,
      BigDecimal("0.023"),
      BigDecimal("0"),
      BigDecimal("0.023"),
      "LV1.101",
      7
    ),
    RES(
      "LV1.101 SGen 4",
      getNodePair("LV1.101 Bus 4")._1,
      ResType.WindMv,
      ResProfileType.WP4,
      CalculationType.VaVm,
      BigDecimal("0.023"),
      BigDecimal("0"),
      BigDecimal("0.023"),
      "LV1.101",
      7
    )
  )

  "The node converter" should {
    "build a slack node with all possible information given correctly" in {
      val actual =
        NodeConverter.convert(
          slackNode,
          slackNodeKeys,
          subnetConverter,
          uuid
        )

      actual.getId shouldBe slackNodeExpected.getId
      actual.getOperationTime shouldBe slackNodeExpected.getOperationTime
      actual.getOperator shouldBe slackNodeExpected.getOperator
      actual.getGeoPosition shouldBe slackNodeExpected.getGeoPosition
      actual.getvTarget shouldBe slackNodeExpected.getvTarget()
      actual.getSubnet shouldBe slackNodeExpected.getSubnet
      actual.getVoltLvl shouldBe slackNodeExpected.getVoltLvl
    }

    "build a normal node with least possible information given correctly" in {
      val actual =
        NodeConverter.convert(
          otherNode,
          slackNodeKeys,
          subnetConverter,
          uuid
        )
      actual.getId shouldBe otherNodeExpected.getId
      actual.getOperationTime shouldBe otherNodeExpected.getOperationTime
      actual.getOperator shouldBe otherNodeExpected.getOperator
      actual.getGeoPosition shouldBe otherNodeExpected.getGeoPosition
      actual.getvTarget shouldBe otherNodeExpected.getvTarget()
      actual.getSubnet shouldBe otherNodeExpected.getSubnet
      actual.getVoltLvl shouldBe otherNodeExpected.getVoltLvl
    }

    val extractSlackNodeKeysMethod =
      PrivateMethod[Vector[NodeKey]](Symbol("extractSlackNodeKeys"))

    "extract the correct Vector of NodeKeys from each single set of models" in {
      val getCalcTypeFromExternalNet =
        (model: ExternalNet) => model.calculationType
      val getNodeFromExternalNet = (model: ExternalNet) => model.node
      NodeConverter invokePrivate extractSlackNodeKeysMethod(
        externalNets,
        getCalcTypeFromExternalNet,
        getNodeFromExternalNet
      ) shouldBe Vector(
        NodeKey("MV1.101 Bus 4", "MV1.101_LV1.101_Feeder1", 5),
        NodeKey("MV1.101 Bus 5", "MV1.101_LV1.101_Feeder1", 5)
      )

      val getCalcTypeFromPowerPlant =
        (model: PowerPlant) => model.calculationType
      val getNodeFromPowerPlant = (model: PowerPlant) => model.node
      NodeConverter invokePrivate extractSlackNodeKeysMethod(
        powerPlantsVec,
        getCalcTypeFromPowerPlant,
        getNodeFromPowerPlant
      ) shouldBe Vector(NodeKey("MV1.101 Bus 4", "MV1.101_LV1.101_Feeder1", 5))

      val getCalcTypeFromRes = (model: RES) => model.calculationType
      val getNodeFromRes = (model: RES) => model.node
      NodeConverter invokePrivate extractSlackNodeKeysMethod(
        resVec,
        getCalcTypeFromRes,
        getNodeFromRes
      ) shouldBe Vector(NodeKey("LV1.101 Bus 4", "LV1.101", 7))
    }

    "extract the total set of slack node keys correctly" in {
      NodeConverter.getSlackNodeKeys(
        externalNets,
        powerPlantsVec,
        resVec
      ) shouldBe Vector(
        NodeKey("MV1.101 Bus 4", "MV1.101_LV1.101_Feeder1", 5),
        NodeKey("MV1.101 Bus 5", "MV1.101_LV1.101_Feeder1", 5),
        NodeKey("LV1.101 Bus 4", "LV1.101", 7)
      )
    }
  }
}
