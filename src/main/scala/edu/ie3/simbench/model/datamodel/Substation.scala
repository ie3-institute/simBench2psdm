package edu.ie3.simbench.model.datamodel

import edu.ie3.simbench.model.RawModelData
import edu.ie3.simbench.model.datamodel.SimbenchModel.SimbenchCompanionObject

/**
  * Model to group different assets in one substation
  *
  * @param id Identifier
  * @param subnet Subnet it belongs to
  * @param voltLvl Voltage level
  */
case class Substation(id: String, subnet: String, voltLvl: Int)
    extends EntityModel

case object Substation extends SimbenchCompanionObject[Substation] {

  /**
    * Get an Array of table fields denoting the mapping to the model's attributes
    *
    * @return Array of table headings
    */
  override def getFields: Array[String] =
    Array(SimbenchModel.ID, EntityModel.SUBNET, EntityModel.VOLT_LVL)

  /**
    * Factory method to build one model from a mapping from field id to value
    *
    * @param rawData mapping from field id to value
    * @return A model
    */
  override def buildModel(rawData: RawModelData): Substation = ???
}
