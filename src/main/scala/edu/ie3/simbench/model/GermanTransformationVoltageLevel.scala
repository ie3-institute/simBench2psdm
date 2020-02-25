package edu.ie3.simbench.model

import edu.ie3.models.VoltageLevel

sealed trait GermanTransformationVoltageLevel

/**
  * Voltage level description at the transforming levels between two actual voltage levels
  */
object GermanTransformationVoltageLevel {

  /**
    * Case class to denote a voltage level, which is connecting two actual voltage levels
    * @param id Identifier
    */
  class TransformationVoltageLevel(id: String) extends VoltageLevel {
    override def getName: String = id
  }

  case object MV2LV
      extends TransformationVoltageLevel("Medium to low voltage level")
      with GermanTransformationVoltageLevel
  case object HV2MV
      extends TransformationVoltageLevel("High to medium voltage level")
      with GermanTransformationVoltageLevel
  case object EHV2HV
      extends TransformationVoltageLevel("Extra high to high voltage level")
      with GermanTransformationVoltageLevel
}
