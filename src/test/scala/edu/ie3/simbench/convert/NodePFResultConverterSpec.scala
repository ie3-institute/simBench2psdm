package edu.ie3.simbench.convert

import java.time.ZonedDateTime
import java.util.UUID

import edu.ie3.simbench.model.datamodel.Node.NodeKey
import edu.ie3.test.common.{ConverterTestData, UnitSpec}

class NodePFResultConverterSpec extends UnitSpec with ConverterTestData {
  val uuid: UUID = UUID.randomUUID()

  val subnetConverter: SubnetConverter = SubnetConverter(
    Vector((BigDecimal("10.0"), "MV0"), (BigDecimal("10.0"), "MV1"))
  )

  val (slackNode, slackNodeExpected) = getNodePair("slack_node_0")
  val (otherNode, otherNodeExpected) = getNodePair("node_0")

  val slackNodeKeys = Vector(NodeKey("slack_node_0", "subnet_1", 5))

  "The node result converter" should {
    "convert a single result correctly without any further specifications" in {
      val (input, expected) = getNodeResultPair("0")
      val (_, node) = getNodePair(input.node.id)

      val actual = NodePFResultConverter.convert(input, node)

      actual.getTime shouldBe expected.getTime
      actual.getInputModel shouldBe expected.getInputModel
      actual.getvMag shouldBe actual.getvMag()
      actual.getvAng shouldBe expected.getvAng()
    }

    "convert a single result correctly" in {
      val (input, expected) = getNodeResultPair("1")
      val (_, node) = getNodePair(input.node.id)

      val actual = NodePFResultConverter.convert(input, node)

      actual.getTime shouldBe expected.getTime
      actual.getInputModel shouldBe expected.getInputModel
      actual.getvMag shouldBe actual.getvMag()
      actual.getvAng shouldBe expected.getvAng()
    }

    "convert a single result correctly with specified time stamp" in {
      val timeStamp = ZonedDateTime.parse("2020-08-24T09:24:00Z")
      val (input, expected) = getNodeResultPair("0")
      val (_, node) = getNodePair(input.node.id)

      val actual =
        NodePFResultConverter.convert(input, node, timeStamp = timeStamp)

      actual.getTime shouldBe timeStamp
      actual.getInputModel shouldBe expected.getInputModel
      actual.getvMag shouldBe actual.getvMag()
      actual.getvAng shouldBe expected.getvAng()
    }
  }
}
