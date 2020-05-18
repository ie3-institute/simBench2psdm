package edu.ie3.simbench.model.datamodel

import edu.ie3.simbench.exception.io.SimbenchDataModelException
import edu.ie3.simbench.io.HeadLineField
import edu.ie3.simbench.io.HeadLineField.MandatoryField
import edu.ie3.simbench.model.RawModelData
import edu.ie3.simbench.model.datamodel.Node.NodeKey
import edu.ie3.simbench.model.datamodel.EntityModel.EntityModelCompanionObject
import edu.ie3.simbench.model.datamodel.SimbenchModel.SimbenchCompanionObject
import edu.ie3.simbench.model.datamodel.enums.NodeType

/**
  * Electrical node
  *
  * @param id Identifier
  * @param nodeType Type of the node
  * @param vmSetp Setpoint for the voltage magnitude in p.u.
  * @param vaSetp Setpoint for the voltage angle in ° (Optional)
  * @param vmR Rated voltage magnitude in kV
  * @param vmMin Minimum permissible voltage magnitude in p.u.
  * @param vmMax Maximum permissible voltage magnitude in p.u.
  * @param substation Substation the node belongs to
  * @param coordinate Coordinate at which it is located
  * @param subnet Subnet it belongs to
  * @param voltLvl Voltage level
  */
final case class Node(
    id: String,
    nodeType: NodeType,
    vmSetp: Option[BigDecimal] = None,
    vaSetp: Option[BigDecimal] = None,
    vmR: BigDecimal,
    vmMin: BigDecimal,
    vmMax: BigDecimal,
    substation: Option[Substation] = None,
    coordinate: Option[Coordinate] = None,
    subnet: String,
    voltLvl: Int
) extends EntityModel {

  /**
    * Generating the key, to uniquely identify this node
    *
    * @return The node's key as (id, subnet, voltLvl)
    */
  def getKey: NodeKey = NodeKey(id, subnet, voltLvl)
}

case object Node extends EntityModelCompanionObject[Node] {
  private val NODE_TYPE = "type"
  private val VMR = "vmR"
  private val VM_SETP = "vmSetp"
  private val VA_SETP = "vaSetp"
  private val V_M_MIN = "vmMin"
  private val V_M_MAX = "vmMax"
  private val SUBSTATION = "substation"
  private val COORDINATE = "coordID"

  /**
    * Get an Array of table fields denoting the mapping to the model's attributes
    *
    * @return Array of table headings
    */
  override def getFields: Array[HeadLineField] =
    Array(
      ID,
      VOLT_LVL,
      SUBNET,
      NODE_TYPE,
      VM_SETP,
      VA_SETP,
      VMR,
      V_M_MIN,
      V_M_MAX,
      SUBSTATION,
      COORDINATE
    ).map(id => MandatoryField(id))

  /**
    * Factory method to build a batch of models from a mapping from field id to value
    *
    * @param rawData      Mapping from field id to value
    * @param coordinates  Mapping from coordinate id to coordinate itself
    * @param substations  Mapping from substation id to substation itself
    * @return A [[Vector]] of models
    */
  def buildModels(
      rawData: Vector[RawModelData],
      coordinates: Map[String, Coordinate],
      substations: Map[String, Substation]
  ): Vector[Node] = {
    for (entry <- rawData) yield {
      val coordinate = coordinates.get(entry.get(Node.COORDINATE))
      val substation = substations.get(entry.get(Node.SUBSTATION))
      buildModel(entry, coordinate, substation)
    }
  }

  /**
    * Factory method to build one model from a mapping from field id to value
    *
    * @param rawData    mapping from field id to value
    * @param coordinate Option to a coordinate to use
    * @param substation Option to a substation to use
    * @return A [[Node]] model
    */
  def buildModel(
      rawData: RawModelData,
      coordinate: Option[Coordinate],
      substation: Option[Substation]
  ): Node = {
    val (id, subnet, voltLvl) = getBaseInformation(rawData)
    val nodeType = NodeType(rawData.get(NODE_TYPE))
    val vmSetp = rawData.getBigDecimalOption(VM_SETP)
    val vaSetp = rawData.getBigDecimalOption(VA_SETP)
    val vmR = rawData.getBigDecimal(VMR)
    val vmMin = rawData.getBigDecimal(V_M_MIN)
    val vmMax = rawData.getBigDecimal(V_M_MAX)

    Node(
      id,
      nodeType,
      vmSetp,
      vaSetp,
      vmR,
      vmMin,
      vmMax,
      substation,
      coordinate,
      subnet,
      voltLvl
    )
  }

  /**
    * Factory method to build one model from a mapping from field id to value
    *
    * @param rawData mapping from field id to value
    * @return A model
    */
  override def apply(rawData: RawModelData): Node =
    throw SimbenchDataModelException(
      s"No basic implementation of model creation available for ${this.getClass.getSimpleName}"
    )

  /**
    * Key to uniquely identify different nodes in the SimBench data set.
    *
    * @param id       Identifier
    * @param subnet   Subnet description
    * @param voltLvl  Voltage level
    */
  final case class NodeKey(id: String, subnet: String, voltLvl: Int)
}
