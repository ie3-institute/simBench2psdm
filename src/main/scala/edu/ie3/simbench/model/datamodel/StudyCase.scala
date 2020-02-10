package edu.ie3.simbench.model.datamodel

/**
  * Defining the circumstances of a study case
  * @param id Identifier
  * @param pLoad Scaling factor for loads (active power)
  * @param qLoad Scaling factor for loads (active repower)
  * @param pWind Scaling factor for wind (active power)
  * @param pPv Scaling factor for PV plant (active power)
  * @param pRes Scaling factor for renewable energy sources (active power)
  * @param vMSlack Voltage magnitude at the slack nodes
  */
case class StudyCase(id: String,
                     pLoad: BigDecimal,
                     qLoad: BigDecimal,
                     pWind: BigDecimal,
                     pPv: BigDecimal,
                     pRes: BigDecimal,
                     vMSlack: BigDecimal)
    extends SimbenchModel
