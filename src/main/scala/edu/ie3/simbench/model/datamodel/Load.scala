package edu.ie3.simbench.model.datamodel

import edu.ie3.simbench.exception.io.SimbenchDataModelException
import edu.ie3.simbench.model.RawModelData
import edu.ie3.simbench.model.datamodel.SimbenchModel.SimbenchCompanionObject
import edu.ie3.simbench.model.datamodel.profiles.{LoadProfile, LoadProfileType}

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
  val NODE = "node"
  val PROFILE = "profile"
  val P_LOAD = "pLoad"
  val Q_LOAD = "qLoad"
  val S_RATED = "sR"

  /**
    * Get an Array of table fields denoting the mapping to the model's attributes
    *
    * @return Array of table headings
    */
  override def getFields: Array[String] =
    Array(SimbenchModel.ID,
          NODE,
          PROFILE,
          P_LOAD,
          Q_LOAD,
          S_RATED,
          EntityModel.SUBNET,
          EntityModel.VOLT_LVL)

  /**
    * Factory method to build one model from a mapping from field id to value
    *
    * @param rawData mapping from field id to value
    * @return A model
    */
  override def buildModel(rawData: RawModelData): Load =
    throw SimbenchDataModelException(
      s"No basic implementation of model creation available for ${this.getClass.getSimpleName}")

  /**
    * Factory method to build a batch of models from a mapping from field id to value
    *
    * @param rawData mapping from field id to value
    * @return A [[Vector]] of models
    */
  def buildModels(rawData: Vector[RawModelData],
                  nodes: Map[String, Node]): Vector[Load] =
    for (entry <- rawData) yield {
      val node = EntityModel.getNode(entry.get(NODE), nodes)
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
    val (id, subnet, voltLvl) = EntityModel.getBaseInformation(rawData)
    val profileType = LoadProfileType(rawData.get(PROFILE))
    val p = BigDecimal(rawData.get(P_LOAD))
    val q = BigDecimal(rawData.get(Q_LOAD))
    val sRated = BigDecimal(rawData.get(S_RATED))
    Load(id, node, profileType, p, q, sRated, subnet, voltLvl)
  }
}
