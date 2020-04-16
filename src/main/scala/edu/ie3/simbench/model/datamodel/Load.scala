package edu.ie3.simbench.model.datamodel

import edu.ie3.simbench.exception.io.SimbenchDataModelException
import edu.ie3.simbench.io.HeadLineField
import edu.ie3.simbench.io.HeadLineField.MandatoryField
import edu.ie3.simbench.model.RawModelData
import edu.ie3.simbench.model.datamodel.EntityModel.EntityModelCompanionObject
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
case class Load(
    id: String,
    node: Node,
    profile: LoadProfileType,
    pLoad: BigDecimal,
    qLoad: BigDecimal,
    sR: BigDecimal,
    subnet: String,
    voltLvl: Int
) extends ShuntModel

case object Load extends EntityModelCompanionObject[Load] {
  private val NODE = "node"
  private val PROFILE = "profile"
  private val P_LOAD = "pLoad"
  private val Q_LOAD = "qLoad"
  private val S_RATED = "sR"

  /**
    * Get an Array of table fields denoting the mapping to the model's attributes
    *
    * @return Array of table headings
    */
  override def getFields: Array[HeadLineField] =
    Array(ID, NODE, PROFILE, P_LOAD, Q_LOAD, S_RATED, SUBNET, VOLT_LVL).map(
      id => MandatoryField(id)
    )

  /**
    * Factory method to build one model from a mapping from field id to value
    *
    * @param rawData mapping from field id to value
    * @return A model
    */
  override def apply(rawData: RawModelData): Load =
    throw SimbenchDataModelException(
      s"No basic implementation of model creation available for ${this.getClass.getSimpleName}"
    )

  /**
    * Factory method to build a batch of models from a mapping from field id to value
    *
    * @param rawData mapping from field id to value
    * @return A [[Vector]] of models
    */
  def buildModels(
      rawData: Vector[RawModelData],
      nodes: Map[String, Node]
  ): Vector[Load] =
    for (entry <- rawData) yield {
      val node = getNode(entry.get(NODE), nodes)
      buildModel(entry, node)
    }

  /**
    * Factory method to build one model from a mapping from field id to value
    *
    * @param rawData  mapping from field id to value
    * @param node     Node at which the load is connected
    * @return A load model
    */
  def buildModel(rawData: RawModelData, node: Node): Load = {
    val (id, subnet, voltLvl) = getBaseInformation(rawData)
    val profileType = LoadProfileType(rawData.get(PROFILE))
    val p = rawData.getBigDecimal(P_LOAD)
    val q = rawData.getBigDecimal(Q_LOAD)
    val sRated = rawData.getBigDecimal(S_RATED)
    Load(id, node, profileType, p, q, sRated, subnet, voltLvl)
  }
}
