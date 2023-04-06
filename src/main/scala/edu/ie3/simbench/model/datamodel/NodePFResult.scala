package edu.ie3.simbench.model.datamodel

import edu.ie3.simbench.exception.io.SimbenchDataModelException
import edu.ie3.simbench.io.HeadLineField
import edu.ie3.simbench.io.HeadLineField.{MandatoryField, OptionalField}
import edu.ie3.simbench.model.RawModelData
import edu.ie3.simbench.model.datamodel.EntityModel.EntityModelCompanionObject

/** Model class for nodal power flow results
  *
  * @param node
  *   Reference to the node
  * @param vm
  *   Voltage magnitude in p.u.
  * @param va
  *   Voltage angle in degree
  * @param maybeSubstation
  *   Optional information about the substation assignment
  * @param subnet
  *   Information about subnet assignment
  * @param voltLvl
  *   Voltage level information
  */
final case class NodePFResult(
    node: Node,
    vm: BigDecimal,
    va: BigDecimal,
    maybeSubstation: Option[Substation],
    subnet: String,
    voltLvl: Int
) extends EntityModel {

  /** Identifier
    */
  override val id: String = "NO ID NEEDED"
}

case object NodePFResult extends EntityModelCompanionObject[NodePFResult] {
  private val NODE: String = "node"
  private val V_M: String = "vm"
  private val V_A: String = "va"
  private val SUBSTATION: String = "substation"

  /** Get an Array of table fields denoting the mapping to the model's
    * attributes
    *
    * @return
    *   Array of table headings
    */
  override def getFields: Array[HeadLineField] =
    Array(NODE, V_M, V_A, SUBNET, VOLT_LVL).map(MandatoryField) ++ Array[
      HeadLineField
    ](
      OptionalField(
        SUBSTATION
      )
    )

  /** Factory method to build one model from a mapping from field id to value
    *
    * @param rawData
    *   mapping from field id to value
    * @return
    *   A model
    */
  override def apply(rawData: RawModelData): NodePFResult =
    throw SimbenchDataModelException(
      s"No basic implementation of model creation available for ${this.getClass.getSimpleName}"
    )

  /** Factory method to build a batch of models from a mapping from field id to
    * value
    *
    * @param rawData
    *   mapping from field id to value
    * @param nodes
    *   Nodes to use for mapping
    * @param substations
    *   Substations to use for mapping
    * @return
    *   A [[Vector]] of models
    */
  def buildModels(
      rawData: Vector[RawModelData],
      nodes: Map[String, Node],
      substations: Map[String, Substation]
  ): Vector[NodePFResult] =
    for (entry <- rawData) yield {
      val node = getNode(entry.get(NODE), nodes)
      val maybeSubstation = substations.get(entry.get(SUBSTATION))

      buildModel(entry, node, maybeSubstation)
    }

  /** Factory method to build one model from a mapping from field id to value
    *
    * @param rawData
    *   mapping from field id to value
    * @param node
    *   Node to use
    * @param maybeSubstation
    *   Substation information
    * @return
    *   A model
    */
  def buildModel(
      rawData: RawModelData,
      node: Node,
      maybeSubstation: Option[Substation]
  ): NodePFResult = {
    val vm = rawData.getBigDecimal(V_M)
    val va = rawData.getBigDecimal(V_A)
    val subnet = rawData.get(SUBNET)
    val voltLvl = rawData.getInt(VOLT_LVL)

    NodePFResult(
      node,
      vm,
      va,
      maybeSubstation,
      subnet,
      voltLvl
    )
  }
}
