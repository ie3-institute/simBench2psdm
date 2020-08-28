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
import edu.ie3.simbench.model.datamodel.enums.SwitchType

/**
  * Modeling switches between nodes
  *
  * @param id Identifier
  * @param nodeA Node at end A
  * @param nodeB Node at end B
  * @param switchType Type of the switch
  * @param cond true, if the switch ist closed
  * @param substation Substation it belongs to
  * @param subnet Subnet it belongs to
  * @param voltLvl Voltage level
  */
final case class Switch(
    id: String,
    nodeA: Node,
    nodeB: Node,
    switchType: SwitchType,
    cond: Boolean,
    substation: Option[Substation],
    subnet: String,
    voltLvl: Int
) extends EntityModel

case object Switch extends EntityModelCompanionObject[Switch] {
  private val NODE_A = "nodeA"
  private val NODE_B = "nodeB"
  private val SWITCH_TYPE = "type"
  private val COND = "cond"
  private val SUBSTATION = "substation"

  /**
    * Get an Array of table fields denoting the mapping to the model's attributes
    *
    * @return Array of table headings
    */
  override def getFields: Array[HeadLineField] =
    Array(ID, NODE_A, NODE_B, SWITCH_TYPE, COND, SUBSTATION, SUBNET, VOLT_LVL)
      .map(
        id => MandatoryField(id)
      )

  /**
    * Factory method to build one model from a mapping from field id to value
    *
    * @param rawData mapping from field id to value
    * @return A model
    */
  override def apply(rawData: RawModelData): Switch =
    throw SimbenchDataModelException(
      s"No basic implementation of model creation available for ${this.getClass.getSimpleName}"
    )

  /**
    * Factory method to build a batch of models from a mapping from field id to value
    *
    * @param rawData mapping from field id to value
    * @return A [[Vector]] of models
    */
  def buildModels(
      rawData: Vector[RawModelData],
      nodes: Map[String, Node],
      substations: Map[String, Substation]
  ): Vector[Switch] =
    for (entry <- rawData) yield {
      val (nodeA, nodeB) =
        getNodes(entry.get(NODE_A), entry.get(NODE_B), nodes)
      val substation = substations.get(entry.get(SUBSTATION))

      buildModel(entry, nodeA, nodeB, substation)
    }

  /**
    * Factory method to build one model from a mapping from field id to value
    *
    * @param rawData mapping from field id to value
    * @param nodeA      Node at port A
    * @param nodeB      Node at port B
    * @param substation Substation to use
    * @return A model
    */
  def buildModel(
      rawData: RawModelData,
      nodeA: Node,
      nodeB: Node,
      substation: Option[Substation]
  ): Switch = {
    val (id, subnet, voltLvl) = getBaseInformation(rawData)
    val switchType = SwitchType(rawData.get(SWITCH_TYPE))
    val cond = rawData.getBoolean(COND)

    Switch(id, nodeA, nodeB, switchType, cond, substation, subnet, voltLvl)
  }
}
