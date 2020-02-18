package edu.ie3.simbench.model.datamodel

import edu.ie3.simbench.model.datamodel.EntityModel.EntityCompanionObject

/**
  * Model to group different assets in one substation
  *
  * @param id Identifier
  * @param subnet Subnet it belongs to
  * @param voltLvl Voltage level
  */
case class Substation(id: String, subnet: String, voltLvl: Int)
    extends EntityModel

case object Substation extends EntityCompanionObject[Substation] {

  /**
    * Get an Array of table fields denoting the mapping to the model's attributes
    *
    * @return Array of table headings
    */
  override def getFields: Array[String] =
    Array(SimbenchModel.ID, EntityModel.SUBNET, EntityModel.VOLT_LVL)

  /**
    * Factory method to build a certain model class from a map of desired fields to content
    *
    * @param fieldValues Mapping of desired fields to their content
    * @param gridModel   Total grid model as of current status
    * @return An instance of the model class
    */
  def apply(fieldValues: Map[String, String],
            gridModel: GridModel): Substation = {
    checkFields(fieldValues)

    val id = extractId(fieldValues)
    val subnet = extractSubnet(fieldValues)
    val voltLvl = extractVoltLvl(fieldValues)

    new Substation(id, subnet, voltLvl)
  }

  /**
    * Factory method to build one model from a mapping from field id to value
    *
    * @param fieldToValueMap mapping from field id to value
    * @return A model
    */
  override def buildModel(fieldToValueMap: Map[String, String]): Substation =
    ???
}
