package edu.ie3.simbench.convert

import java.util.UUID

import com.typesafe.scalalogging.LazyLogging
import edu.ie3.datamodel.models.OperationTime
import edu.ie3.datamodel.models.input.connector.LineInput
import edu.ie3.datamodel.models.input.connector.`type`.LineTypeInput
import edu.ie3.datamodel.models.input.system.characteristic.OlmCharacteristicInput
import edu.ie3.datamodel.models.input.{NodeInput, OperatorInput}
import edu.ie3.datamodel.utils.GridAndGeoUtils
import edu.ie3.simbench.exception.ConversionException
import edu.ie3.simbench.model.datamodel.types.LineType
import edu.ie3.simbench.model.datamodel.{Line, Node}
import edu.ie3.util.quantities.PowerSystemUnits.KILOMETRE
import org.locationtech.jts.geom.GeometryFactory
import tec.uom.se.quantity.Quantities

case object LineConverter extends LazyLogging {

  /**
    * Convert a full vector of [[Line]]s to [[LineInput]]s
    *
    * @param inputs Vector of input model
    * @param types  Mapping of SimBench to ie³ line types
    * @param nodes  Mapping of SimBench to ie³ nodes
    * @return       A [[Vector]] of [[LineInput]]s
    */
  def convert(
      inputs: Vector[Line[_ <: LineType]],
      types: Map[LineType, LineTypeInput],
      nodes: Map[Node, NodeInput]
  ): Vector[LineInput] =
    for (input <- inputs.filter(
           input =>
             input match {
               case _: Line.ACLine => true
               case _: Line.DCLine => false
             }
         )) yield {
      val (nodeA, nodeB) =
        NodeConverter.getNodes(input.nodeA, input.nodeB, nodes)
      val lineType = types.getOrElse(
        input.lineType,
        throw ConversionException(
          s"Cannot find conversion result for line type ${input.lineType.id}"
        )
      )
      convert(input, lineType, nodeA, nodeB)
    }

  /**
    * Converts a single [[Line]] to [[LineInput]]. [[Line.DCLine]] is not converted, as the ie³ data model does not
    * support it. Currently, the maximum loading from SimBench is not supported.
    *
    * @param input    Input model
    * @param lineType Corresponding line type to use
    * @param nodeA    [[NodeInput]] at end A
    * @param nodeB    [[NodeInput]] at end B
    * @param uuid     UUID to use for the model generation (default: Random UUID)
    * @return         A [[LineInput]] model
    */
  def convert(
      input: Line[_ <: LineType],
      lineType: LineTypeInput,
      nodeA: NodeInput,
      nodeB: NodeInput,
      uuid: UUID = UUID.randomUUID()
  ): LineInput = {
    input match {
      case input: Line.ACLine =>
        val id = input.id
        val length = Quantities.getQuantity(input.length, KILOMETRE)
        val geoPosition =
          GridAndGeoUtils.buildSafeLineStringBetweenNodes(nodeA, nodeB)
        new LineInput(
          uuid,
          id,
          OperatorInput.NO_OPERATOR_ASSIGNED,
          OperationTime.notLimited(),
          nodeA,
          nodeB,
          1,
          lineType,
          length,
          geoPosition,
          OlmCharacteristicInput.CONSTANT_CHARACTERISTIC
        )
      case _: Line.DCLine =>
        throw ConversionException(
          "Conversion of DC lines is currently not supported."
        )
    }
  }
}
