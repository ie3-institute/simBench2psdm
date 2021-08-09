package edu.ie3.test.common

import edu.ie3.simbench.model.datamodel.{Node, Switch}
import edu.ie3.simbench.model.datamodel.enums.{NodeType, SwitchType}

trait SwitchTestingData {
  val nodeA: Node = Node(
    "nodeA",
    NodeType.BusBar,
    vmR = BigDecimal("0.4"),
    vmMin = BigDecimal("0.9"),
    vmMax = BigDecimal("1.1"),
    subnet = "a",
    voltLvl = 7
  )
  val nodeB: Node = Node(
    "nodeB",
    NodeType.BusBar,
    vmR = BigDecimal("0.4"),
    vmMin = BigDecimal("0.9"),
    vmMax = BigDecimal("1.1"),
    subnet = "a",
    voltLvl = 7
  )
  val nodeC: Node = Node(
    "nodeC",
    NodeType.BusBar,
    vmR = BigDecimal("0.4"),
    vmMin = BigDecimal("0.9"),
    vmMax = BigDecimal("1.1"),
    subnet = "a",
    voltLvl = 7
  )
  val nodeD: Node = Node(
    "nodeD",
    NodeType.BusBar,
    vmR = BigDecimal("0.4"),
    vmMin = BigDecimal("0.9"),
    vmMax = BigDecimal("1.1"),
    subnet = "a",
    voltLvl = 7
  )
  val nodeE: Node = Node(
    "nodeE",
    NodeType.BusBar,
    vmR = BigDecimal("0.4"),
    vmMin = BigDecimal("0.9"),
    vmMax = BigDecimal("1.1"),
    subnet = "a",
    voltLvl = 7
  )
  val nodeF: Node = Node(
    "nodeF",
    NodeType.BusBar,
    vmR = BigDecimal("0.4"),
    vmMin = BigDecimal("0.9"),
    vmMax = BigDecimal("1.1"),
    subnet = "a",
    voltLvl = 7
  )
  val nodeG: Node = Node(
    "nodeG",
    NodeType.BusBar,
    vmR = BigDecimal("0.4"),
    vmMin = BigDecimal("0.9"),
    vmMax = BigDecimal("1.1"),
    subnet = "a",
    voltLvl = 7
  )
  val nodeH: Node = Node(
    "nodeH",
    NodeType.BusBar,
    vmR = BigDecimal("0.4"),
    vmMin = BigDecimal("0.9"),
    vmMax = BigDecimal("1.1"),
    subnet = "a",
    voltLvl = 7
  )
  val nodeI: Node = Node(
    "nodeI",
    NodeType.BusBar,
    vmR = BigDecimal("0.4"),
    vmMin = BigDecimal("0.9"),
    vmMax = BigDecimal("1.1"),
    subnet = "a",
    voltLvl = 7
  )

  /* First switch chain between nodes A, B and C */
  val switchAB: Switch = Switch(
    "switchAB",
    nodeA,
    nodeB,
    SwitchType.LoadSwitch,
    cond = true,
    subnet = "a",
    voltLvl = 7,
    substation = None
  )
  val switchBC: Switch = Switch(
    "switchBC",
    nodeB,
    nodeC,
    SwitchType.LoadSwitch,
    cond = true,
    subnet = "a",
    voltLvl = 7,
    substation = None
  )
  val switchGroup0 = Vector(switchAB, switchBC)

  /* Second switch chain between nodes D, E, F und G */
  val switchDE: Switch = Switch(
    "switchDE",
    nodeD,
    nodeE,
    SwitchType.LoadSwitch,
    cond = true,
    subnet = "a",
    voltLvl = 7,
    substation = None
  )
  val switchDF: Switch = Switch(
    "switchDF",
    nodeD,
    nodeF,
    SwitchType.LoadSwitch,
    cond = true,
    subnet = "a",
    voltLvl = 7,
    substation = None
  )
  val switchDG: Switch = Switch(
    "switchDG",
    nodeD,
    nodeG,
    SwitchType.LoadSwitch,
    cond = true,
    subnet = "a",
    voltLvl = 7,
    substation = None
  )
  val switchGroup1 = Vector(switchDE, switchDF, switchDG)

  /* Third switch "chain" between node H and I */
  val switchHI: Switch = Switch(
    "switchHI",
    nodeH,
    nodeI,
    SwitchType.LoadSwitch,
    cond = true,
    subnet = "a",
    voltLvl = 7,
    substation = None
  )
  val switchGroup2 = Vector(switchHI)

  val switches =
    Vector(switchAB, switchBC, switchDE, switchDF, switchDG, switchHI)
}
