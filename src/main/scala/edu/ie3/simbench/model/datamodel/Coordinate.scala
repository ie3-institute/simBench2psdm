package edu.ie3.simbench.model.datamodel

import edu.ie3.simbench.model.RawModelData
import edu.ie3.simbench.model.datamodel.SimbenchModel.SimbenchCompanionObject

/**
  * Coordinate to describe a geographical location in the WGS84 reference system
  *
  * @param id Identifier
  * @param x Longitude
  * @param y Latitude
  * @param subnet Subnet it belongs to
  * @param voltLvl Voltage level
  */
case class Coordinate(id: String,
                      x: BigDecimal,
                      y: BigDecimal,
                      subnet: String,
                      voltLvl: Int)
    extends EntityModel

case object Coordinate extends SimbenchCompanionObject[Coordinate] {
  val X = "x"
  val Y = "y"

  /**
    * Get an Array of table fields denoting the mapping to the model's attributes
    *
    * @return Array of table headings
    */
  override def getFields: Array[String] =
    Array(SimbenchModel.ID, EntityModel.SUBNET, EntityModel.VOLT_LVL, X, Y)

  /**
    * Factory method to build one model from a mapping from field id to value
    *
    * @param rawData mapping from field id to value
    * @return A model
    */
  override def buildModel(rawData: RawModelData): Coordinate = {
    val (id, subnet, voltLvl) = EntityModel.getBaseInformation(rawData)
    val x = BigDecimal(rawData.get(X))
    val y = BigDecimal(rawData.get(Y))

    Coordinate(id, x, y, subnet, voltLvl)
  }
}
