package edu.ie3.simbench.model.datamodel

/**
  * Generic shunt model
  *
  * @param id Identifier
  * @param node [[Node]] it is connected to
  * @param p0 Active power contribution in MW
  * @param q0 Reactive power contribution in MVAr
  * @param vmR Rated voltage magnitude in kV
  * @param step Current step
  * @param subnet Subnet it belongs to
  * @param voltLvl Voltage level
  */
final case class Shunt(
    id: String,
    node: Node,
    p0: BigDecimal,
    q0: BigDecimal,
    vmR: BigDecimal,
    step: Int,
    subnet: String,
    voltLvl: Int
) extends ShuntModel
