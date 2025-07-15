package edu.ie3.simbench.convert

import java.util.UUID

import edu.ie3.datamodel.models.OperationTime
import edu.ie3.datamodel.models.input.connector.Transformer2WInput
import edu.ie3.datamodel.models.input.connector.`type`.Transformer2WTypeInput
import edu.ie3.datamodel.models.input.{NodeInput, OperatorInput}
import edu.ie3.simbench.exception.ConversionException
import edu.ie3.simbench.model.datamodel.types.Transformer2WType
import edu.ie3.simbench.model.datamodel.{Node, Transformer2W}

import scala.collection.parallel.CollectionConverters.*

case object Transformer2wConverter {

  /** Converts a [[Vector]] of [[Transformer2W]] into ie3's
    * [[Transformer2WInput]] models
    *
    * @param inputs
    *   Vector of input models
    * @param types
    *   Mapping from input model to transformer type
    * @param nodes
    *   Mapping from input node model to converted node model
    * @return
    *   A Vector of ie3's [[Transformer2WInput]]
    */
  def convert(
      inputs: Vector[Transformer2W],
      types: Map[Transformer2WType, Transformer2WTypeInput],
      nodes: Map[Node, NodeInput]
  ): Vector[Transformer2WInput] =
    inputs.par.map { input =>
      val (nodeA, nodeB) =
        NodeConverter.getNodes(input.nodeHV, input.nodeLV, nodes)
      val transformerType = types.getOrElse(
        input.transformerType,
        throw ConversionException(
          s"Cannot find conversion result for transformer type ${input.transformerType.id}"
        )
      )

      convert(input, transformerType, nodeA, nodeB)
    }.seq

  /** Converts a SimBench [[Transformer2W]] into a [[Transformer2WInput]].
    *
    * @param input
    *   Input model
    * @param transformerType
    *   Type of the transformer
    * @param nodeA
    *   Node at the high voltage port
    * @param nodeB
    *   Node at the low voltage port
    * @param uuid
    *   UUID to use for the model generation (default: Random UUID)
    * @return
    *   A [[Transformer2WInput]] model
    */
  def convert(
      input: Transformer2W,
      transformerType: Transformer2WTypeInput,
      nodeA: NodeInput,
      nodeB: NodeInput,
      uuid: UUID = UUID.randomUUID()
  ): Transformer2WInput = {
    val id = input.id
    val tapPos = input.tappos
    val autoTap = input.autoTap

    new Transformer2WInput(
      uuid,
      id,
      OperatorInput.NO_OPERATOR_ASSIGNED,
      OperationTime.notLimited(),
      nodeA,
      nodeB,
      1,
      transformerType,
      tapPos,
      autoTap
    )
  }
}
