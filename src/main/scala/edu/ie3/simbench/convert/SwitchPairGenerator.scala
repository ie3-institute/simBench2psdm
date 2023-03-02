package edu.ie3.simbench.convert

import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.connector.{
  LineInput,
  SwitchInput,
  Transformer2WInput
}
import org.jgrapht.graph.DirectedMultigraph

import java.io.{File, PrintWriter}
import java.nio.file.Paths
import java.util.UUID
import scala.jdk.CollectionConverters.IterableHasAsScala

object SwitchPairGenerator {

  final case class SwitchPairs(
      loopSwitch: SwitchInput,
      lineSwitches: Map[LineInput, (NodeInput, SwitchInput)]
  )

  object SwitchPairs {

    def writeSwitchPairMappings(
        switchPairs: Seq[SwitchPairs],
        filePath: String
    ): Unit = {
      val uuidMapping = switchPairs
        .map(
          sp =>
            (
              sp.loopSwitch.getUuid.toString,
              sp.lineSwitches.map(_._2._2.getUuid.toString)
            )
        )
        .toMap
      val idMapping = switchPairs
        .map(sp => (sp.loopSwitch.getId, sp.lineSwitches.map(_._2._2.getId)))
        .toMap
      val uuidFile = Paths.get(filePath, "switch_pair_mapping_uuid.csv").toFile
      val idFile = Paths.get(filePath, "switch_pair_mapping_id.csv").toFile
      Seq((uuidMapping, uuidFile), (idMapping, idFile)).foreach {
        case (mapping, file) =>
          writeMapToCSV(
            mapping,
            file
          )
      }
    }

    def writeMapToCSV(map: Map[String, Iterable[String]], file: File): Unit = {
      val pw = new PrintWriter(file)
      map.foreach { case (k, v) => pw.write(k + "," + v.mkString(" ") + "\n") }
      pw.close()
    }
  }

  // todo: no extra switches at nodes that already contain a switch
  def generate(
      nodes: Set[NodeInput],
      lines: Set[LineInput],
      transformers: Set[Transformer2WInput],
      switches: Set[SwitchInput]
  ): Seq[SwitchPairs] = {

    // build graph of grid
    val graph =
      new DirectedMultigraph[NodeInput, LineInput](classOf[LineInput])
    nodes.foreach(graph.addVertex)
    lines.foreach(
      line => graph.addEdge(line.getNodeA, line.getNodeB, line)
    )

    val transformerNodes =
      transformers.flatMap(t => Seq(t.getNodeA, t.getNodeB))

    switches
      .foldLeft((Seq.empty[SwitchPairs], Set.empty[NodeInput]))(
        (currentState, switch) => {
          val (switchPairs, alreadyVisited) = currentState

          // get both nodes and respective connected edges
          val switchNodeAndEdges = getConnectedLinesOfSwitch(
            switch.getNodeA,
            graph
          ).map(l => (switch.getNodeA, l)) ++ getConnectedLinesOfSwitch(
            switch.getNodeB,
            graph
          ).map(l => (switch.getNodeB, l))

          // todo -> fold to update already visited
          val (visited, lineSwitches) = switchNodeAndEdges.foldLeft(
            (alreadyVisited, Map.empty[LineInput, (NodeInput, SwitchInput)])
          )((current, nodeAndEdge) => {
            val (node, edge) = nodeAndEdge
            val (currentVisited, currentLineSwitches) = current
            traverseRing(
              graph,
              node,
              edge,
              currentVisited,
              transformerNodes,
              currentLineSwitches
            )
          })

          val switchPair = SwitchPairs(
            switch,
            lineSwitches
          )
          (switchPairs :+ switchPair, alreadyVisited ++ visited)
        }
      )
      ._1
  }

  def getConnectedLinesOfSwitch(
      node: NodeInput,
      graph: DirectedMultigraph[NodeInput, LineInput]
  ): Seq[LineInput] = {
    graph.incomingEdgesOf(node).asScala.toSeq ++ graph
      .outgoingEdgesOf(node)
      .asScala
      .toSeq
  }

  def traverseRing(
      graph: DirectedMultigraph[NodeInput, LineInput],
      currentNode: NodeInput,
      currentEdge: LineInput,
      alreadyVisited: Set[NodeInput],
      transformerNodes: Set[NodeInput],
      lineSwitches: Map[LineInput, (NodeInput, SwitchInput)]
  ): (Set[NodeInput], Map[LineInput, (NodeInput, SwitchInput)]) = {

    if (transformerNodes.contains(currentNode) | alreadyVisited.contains(
          currentNode
        ))
      return (alreadyVisited + currentNode, lineSwitches)

    val nextNode =
      if (currentEdge.getNodeA == currentNode) currentEdge.getNodeB
      else currentEdge.getNodeA

    val updatedLineSwitches = if (!currentEdge.getId.contains("loop")) {
      // build auxiliary node and switch in switch direction
      // we use the next node for building the switch so we don't have two switches at loop line switch node
      val switchNode = buildAuxSwitchNode(
        nextNode
      )
      val switch = buildSwitch(
        nextNode,
        switchNode
      )
      lineSwitches ++ Map(
        currentEdge -> (switchNode, switch)
      )
    }
    // skip building a switch if we are on the loop line, as this already has a connected switch
    else {
      lineSwitches
    }

    val edges = graph.incomingEdgesOf(nextNode).asScala.toSet ++ graph
      .outgoingEdgesOf(nextNode)
      .asScala
      .toSet

    //  continue in all directions except the one we came from
    edges.foldLeft((alreadyVisited, updatedLineSwitches))((last, nextEdge) => {
      // ignore the direction we came from
      if (nextEdge == currentEdge) last
      else {
        val (currentAlreadyVisited, currentLineSwitches) = last
        val visited = currentAlreadyVisited + currentNode
        traverseRing(
          graph,
          nextNode,
          nextEdge,
          visited + currentNode,
          transformerNodes,
          currentLineSwitches
        )
      }
    })
  }

  def buildAuxSwitchNode(
      nodeAtSwitchLoc: NodeInput
  ): NodeInput = {
    new NodeInput(
      UUID.randomUUID(),
      nodeAtSwitchLoc.getId + "-switch_node",
      nodeAtSwitchLoc.getvTarget(),
      false,
      // todo geo position between node and other end of line
      nodeAtSwitchLoc.getGeoPosition,
      nodeAtSwitchLoc.getVoltLvl,
      nodeAtSwitchLoc.getSubnet
    )
  }

  def buildSwitch(
      node: NodeInput,
      auxSwitchNode: NodeInput
  ): SwitchInput = {
    new SwitchInput(
      UUID.randomUUID(),
      node.getId + "-switch",
      node,
      auxSwitchNode,
      true
    )
  }

}
