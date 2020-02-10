package edu.ie3.simbench.model.datamodel

import edu.ie3.simbench.model.datamodel.enums.MeasurementVariable

/**
  * Measurement device, that is able to measure different information at different elements
  *
  * @param id Identifier
  * @param elements List of [[EntityModel]]s that are covered by the measurement
  * @param variable The value, that is measured
  * @param subnet Subnet it belongs to
  * @param voltLvl Voltage level
  */
case class Measurement(id: String,
                       elements: List[EntityModel],
                       variable: MeasurementVariable,
                       subnet: String,
                       voltLvl: Int)
    extends EntityModel
