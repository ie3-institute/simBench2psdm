/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.simbench.model.datamodel

import edu.ie3.simbench.io.HeadLineField
import edu.ie3.simbench.io.HeadLineField.MandatoryField
import edu.ie3.simbench.model.RawModelData
import edu.ie3.simbench.model.datamodel.EntityModel.EntityModelCompanionObject

/**
  * Coordinate to describe a geographical location in the WGS84 reference system
  *
  * @param id Identifier
  * @param x Longitude
  * @param y Latitude
  * @param subnet Subnet it belongs to
  * @param voltLvl Voltage level
  */
final case class Coordinate(
    id: String,
    x: Double,
    y: Double,
    subnet: String,
    voltLvl: Int
) extends EntityModel

case object Coordinate extends EntityModelCompanionObject[Coordinate] {
  private val X = "x"
  private val Y = "y"

  /**
    * Get an Array of table fields denoting the mapping to the model's attributes
    *
    * @return Array of table headings
    */
  override def getFields: Array[HeadLineField] =
    Array(ID, SUBNET, VOLT_LVL, X, Y)
      .map(id => MandatoryField(id))

  /**
    * Factory method to build one model from a mapping from field id to value
    *
    * @param rawData mapping from field id to value
    * @return A model
    */
  override def apply(rawData: RawModelData): Coordinate = {
    val (id, subnet, voltLvl) = getBaseInformation(rawData)
    val x = rawData.getDouble(X)
    val y = rawData.getDouble(Y)

    Coordinate(id, x, y, subnet, voltLvl)
  }
}
