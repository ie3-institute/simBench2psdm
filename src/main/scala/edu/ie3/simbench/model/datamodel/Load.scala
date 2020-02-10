package edu.ie3.simbench.model.datamodel

import edu.ie3.simbench.model.datamodel.profiles.LoadProfileType

/**
  * Model of an electrical load
  *
  * @param id Identifier
  * @param node The node the load is connected to
  * @param profile Profile type to use for time series calculation
  * @param pLoad Active power consumption in MW
  * @param qLoad Reactive power consumption in MVAr
  * @param sR Rated apparent power in MVA
  * @param subnet Subnet it belongs to
  * @param voltLvl Voltage level
  */
case class Load(id: String,
                node: Node,
                profile: LoadProfileType,
                pLoad: BigDecimal,
                qLoad: BigDecimal,
                sR: BigDecimal,
                subnet: String,
                voltLvl: Int)
    extends EntityModel
