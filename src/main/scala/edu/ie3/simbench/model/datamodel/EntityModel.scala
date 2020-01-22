package edu.ie3.simbench.model.datamodel

/**
  * Common attributes to actual entities
  */
trait EntityModel extends SimbenchModel {

  /**
    * Subnet it belongs to
    */
  val subnet: String

  /**
    * Voltage level
    */
  val voltLvl: Int
}
