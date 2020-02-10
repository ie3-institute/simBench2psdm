package edu.ie3.simbench.model.datamodel

/**
  * Model to group different assets in one substation
  *
  * @param id Identifier
  * @param subnet Subnet it belongs to
  * @param voltLvl Voltage level
  */
case class Substation(id: String, subnet: String, voltLvl: Int)
    extends EntityModel
