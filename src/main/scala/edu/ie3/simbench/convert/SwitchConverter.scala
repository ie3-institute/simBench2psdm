package edu.ie3.simbench.convert

import java.util.UUID

import edu.ie3.datamodel.models.OperationTime
import edu.ie3.datamodel.models.input.connector.SwitchInput
import edu.ie3.datamodel.models.input.{NodeInput, OperatorInput}
import edu.ie3.simbench.model.datamodel.{Node, Switch}

import scala.collection.parallel.CollectionConverters._

case object SwitchConverter {

  /** Converts a set of [[Switch]]es to [[SwitchInput]]
    *
    * @param switches
    *   [[Vector]] of SimBench [[Switch]]es
    * @param nodes
    *   Mapping from SimBench [[Node]] to ie3's [[NodeInput]]
    * @return
    *   [[Vector]] of [[SwitchInput]]
    */
  def convert(
      switches: Vector[Switch],
      nodes: Map[Node, NodeInput]
  ): Vector[SwitchInput] =
    switches.par.map { input =>
      val (nodeA, nodeB) =
        NodeConverter.getNodes(input.nodeA, input.nodeB, nodes)
      convert(input, nodeA, nodeB)
    }.seq

  /** Converts a [[Switch]] into ie3's [[SwitchInput]]
    *
    * @param input
    *   Model to convert
    * @param nodeA
    *   One of the nodes, the switch connects
    * @param nodeB
    *   The other of the nodes, the switch connects
    * @param uuid
    *   UUID to use for the model generation (default: Random UUID)
    * @return
    *   A [[SwitchInput]] model
    */
  def convert(
      input: Switch,
      nodeA: NodeInput,
      nodeB: NodeInput,
      uuid: UUID = UUID.randomUUID()
  ): SwitchInput = {
    val id = input.id
    val isClosed = input.cond
    new SwitchInput(
      uuid,
      id,
      OperatorInput.NO_OPERATOR_ASSIGNED,
      OperationTime.notLimited(),
      nodeA,
      nodeB,
      isClosed
    )
  }
}
