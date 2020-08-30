/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.simbench.model.datamodel

import edu.ie3.simbench.exception.io.SimbenchDataModelException
import edu.ie3.simbench.io.HeadLineField
import edu.ie3.simbench.io.HeadLineField.MandatoryField
import edu.ie3.simbench.model.RawModelData
import edu.ie3.simbench.model.datamodel.EntityModel.EntityModelCompanionObject
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
final case class PowerPlant(
    id: String,
    node: Node,
    powerPlantType: PowerPlantType,
    profile: PowerPlantProfileType,
    calculationType: CalculationType,
    dspf: Double,
    p: Double,
    q: Option[Double],
    sR: Double,
    pMin: Double,
    pMax: Double,
    qMin: Double,
    qMax: Double,
    subnet: String,
    voltLvl: Int
) extends ShuntModel

case object PowerPlant extends EntityModelCompanionObject[PowerPlant] {
  private val NODE = "node"
  private val PLANT_TYPE = "type"
  private val PROFILE_TYPE = "profile"
  private val CALC_TYPE = "calc_type"
  private val DSPF = "dspf"
  private val P = "pPP"
  private val Q = "qPP"
  private val S_RATED = "sR"
  private val P_MIN = "pMin"
  private val P_MAX = "pMax"
  private val Q_MIN = "qMin"
  private val Q_MAX = "qMax"

  /**
    * Get an Array of table fields denoting the mapping to the model's attributes
    *
    * @return Array of table headings
    */
  override def getFields: Array[HeadLineField] =
    Array(
      ID,
      NODE,
      PLANT_TYPE,
      PROFILE_TYPE,
      CALC_TYPE,
      DSPF,
      P,
      Q,
      S_RATED,
      P_MIN,
      P_MAX,
      Q_MIN,
      Q_MAX,
      SUBNET,
      VOLT_LVL
    ).map(id => MandatoryField(id))

  /**
    * Factory method to build one model from a mapping from field id to value
    *
    * @param rawData mapping from field id to value
    * @return A model
    */
  override def apply(rawData: RawModelData): PowerPlant =
    throw SimbenchDataModelException(
      s"No basic implementation of model creation available for ${this.getClass.getSimpleName}"
    )

  /**
    * Factory method to build a batch of models from a mapping from field id to value
    *
    * @param rawData  mapping from field id to value
    * @param nodes    A mapping from node id to node itself
    * @return A [[Vector]] of models
    */
  def buildModels(
      rawData: Vector[RawModelData],
      nodes: Map[String, Node]
  ): Vector[PowerPlant] =
    for (entry <- rawData) yield {
      val node = getNode(entry.get(NODE), nodes)
      buildModel(entry, node)
    }

  /**
    * Factory method to build one model from a mapping from field id to value
    *
    * @param rawData  mapping from field id to value
    * @param node     Node at which the power plant is connected
    * @return A model
    */
  def buildModel(rawData: RawModelData, node: Node): PowerPlant = {
    val (id, subnet, voltLvl) = getBaseInformation(rawData)
    val plantType = PowerPlantType(rawData.get(PLANT_TYPE))
    val profileType = PowerPlantProfileType(rawData.get(PROFILE_TYPE))
    val calcType = CalculationType(rawData.get(CALC_TYPE))
    val dspf = rawData.getDouble(DSPF)
    val p = rawData.getDouble(P)
    val q = rawData.getDoubleOption(Q)
    val sRated = rawData.getDouble(S_RATED)
    val pMin = rawData.getDouble(P_MIN)
    val pMax = rawData.getDouble(P_MAX)
    val qMin = rawData.getDouble(Q_MIN)
    val qMax = rawData.getDouble(Q_MAX)

    PowerPlant(
      id,
      node,
      plantType,
      profileType,
      calcType,
      dspf,
      p,
      q,
      sRated,
      pMin,
      pMax,
      qMin,
      qMax,
      subnet,
      voltLvl
    )
  }
}
