package edu.ie3.simbench.convert

import edu.ie3.datamodel.models.input.container.JointGridContainer
import edu.ie3.simbench.config.SimbenchConfig
import edu.ie3.simbench.io.SimbenchModelRetriever
import edu.ie3.simbench.model.datamodel.Node.NodeKey
import edu.ie3.simbench.model.datamodel.enums.NodeType

object TopologyChangeConverter {

  def convert(
      simbenchCode: String,
      simbenchConfig: SimbenchConfig,
      slackNodeKeys: Vector[NodeKey],
      subnetConverter: SubnetConverter
  ): Unit = {

    val withSwitches = simbenchCode.replace("no_sw", "sw")

    val gridWithSwitches = SimbenchModelRetriever.retrieve(
      withSwitches,
      simbenchConfig
    )

    // get switches that are used for loop operation
    val openLoopSwitches =
      gridWithSwitches.switches.filter(_.id.contains("loop_line_switch"))
    val switchNodes =
      openLoopSwitches.flatMap(switch => Seq(switch.nodeA, switch.nodeB))
    val auxiliaryNodes = switchNodes.filter(_.nodeType == NodeType.Auxiliary)
    require(
      auxiliaryNodes.size == openLoopSwitches.size,
      "Expected one auxiliary node for every switch."
    )
    val loopLines = gridWithSwitches.lines.filter(
      line =>
        (auxiliaryNodes.contains(line.nodeA) | auxiliaryNodes
          .contains(line.nodeB))
    )
    require(
      loopLines.size == openLoopSwitches.size,
      "Expected one loop line for every loop switch."
    )

    val loopNodes = auxiliaryNodes.map(
      node =>
        NodeConverter.convert(
          node,
          slackNodeKeys,
          subnetConverter,
          maybeExplicitSubnet = None
        )
    )

    // add switches on the loop that can be used for simulating faults
    val faultSwitches = ???

  }

}
