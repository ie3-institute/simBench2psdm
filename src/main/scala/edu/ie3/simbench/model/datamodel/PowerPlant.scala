package edu.ie3.simbench.model.datamodel

import edu.ie3.simbench.model.datamodel.enums.{CalculationType, PowerPlantType}
import edu.ie3.simbench.model.datamodel.profiles.PowerPlantProfileType

/**
  * A power plant
  *
  * @param id Identifier
  * @param node Node at which this entity is connected to
  * @param powerPlantType Type of the power plant
  * @param profile Profile to follow
  * @param calculationType The way, the power flow calculation is done
  * @param dspf Distributed Slack Power Factor in p.u.
  * @param p Active power contribution in MW
  * @param q Reactive power contribution in MVAr
  * @param sR Rated apperent power in MVA
  * @param pMin Minimum permissible active power contribution in MW
  * @param pMax Maximum permissible active power contribution in MW
  * @param qMin Minimum permissible reactive power contribution in MVAr
  * @param qMax Maximum permissible reactive power contribution in MVAr
  * @param subnet Subnet it belongs to
  * @param voltLvl Voltage level
  */
case class PowerPlant(id: String,
                      node: Node,
                      powerPlantType: PowerPlantType,
                      profile: PowerPlantProfileType,
                      calculationType: CalculationType,
                      dspf: BigDecimal,
                      p: BigDecimal,
                      q: BigDecimal,
                      sR: BigDecimal,
                      pMin: BigDecimal,
                      pMax: BigDecimal,
                      qMin: BigDecimal,
                      qMax: BigDecimal,
                      subnet: String,
                      voltLvl: Int)
    extends ShuntModel
