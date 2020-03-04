package edu.ie3.simbench.model.datamodel

import edu.ie3.simbench.io.HeadLineField
import edu.ie3.simbench.io.HeadLineField.MandatoryField
import edu.ie3.simbench.model.RawModelData
import edu.ie3.simbench.model.datamodel.SimbenchModel.SimbenchCompanionObject

/**
  * Defining the circumstances of a study case
  *
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

case object StudyCase extends SimbenchCompanionObject[StudyCase] {
  val ID = "Study Case"
  val P_LOAD = "pload"
  val Q_LOAD = "qload"
  val P_WIND = "Wind_p"
  val P_PV = "PV_p"
  val P_RES = "RES_p"
  val VM_SLACK = "Slack_vm"

  /**
    * Get an Array of table fields denoting the mapping to the model's attributes
    *
    * @return Array of table headings
    */
  override def getFields: Array[HeadLineField] =
    Array(ID, P_LOAD, Q_LOAD, P_WIND, P_PV, P_RES, VM_SLACK).map(id =>
      MandatoryField(id))

  /**
    * Factory method to build one model from a mapping from field id to value
    *
    * @param rawData mapping from field id to value
    * @return A model
    */
  override def apply(rawData: RawModelData): StudyCase = {
    val id = rawData.get(ID)
    val pLoad = BigDecimal(rawData.get(P_LOAD))
    val qLoad = BigDecimal(rawData.get(Q_LOAD))
    val pWind = BigDecimal(rawData.get(P_WIND))
    val pPv = BigDecimal(rawData.get(P_PV))
    val pRes = BigDecimal(rawData.get(P_RES))
    val vMSlack = BigDecimal(rawData.get(VM_SLACK))

    StudyCase(id, pLoad, qLoad, pWind, pPv, pRes, vMSlack)
  }
}
