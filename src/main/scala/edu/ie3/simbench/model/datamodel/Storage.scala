package edu.ie3.simbench.model.datamodel

import edu.ie3.simbench.model.datamodel.enums.StorageType
import edu.ie3.simbench.model.datamodel.profiles.StorageProfile

/**
  * Model of an electric storage
  *
  * @param id Identifier
  * @param node [[Node]] it is connected to
  * @param storageType Type of storage
  * @param profile The [[StorageProfile]] to apply
  * @param p Active power contribution in MW
  * @param q Reactive power contribtuion in MVAr
  * @param soc State of charge in %
  * @param sR Rated apparent power
  * @param eStore Capacity of the storage in MWh
  * @param etaStore Efficiency of the storage in MWh
  * @param selfDischarge Self discharge ratio per day in % / Day
  * @param pMin Minimum permissible active power contribution in MW
  * @param pMax Maximum permissible active power contribution in MVAr
  * @param qMin Minimum permissible reactive power contribution in MW
  * @param qMax Maximum permissible reactive power contribution in MVAr
  * @param subnet Subnet it belongs to
  * @param voltLvl Voltage level
  */
final case class Storage(
    id: String,
    node: Node,
    storageType: StorageType,
    profile: StorageProfile,
    p: BigDecimal,
    q: BigDecimal,
    soc: BigDecimal,
    sR: BigDecimal,
    eStore: BigDecimal,
    etaStore: BigDecimal,
    selfDischarge: BigDecimal,
    pMin: BigDecimal,
    pMax: BigDecimal,
    qMin: BigDecimal,
    qMax: BigDecimal,
    subnet: String,
    voltLvl: Int
) extends ShuntModel
