package edu.ie3.simbench.model.datamodel

import edu.ie3.simbench.exception.io.SimbenchDataModelException
import edu.ie3.simbench.io.HeadLineField
import edu.ie3.simbench.io.HeadLineField.MandatoryField
import edu.ie3.simbench.model.RawModelData
import edu.ie3.simbench.model.datamodel.SimbenchModel.SimbenchCompanionObject
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
                      q: Option[BigDecimal],
                      sR: BigDecimal,
                      pMin: BigDecimal,
                      pMax: BigDecimal,
                      qMin: BigDecimal,
                      qMax: BigDecimal,
                      subnet: String,
                      voltLvl: Int)
    extends ShuntModel

case object PowerPlant extends SimbenchCompanionObject[PowerPlant] {
  val NODE = "node"
  val PLANT_TYPE = "type"
  val PROFILE_TYPE = "profile"
  val CALC_TYPE = "calc_type"
  val DSPF = "dspf"
  val P = "pPP"
  val Q = "qPP"
  val S_RATED = "sR"
  val P_MIN = "pMin"
  val P_MAX = "pMax"
  val Q_MIN = "qMin"
  val Q_MAX = "qMax"

  /**
    * Get an Array of table fields denoting the mapping to the model's attributes
    *
    * @return Array of table headings
    */
  override def getFields: Array[HeadLineField] =
    Array(SimbenchModel.ID,
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
          EntityModel.SUBNET,
          EntityModel.VOLT_LVL).map(id => MandatoryField(id))

  /**
    * Factory method to build one model from a mapping from field id to value
    *
    * @param rawData mapping from field id to value
    * @return A model
    */
  override def apply(rawData: RawModelData): PowerPlant =
    throw SimbenchDataModelException(
      s"No basic implementation of model creation available for ${this.getClass.getSimpleName}")

  /**
    * Factory method to build a batch of models from a mapping from field id to value
    *
    * @param rawData  mapping from field id to value
    * @param nodes    A mapping from node id to node itself
    * @return A [[Vector]] of models
    */
  def buildModels(rawData: Vector[RawModelData],
                  nodes: Map[String, Node]): Vector[PowerPlant] =
    for (entry <- rawData) yield {
      val node = EntityModel.getNode(entry.get(NODE), nodes)
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
    val (id, subnet, voltLvl) = EntityModel.getBaseInformation(rawData)
    val plantType = PowerPlantType(rawData.get(PLANT_TYPE))
    val profileType = PowerPlantProfileType(rawData.get(PROFILE_TYPE))
    val calcType = CalculationType(rawData.get(CALC_TYPE))
    val dspf = rawData.getBigDecimal(DSPF)
    val p = rawData.getBigDecimal(P)
    val q = rawData.getBigDecimalOption(Q)
    val sRated = rawData.getBigDecimal(S_RATED)
    val pMin = rawData.getBigDecimal(P_MIN)
    val pMax = rawData.getBigDecimal(P_MAX)
    val qMin = rawData.getBigDecimal(Q_MIN)
    val qMax = rawData.getBigDecimal(Q_MAX)

    PowerPlant(id,
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
               voltLvl)
  }
}
