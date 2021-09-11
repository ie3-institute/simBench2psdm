package edu.ie3.simbench.model.datamodel

import edu.ie3.simbench.exception.io.SimbenchDataModelException
import edu.ie3.simbench.io.HeadLineField
import edu.ie3.simbench.io.HeadLineField.MandatoryField
import edu.ie3.simbench.model.RawModelData
import edu.ie3.simbench.model.datamodel.EntityModel.EntityModelCompanionObject
import edu.ie3.simbench.model.datamodel.enums.StorageType
import edu.ie3.simbench.model.datamodel.profiles.StorageProfileType

import scala.util.{Failure, Success}

case object Storage extends EntityModelCompanionObject[Storage] {
  private val NODE = "node"
  private val TYPE = "type"
  private val PROFILE = "profile"
  private val P = "pStor"
  private val Q = "qStor"
  private val S_RATED = "sR"
  private val CHARGE_LEVEL = "chargeLevel"
  private val E = "eStore"
  private val ETA = "etaStore"
  private val SD = "sdStore"
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
      TYPE,
      PROFILE,
      P,
      Q,
      S_RATED,
      CHARGE_LEVEL,
      E,
      ETA,
      SD,
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
  override def apply(rawData: RawModelData): Storage =
    throw SimbenchDataModelException(
      s"No basic implementation of model creation available for ${this.getClass.getSimpleName}"
    )

  /**
    * Factory method to build a batch of models from a mapping from field id to value
    *
    * @param rawData  mapping from field id to value
    * @param nodes    Mapping from node id to node itself
    * @return A [[Vector]] of models
    */
  def buildModels(
      rawData: Vector[RawModelData],
      nodes: Map[String, Node]
  ): Vector[Storage] =
    for (entry <- rawData) yield {
      val node = getNode(entry.get(NODE), nodes)
      buildModel(entry, node)
    }

  /**
    * Factory method to build one model from a mapping from field id to value
    *
    * @param rawData  mapping from field id to value
    * @param node     Node, at which the res is installed
    * @return A model
    */
  def buildModel(rawData: RawModelData, node: Node): Storage = {
    val (id, subnet, voltLvl) = getBaseInformation(rawData)
    val storageType = StorageType(rawData.get(TYPE)) match {
      case Success(resType) => resType
      case Failure(exception) =>
        throw SimbenchDataModelException(
          s"I cannot handle the RES type ${rawData.get(TYPE)}",
          exception
        )
    }
    val profile = StorageProfileType(rawData.get(PROFILE))
    val p = rawData.getBigDecimal(P)
    val q = rawData.getBigDecimal(Q)
    val soc = rawData.getBigDecimal(CHARGE_LEVEL)
    val eStore = rawData.getBigDecimal(E)
    val etaStore = rawData.getBigDecimal(ETA)
    val selfDischarge = rawData.getBigDecimal(SD)
    val pMin = rawData.getBigDecimal(P_MIN)
    val pMax = rawData.getBigDecimal(P_MAX)
    val qMin = rawData.getBigDecimal(Q_MIN)
    val qMax = rawData.getBigDecimal(Q_MAX)
    val sRated = rawData.getBigDecimal(S_RATED)

    Storage(
      id,
      node,
      storageType,
      profile,
      p,
      q,
      soc,
      sRated,
      eStore,
      etaStore,
      selfDischarge,
      pMin,
      pMax,
      qMin,
      qMax,
      subnet,
      voltLvl
    )
  }
}

/**
  * Model of an electric storage
  *
  * @param id Identifier
  * @param node [[Node]] it is connected to
  * @param storageType Type of storage
  * @param profile The [[StorageProfileType]] to apply
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
    storageType: StorageType.Value,
    profile: StorageProfileType,
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
