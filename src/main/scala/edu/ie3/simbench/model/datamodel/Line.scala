package edu.ie3.simbench.model.datamodel

import edu.ie3.simbench.model.datamodel.types.LineType
import edu.ie3.simbench.model.datamodel.types.LineType.{ACLineType, DCLineType}

sealed trait Line[T <: LineType] extends EntityModel {
  val nodeA: Node
  val nodeB: Node
  val lineType: T
  val length: BigDecimal
  val loadingMax: BigDecimal
}

object Line {

  /**
    * AC line model
    *
    * @param id         Identifier
    * @param nodeA      The node at end A
    * @param nodeB      The node at end B
    * @param lineType   The line type
    * @param length     Length of the line in km
    * @param loadingMax Maximum permissible loading in %
    * @param subnet     Subnet it belongs to
    * @param voltLvl    Voltage level
    */
  case class ACLine(id: String,
                    nodeA: Node,
                    nodeB: Node,
                    lineType: ACLineType,
                    length: BigDecimal,
                    loadingMax: BigDecimal,
                    subnet: String,
                    voltLvl: Int)
      extends Line[ACLineType]

  /**
    * DC line model
    *
    * @param id         Identifier
    * @param nodeA      The node at end A
    * @param nodeB      The node at end B
    * @param lineType   The line type
    * @param length     Length of the line in km
    * @param loadingMax Maximum permissible loading in %
    * @param subnet     Subnet it belongs to
    * @param voltLvl    Voltage level
    */
  case class DCLine(id: String,
                    nodeA: Node,
                    nodeB: Node,
                    lineType: DCLineType,
                    length: BigDecimal,
                    loadingMax: BigDecimal,
                    subnet: String,
                    voltLvl: Int)
      extends Line[DCLineType]
}
