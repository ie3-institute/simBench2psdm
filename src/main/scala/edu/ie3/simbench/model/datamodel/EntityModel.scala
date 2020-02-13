package edu.ie3.simbench.model.datamodel

import edu.ie3.simbench.model.datamodel.SimbenchModel.SimbenchCompanionObject

import scala.reflect.ClassTag

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

  abstract class EntityCompanionObject[C <: SimbenchModel]
      extends SimbenchCompanionObject[C] {

    /**
      * Extract the subnet from the field values
      *
      * @param fieldValues Field values to get information from
      * @return The information
      */
    protected def extractSubnet(fieldValues: Map[String, String]): String = {
      implicit val tag: ClassTag[EntityModel] =
        ClassTag.apply(EntityModel.getClass)
      fieldValues.getOrElse(SUBNET,
                            throw getFieldNotFoundException(SUBNET)(
                              ClassTag.apply(classOf[EntityModel])))
    }

    /**
      * Extract the voltage level from the field values
      *
      * @param fieldValues Field values to get information from
      * @return The information
      */
    protected def extractVoltLvl(fieldValues: Map[String, String]): Int = {
      implicit val tag: ClassTag[EntityModel] =
        ClassTag.apply(EntityModel.getClass)
      fieldValues
        .getOrElse(VOLT_LVL,
                   throw getFieldNotFoundException(VOLT_LVL)(
                     ClassTag.apply(classOf[EntityModel])))
        .toInt
    }
  }
}
