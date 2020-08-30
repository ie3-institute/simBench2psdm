/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
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
    p: Double,
    q: Double,
    soc: Double,
    sR: Double,
    eStore: Double,
    etaStore: Double,
    selfDischarge: Double,
    pMin: Double,
    pMax: Double,
    qMin: Double,
    qMax: Double,
    subnet: String,
    voltLvl: Int
) extends ShuntModel
