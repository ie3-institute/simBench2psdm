package edu.ie3.simbench.model.datamodel

import edu.ie3.simbench.model.datamodel.EntityModel.EntityCompanionObject

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

case object Coordinate extends EntityCompanionObject[Coordinate] {
  val X = "x"
  val Y = "y"

  /**
    * Get an Array of table fields denoting the mapping to the model's attributes
    *
    * @return Array of table headings
    */
  override def getFields: Array[String] =
    Array(SimbenchModel.ID, EntityModel.SUBNET, EntityModel.VOLT_LVL, X, Y)

  def apply(fieldValues: Map[String, String]): Coordinate = {
    checkFields(fieldValues)

    val id = extractId(fieldValues)
    val subnet = extractId(fieldValues)
    val voltLvl = extractVoltLvl(fieldValues)
    val x =
      fieldValues.getOrElse(X, throw getFieldNotFoundException(X)).toDouble
    val y =
      fieldValues.getOrElse(Y, throw getFieldNotFoundException(Y)).toDouble

    new Coordinate(id, x, y, subnet, voltLvl)
  }
}
