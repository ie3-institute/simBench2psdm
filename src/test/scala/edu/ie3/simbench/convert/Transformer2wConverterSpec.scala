package edu.ie3.simbench.convert

import java.util.UUID

import com.vividsolutions.jts.geom.{
  GeometryFactory,
  Point,
  Coordinate => JTSCoordinate
}
import edu.ie3.models.OperationTime
import edu.ie3.models.GermanVoltageLevel.{LV, MV}
import edu.ie3.models.input.{NodeInput, OperatorInput}
import edu.ie3.models.input.connector.`type`.Transformer2WTypeInput
import edu.ie3.simbench.model.datamodel.{
  Coordinate,
  Node,
  Substation,
  Transformer2W
}
import edu.ie3.simbench.model.datamodel.enums.BranchElementPort.HV
import edu.ie3.simbench.model.datamodel.enums.NodeType.BusBar
import edu.ie3.simbench.model.datamodel.types.Transformer2WType
import edu.ie3.test.common.UnitSpec
import tec.uom.se.quantity.Quantities
import tec.uom.se.unit.MetricPrefix
import tec.uom.se.unit.Units.{OHM, PERCENT, SIEMENS}
import edu.ie3.util.quantities.PowerSystemUnits.{
  DEGREE_GEOM,
  KILOVOLT,
  KILOVOLTAMPERE,
  PU
}

class Transformer2wConverterSpec extends UnitSpec {
  val inputType: Transformer2WType = Transformer2WType(
    "test type",
    BigDecimal("40"),
    BigDecimal("110"),
    BigDecimal("10"),
    BigDecimal("150"),
    BigDecimal("5"),
    BigDecimal("6"),
    BigDecimal("10"),
    BigDecimal("1"),
    tapable = true,
    HV,
    BigDecimal("0.025"),
    BigDecimal("5"),
    0,
    -10,
    10
  )

  val typeUuid: UUID = UUID.randomUUID()
  val resultType: Transformer2WTypeInput = new Transformer2WTypeInput(
    typeUuid,
    "test type",
    Quantities.getQuantity(45.375, MetricPrefix.MILLI(OHM)),
    Quantities.getQuantity(15.1249319, OHM),
    Quantities.getQuantity(40000d, KILOVOLTAMPERE),
    Quantities.getQuantity(110d, KILOVOLT),
    Quantities.getQuantity(10d, KILOVOLT),
    Quantities.getQuantity(2480.5790, MetricPrefix.NANO(SIEMENS)),
    Quantities
      .getQuantity(32972.94113, MetricPrefix.NANO(SIEMENS))
      .to(MetricPrefix.NANO(SIEMENS)),
    Quantities.getQuantity(2.5, PERCENT),
    Quantities.getQuantity(5d, DEGREE_GEOM),
    false,
    0,
    10,
    -10
  )

  val types: Map[Transformer2WType, Transformer2WTypeInput] = Map(
    inputType -> resultType)

  val nodeAUuid: UUID = UUID.randomUUID()
  val nodeBUuid: UUID = UUID.randomUUID()
  val geometryFactory: GeometryFactory = new GeometryFactory()
  val randomCoordinate: Point =
    geometryFactory.createPoint(new JTSCoordinate(7.412262, 51.492689))
  val nodeAInput: Node = Node(
    "MV1.101 Bus 4",
    BusBar,
    Some(BigDecimal("1.025")),
    Some(BigDecimal("0.0")),
    BigDecimal("20"),
    BigDecimal("0.965"),
    BigDecimal("1.055"),
    None,
    None,
    "MV1.101_LV1.101_Feeder1",
    5
  )
  val nodeA = new NodeInput(
    nodeAUuid,
    OperationTime.notLimited(),
    OperatorInput.NO_OPERATOR_ASSIGNED,
    "MV1.101 Bus 4",
    Quantities.getQuantity(1.025, PU),
    Quantities.getQuantity(20d, KILOVOLT),
    true,
    randomCoordinate,
    MV,
    1
  )

  val nodeBInput: Node = Node(
    "LV1.101 Bus 4",
    BusBar,
    None,
    None,
    BigDecimal("0.4"),
    BigDecimal("0.9"),
    BigDecimal("1.1"),
    None,
    None,
    "LV1.101",
    7
  )
  val nodeB: NodeInput = new NodeInput(
    nodeBUuid,
    OperationTime.notLimited(),
    OperatorInput.NO_OPERATOR_ASSIGNED,
    "LV1.101 Bus 4",
    Quantities.getQuantity(1.0, PU),
    Quantities.getQuantity(0.4, KILOVOLT),
    false,
    randomCoordinate,
    LV,
    2
  )

  val nodes: Map[Node, NodeInput] = Map(
    nodeAInput -> nodeA,
    nodeBInput -> nodeB
  )

  val input: Transformer2W = Transformer2W(
    "MV1.101-LV1.101-Trafo 1",
    Node(
      "MV1.101 Bus 4",
      BusBar,
      Some(BigDecimal("1.025")),
      Some(BigDecimal("0.0")),
      BigDecimal("20"),
      BigDecimal("0.965"),
      BigDecimal("1.055"),
      None,
      None,
      "MV1.101_LV1.101_Feeder1",
      5
    ),
    Node(
      "LV1.101 Bus 4",
      BusBar,
      None,
      None,
      BigDecimal("0.4"),
      BigDecimal("0.9"),
      BigDecimal("1.1"),
      None,
      None,
      "LV1.101",
      7
    ),
    Transformer2WType(
      "test type",
      BigDecimal("40"),
      BigDecimal("110"),
      BigDecimal("10"),
      BigDecimal("150"),
      BigDecimal("5"),
      BigDecimal("6"),
      BigDecimal("10"),
      BigDecimal("1"),
      tapable = true,
      HV,
      BigDecimal("0.025"),
      BigDecimal("5"),
      0,
      -10,
      10
    ),
    10,
    autoTap = true,
    Some(HV),
    BigDecimal("100"),
    None,
    "LV1.101",
    6
  )

  "The two winding transformer converter" should {
    "convert a single input correctly" in {
      val actual =
        Transformer2wConverter.convert(input, resultType, nodeA, nodeB)

      actual.getId shouldBe "MV1.101-LV1.101-Trafo 1"
      actual.getNodeA shouldBe nodeA
      actual.getNodeB shouldBe nodeB
      actual.getType shouldBe resultType
      actual.getNoOfParallelDevices shouldBe 1
      actual.getOperationTime shouldBe OperationTime.notLimited()
      actual.getOperator shouldBe OperatorInput.NO_OPERATOR_ASSIGNED
      actual.getTapPos shouldBe 10
    }
  }

  "convert a set of input models correctly" in {
    val actual = Transformer2wConverter.convert(Vector(input), types, nodes)

    actual.length shouldBe 1

    actual(0).getId shouldBe "MV1.101-LV1.101-Trafo 1"
    actual(0).getNodeA shouldBe nodeA
    actual(0).getNodeB shouldBe nodeB
    actual(0).getType shouldBe resultType
    actual(0).getNoOfParallelDevices shouldBe 1
    actual(0).getOperationTime shouldBe OperationTime.notLimited()
    actual(0).getOperator shouldBe OperatorInput.NO_OPERATOR_ASSIGNED
    actual(0).getTapPos shouldBe 10
  }
}
