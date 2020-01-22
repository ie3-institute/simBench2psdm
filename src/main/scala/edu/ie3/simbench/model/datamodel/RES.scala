package edu.ie3.simbench.model.datamodel

import edu.ie3.simbench.model.datamodel.enums.{CalculationType, ResType}
import edu.ie3.simbench.model.datamodel.profiles.ResProfile

/**
  * A renewable energy sources asset
  *
  * @param id Identifier
  * @param node Node at which this entity is connected to
  * @param resType Type of renewable energy source
  * @param profile Profile to follow
  * @param calculationType The way the asset may be accounted for in the simulation
  * @param p Active power contribution in MW
  * @param q Reactive power contribution in MVAr
  * @param sR Rated apparent power in MVA
  * @param subnet Subnet it belongs to
  * @param voltLvl Voltage level
  */
case class RES(id: String,
               node: Node,
               resType: ResType,
               profile: ResProfile,
               calculationType: CalculationType,
               p: BigDecimal,
               q: BigDecimal,
               sR: BigDecimal,
               subnet: String,
               voltLvl: Int)
    extends ShuntModel
