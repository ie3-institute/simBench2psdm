package edu.ie3.simbench.model.datamodel

import edu.ie3.simbench.io.HeadLineField
import edu.ie3.simbench.io.HeadLineField.MandatoryField
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
  override def getFields: Array[HeadLineField] =
    Array(Node.SUBSTATION, EntityModel.SUBNET, EntityModel.VOLT_LVL).map(id => MandatoryField(id))

  /**
    * Factory method to build a batch of models from a mapping from field id to value. It is allowed, that a node is not
    * assigned to a substation. Therefore, those nodes have to be filtered from the raw data.
    *
    * @param rawData mapping from field id to value
    * @return A [[Vector]] of models
    */
  override def buildModels(rawData: Vector[RawModelData]): Vector[Substation] = {
    val filteredRawData = rawData.filter(data =>
      data.get(Node.SUBSTATION) != "NULL" && data.get(Node.SUBSTATION).nonEmpty)
    for (entry <- filteredRawData) yield {
      buildModel(entry)
    }
  }

  /**
    * Factory method to build one model from a mapping from field id to value
    *
    * @param rawData mapping from field id to value
    * @return A model
    */
  override def buildModel(rawData: RawModelData): Substation = {
    val id = rawData.get(Node.SUBSTATION)
    val (_, subnet, voltLvl) = EntityModel.getBaseInformation(rawData)

    Substation(id, subnet, voltLvl)
  }
}
