/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.simbench.model.datamodel

import edu.ie3.simbench.exception.io.SimbenchDataModelException
import edu.ie3.simbench.io.HeadLineField
import edu.ie3.simbench.io.HeadLineField.MandatoryField
import edu.ie3.simbench.model.RawModelData
import edu.ie3.simbench.model.datamodel.EntityModel.EntityModelCompanionObject
import edu.ie3.simbench.model.datamodel.types.LineType
import edu.ie3.simbench.model.datamodel.types.LineType.{ACLineType, DCLineType}

sealed trait Line[T <: LineType] extends EntityModel {
  val nodeA: Node
  val nodeB: Node
  val lineType: T
  val length: Double
  val loadingMax: Double
}

object Line extends EntityModelCompanionObject[Line[_ <: LineType]] {

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
  final case class ACLine(
      id: String,
      nodeA: Node,
      nodeB: Node,
      lineType: ACLineType,
      length: Double,
      loadingMax: Double,
      subnet: String,
      voltLvl: Int
  ) extends Line[ACLineType]

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
  final case class DCLine(
      id: String,
      nodeA: Node,
      nodeB: Node,
      lineType: DCLineType,
      length: Double,
      loadingMax: Double,
      subnet: String,
      voltLvl: Int
  ) extends Line[DCLineType]

  private val NODE_A = "nodeA"
  private val NODE_B = "nodeB"
  private val LINE_TYPE = "type"
  private val LENGTH = "length"
  private val LOADING_MAX = "loadingMax"

  /**
    * Get an Array of table fields denoting the mapping to the model's attributes
    *
    * @return Array of table headings
    */
  override def getFields: Array[HeadLineField] =
    Array(ID, NODE_A, NODE_B, LINE_TYPE, LENGTH, LOADING_MAX, SUBNET, VOLT_LVL)
      .map(id => MandatoryField(id))

  /**
    * Factory method to build one model from a mapping from field id to value
    *
    * @param rawData mapping from field id to value
    * @return A model
    */
  override def apply(rawData: RawModelData): Line[_ <: LineType] =
    throw SimbenchDataModelException(
      s"No basic implementation of model creation available for ${this.getClass.getSimpleName}"
    )

  /**
    * Factory method to build a batch of models from a mapping from field id to value
    *
    * @param rawData    mapping from field id to value
    * @param nodes      Nodes to use for mapping
    * @param lineTypes  Line types to use for mapping
    * @return A [[Vector]] of models
    */
  def buildModels(
      rawData: Vector[RawModelData],
      nodes: Map[String, Node],
      lineTypes: Map[String, LineType]
  ): Vector[Line[_ <: LineType]] =
    for (entry <- rawData) yield {
      val (nodeA, nodeB) =
        getNodes(entry.get(NODE_A), entry.get(NODE_B), nodes)
      val lineType = lineTypes.getOrElse(
        entry.get(LINE_TYPE),
        throw SimbenchDataModelException(
          s"Cannot build ${this.getClass.getSimpleName} '${entry.get("id")}', as suitable reference to $LINE_TYPE cannot be found."
        )
      )

      buildModel(entry, nodeA, nodeB, lineType)
    }

  /**
    * Factory method to build one model from a mapping from field id to value
    *
    * @param rawData mapping from field id to value
    * @param nodeA    Node to use at port A
    * @param nodeB    Node to use at port B
    * @param lineType Line type to use for this line
    * @return A model
    */
  def buildModel(
      rawData: RawModelData,
      nodeA: Node,
      nodeB: Node,
      lineType: LineType
  ): Line[_ <: LineType] = {
    val (id, subnet, voltLvl) = getBaseInformation(rawData)
    val length = rawData.getDouble(LENGTH)
    val loadingMax = rawData.getDouble(LOADING_MAX)

    lineType match {
      case acLineType: ACLineType =>
        ACLine(
          id,
          nodeA,
          nodeB,
          acLineType,
          length,
          loadingMax,
          subnet,
          voltLvl
        )
      case dcLineType: DCLineType =>
        DCLine(
          id,
          nodeA,
          nodeB,
          dcLineType,
          length,
          loadingMax,
          subnet,
          voltLvl
        )
    }
  }
}
