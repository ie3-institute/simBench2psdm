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

case object EntityModel {

  /**
    * Table field for subnet
    */
  val SUBNET: String = "subnet"

  /**
    * Table field for voltage level
    */
  val VOLT_LVL: String = "voltLvl"
}
