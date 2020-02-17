package edu.ie3.simbench.model.datamodel

import edu.ie3.simbench.model.datamodel.SimbenchModel.SimbenchCompanionObject
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
    extends ShuntModel

case object Load extends SimbenchCompanionObject[Load] {

  /**
    * Get an Array of table fields denoting the mapping to the model's attributes
    *
    * @return Array of table headings
    */
  override def getFields: Array[String] =
    Array("id", "node", "profile", "pLoad", "qLoad", "sR", "subnet", "voltLvl")
}
