package edu.ie3.simbench.model.datamodel

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
