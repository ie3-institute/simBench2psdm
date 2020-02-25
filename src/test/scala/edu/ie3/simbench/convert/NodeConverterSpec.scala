package edu.ie3.simbench.convert

import java.util.UUID

import com.vividsolutions.jts.geom.{
  GeometryFactory,
  Point,
  Coordinate => JTSCoordinate
}
import edu.ie3.models.OperationTime
import edu.ie3.models.input.{NodeInput, OperatorInput}
import edu.ie3.simbench.model.datamodel.{Coordinate, Node}
import edu.ie3.simbench.model.datamodel.enums.NodeType
import edu.ie3.util.quantities.PowerSystemUnits.{KILOVOLT, PU}
import edu.ie3.models.GermanVoltageLevel.MV
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

  val slackNodeKeys = Vector(("slack_node_0", "subnet_1", 5))

  "The node converter" should {
    "build a slack node with all possible information given correctly" in {
      val actual = NodeConverter.convert(slackNode,
                                         slackNodeKeys,
                                         subnetConverter,
                                         Some(uuid))
      actual shouldBe slackNodeExpected
    }

    "build a normal node with least possible information given correctly" in {
      val actual = NodeConverter.convert(otherNode,
                                         slackNodeKeys,
                                         subnetConverter,
                                         Some(uuid))
      actual shouldBe otherNodeExpected
    }
  }
}
