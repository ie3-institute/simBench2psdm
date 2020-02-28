package edu.ie3.simbench.convert

import java.util.UUID

import com.vividsolutions.jts.geom.{
  GeometryFactory,
  Point,
  Coordinate => JTSCoordinate
}
import edu.ie3.models.OperationTime
import edu.ie3.models.input.{NodeInput, OperatorInput}
import edu.ie3.simbench.model.datamodel.{
  Coordinate,
  ExternalNet,
  Node,
  PowerPlant,
  RES,
  Substation
}
import edu.ie3.simbench.model.datamodel.enums.{
  CalculationType,
  NodeType,
  PowerPlantType,
  ResType
}
import edu.ie3.util.quantities.PowerSystemUnits.{KILOVOLT, PU}
import edu.ie3.models.GermanVoltageLevel.MV
import edu.ie3.simbench.model.datamodel.ExternalNet.{Simple, Ward, WardExtended}
import edu.ie3.simbench.model.datamodel.Node.NodeKey
import edu.ie3.simbench.model.datamodel.profiles.{
  PowerPlantProfileType,
  ResProfileType
}
import edu.ie3.test.common.UnitSpec
import tec.uom.se.quantity.Quantities

class NodeConverterSpec extends UnitSpec {
  val geometryFactory = new GeometryFactory()

  val uuid: UUID = UUID.randomUUID()

  val subnetConverter: SubnetConverter = SubnetConverter(
    Vector("subnet_1", "subnet_2"))
  val expectedCoordinate: Point =
    geometryFactory.createPoint(new JTSCoordinate(7.412262, 51.492689))

  val slackNode: Node = Node(
    "slack_node_0",
    NodeType.BusBar,
    Some(BigDecimal("1.3")),
    None,
    BigDecimal("10.0"),
    BigDecimal("0.95"),
    BigDecimal("1.05"),
    None,
    Some(
      Coordinate("random coordinate",
                 BigDecimal("7.412262"),
                 BigDecimal("51.492689"),
                 "subnet_1",
                 7)),
    "subnet_1",
    5
  )
  val slackNodeExpected = new NodeInput(
    uuid,
    OperationTime.notLimited(),
    OperatorInput.NO_OPERATOR_ASSIGNED,
    "slack_node_0",
    Quantities.getQuantity(1.3, PU),
    Quantities.getQuantity(10.0, KILOVOLT),
    true,
    expectedCoordinate,
    MV,
    1
  )
  val otherNode: Node = Node("node_0",
                             NodeType.Node,
                             None,
                             None,
                             BigDecimal("10.0"),
                             BigDecimal("0.95"),
                             BigDecimal("1.05"),
                             None,
                             None,
                             "subnet_2",
                             5)
  val otherNodeExpected = new NodeInput(
    uuid,
    OperationTime.notLimited(),
    OperatorInput.NO_OPERATOR_ASSIGNED,
    "node_0",
    Quantities.getQuantity(1.0, PU),
    Quantities.getQuantity(10.0, KILOVOLT),
    false,
    expectedCoordinate,
    MV,
    2
  )

  val slackNodeKeys = Vector(NodeKey("slack_node_0", "subnet_1", 5))

  val externalNets = Vector(
    Simple(
      "MV1.101 grid at LV1.101",
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
          )),
        "MV1.101_LV1.101_Feeder1",
        5
      ),
      CalculationType.VaVm,
      BigDecimal("1"),
      None,
      None,
      "LV1.101_MV1.101_eq",
      5
    ),
    WardExtended(
      "MV1.101 grid at LV1.101",
      Node(
        "MV1.101 Bus 5",
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
          )),
        "MV1.101_LV1.101_Feeder1",
        5
      ),
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

  val powerPlants = Vector(
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
          )),
        "MV1.101_LV1.101_Feeder1",
        5
      ),
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
          )),
        "MV1.101_LV1.101_Feeder1",
        5
      ),
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

  val res = Vector(
    RES(
      "LV1.101 SGen 3",
      Node(
        "LV1.101 Bus 4",
        NodeType.BusBar,
        None,
        None,
        BigDecimal("0.4"),
        BigDecimal("0.9"),
        BigDecimal("1.1"),
        None,
        Some(
          Coordinate(
            "coord_14",
            BigDecimal("11.4097"),
            BigDecimal("53.6413"),
            "LV1.101",
            5
          )),
        "LV1.101",
        7
      ),
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
      Node(
        "LV1.101 Bus 4",
        NodeType.BusBar,
        None,
        None,
        BigDecimal("0.4"),
        BigDecimal("0.9"),
        BigDecimal("1.1"),
        None,
        Some(
          Coordinate(
            "coord_14",
            BigDecimal("11.4097"),
            BigDecimal("53.6413"),
            "LV1.101",
            7
          )),
        "LV1.101",
        7
      ),
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
        NodeConverter.convert(slackNode, slackNodeKeys, subnetConverter, uuid)
      actual shouldBe slackNodeExpected
    }

    "build a normal node with least possible information given correctly" in {
      val actual =
        NodeConverter.convert(otherNode, slackNodeKeys, subnetConverter, uuid)
      actual shouldBe otherNodeExpected
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
        getNodeFromExternalNet) shouldBe Vector(
        NodeKey("MV1.101 Bus 4", "MV1.101_LV1.101_Feeder1", 5),
        NodeKey("MV1.101 Bus 5", "MV1.101_LV1.101_Feeder1", 5)
      )

      val getCalcTypeFromPowerPlant =
        (model: PowerPlant) => model.calculationType
      val getNodeFromPowerPlant = (model: PowerPlant) => model.node
      NodeConverter invokePrivate extractSlackNodeKeysMethod(
        powerPlants,
        getCalcTypeFromPowerPlant,
        getNodeFromPowerPlant) shouldBe Vector(
        NodeKey("MV1.101 Bus 4", "MV1.101_LV1.101_Feeder1", 5))

      val getCalcTypeFromRes = (model: RES) => model.calculationType
      val getNodeFromRes = (model: RES) => model.node
      NodeConverter invokePrivate extractSlackNodeKeysMethod(
        res,
        getCalcTypeFromRes,
        getNodeFromRes) shouldBe Vector(NodeKey("LV1.101 Bus 4", "LV1.101", 7))
    }

    "extract the total set of slack node keys correctly" in {
      NodeConverter.getSlackNodeKeys(externalNets, powerPlants, res) shouldBe Vector(
        NodeKey("MV1.101 Bus 4", "MV1.101_LV1.101_Feeder1", 5),
        NodeKey("MV1.101 Bus 5", "MV1.101_LV1.101_Feeder1", 5),
        NodeKey("LV1.101 Bus 4", "LV1.101", 7)
      )
    }
  }
}
