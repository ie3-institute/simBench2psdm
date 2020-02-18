package edu.ie3.simbench.model.datamodel

import edu.ie3.simbench.model.RawModelData
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
case class Node(id: String,
                nodeType: NodeType,
                vmSetp: BigDecimal,
                vaSetp: Option[BigDecimal] = None,
                vmR: BigDecimal,
                vmMin: BigDecimal,
                vmMax: BigDecimal,
                substation: Substation,
                coordinate: Coordinate,
                subnet: String,
                voltLvl: Int)
    extends EntityModel

case object Node extends SimbenchCompanionObject[Node] {
  val NODE_TYPE = "type"
  val VMR = "vmR"
  val VM_SETP = "vmSetp"
  val VA_SETP = "vaSetp"
  val V_M_MIN = "vmMin"
  val V_M_MAX = "vmMax"
  val SUBSTATION = "substation"
  val COORDINATE = "coordID"

  /**
    * Get an Array of table fields denoting the mapping to the model's attributes
    *
    * @return Array of table headings
    */
  override def getFields: Array[String] =
    Array(SimbenchModel.ID,
          EntityModel.VOLT_LVL,
          EntityModel.SUBNET,
          NODE_TYPE,
          VM_SETP,
          VA_SETP,
          V_M_MIN,
          V_M_MAX,
          SUBSTATION,
          COORDINATE)

  /**
    * Factory method to build one model from a mapping from field id to value
    *
    * @param rawData mapping from field id to value
    * @return A model
    */
  override def buildModel(rawData: RawModelData): Node = ???
}
