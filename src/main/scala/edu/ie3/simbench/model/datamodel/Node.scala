package edu.ie3.simbench.model.datamodel

import edu.ie3.simbench.exception.io.IoException
import edu.ie3.simbench.model.datamodel.EntityModel.EntityCompanionObject
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

case object Node extends EntityCompanionObject[Node] {
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
    * Factory method to build a certain model class from a map of desired fields to content
    *
    * @param fieldValues Mapping of desired fields to their content
    * @param gridModel   Total grid model as of current status
    * @param coordinates Vector of currently known coordinates
    * @return An instance of the model class
    */
  def apply(fieldValues: Map[String, String],
            gridModel: GridModel,
            coordinates: Vector[Coordinate]): Node = {
    checkFields(fieldValues)

    val id = extractId(fieldValues)
    val subnet = extractId(fieldValues)
    val voltLvl = extractVoltLvl(fieldValues)
    val nodeType =
      NodeType(
        fieldValues.getOrElse(NODE_TYPE,
                              throw getFieldNotFoundException(NODE_TYPE)))
    val vmR =
      fieldValues.getOrElse(VMR, throw getFieldNotFoundException(VMR)).toDouble
    val vmSetp =
      fieldValues
        .getOrElse(VM_SETP, throw getFieldNotFoundException(VM_SETP))
        .toDouble
    val vaSetp =
      fieldValues
        .getOrElse(VA_SETP, throw getFieldNotFoundException(VA_SETP))
        .toDouble
    val vmMin =
      fieldValues
        .getOrElse(V_M_MIN, throw getFieldNotFoundException(V_M_MIN))
        .toDouble
    val vmMax =
      fieldValues
        .getOrElse(V_M_MAX, throw getFieldNotFoundException(V_M_MAX))
        .toDouble
    val substationId = fieldValues.getOrElse(
      SUBSTATION,
      throw getFieldNotFoundException(SUBSTATION))
    val substation = gridModel.substations
      .find(substation => substation.id == substationId)
      .getOrElse(throw IoException(
        s"Cannot find a substation named $id among the currently known substations"))
    val coordinateId =
      fieldValues.getOrElse(COORDINATE,
                            throw getFieldNotFoundException(COORDINATE))
    val coordinate = coordinates
      .find(coordinate => coordinate.id == coordinateId)
      .getOrElse(throw IoException(
        s"Cannot find a coordinate named $id among the currently known coordinates"))

    new Node(id,
             nodeType,
             vmSetp,
             Option(vaSetp),
             vmR,
             vmMin,
             vmMax,
             substation,
             coordinate,
             subnet,
             voltLvl)
  }

  /**
    * Factory method to build one model from a mapping from field id to value
    *
    * @param fieldToValueMap mapping from field id to value
    * @return A model
    */
  override def buildModel(fieldToValueMap: Map[String, String]): Node = ???
}
