package edu.ie3.simbench.model.datamodel

import edu.ie3.simbench.exception.io.SimbenchDataModelException
import edu.ie3.simbench.model.RawModelData
import edu.ie3.simbench.model.datamodel.SimbenchModel.SimbenchCompanionObject
import edu.ie3.simbench.model.datamodel.enums.{CalculationType, ResType}
import edu.ie3.simbench.model.datamodel.profiles.{ResProfile, ResProfileType}

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
               profile: ResProfileType,
               calculationType: CalculationType,
               p: BigDecimal,
               q: BigDecimal,
               sR: BigDecimal,
               subnet: String,
               voltLvl: Int)
    extends ShuntModel

case object RES extends SimbenchCompanionObject[RES] {
  val NODE = "node"
  val TYPE = "type"
  val PROFILE = "profile"
  val CALC_TYPE = "calc_type"
  val P = "pRES"
  val Q = "qRES"
  val S_RATED = "sR"

  /**
    * Get an Array of table fields denoting the mapping to the model's attributes
    *
    * @return Array of table headings
    */
  override def getFields: Array[String] =
    Array(SimbenchModel.ID,
          NODE,
          TYPE,
          PROFILE,
          CALC_TYPE,
          P,
          Q,
          S_RATED,
          EntityModel.SUBNET,
          EntityModel.VOLT_LVL)

  /**
    * Factory method to build one model from a mapping from field id to value
    *
    * @param rawData mapping from field id to value
    * @return A model
    */
  override def buildModel(rawData: RawModelData): RES =
    throw SimbenchDataModelException(
      s"No basic implementation of model creation available for ${this.getClass.getSimpleName}")

  /**
    * Factory method to build a batch of models from a mapping from field id to value
    *
    * @param rawData  mapping from field id to value
    * @param nodes    Mapping from node id to node itself
    * @return A [[Vector]] of models
    */
  def buildModels(rawData: Vector[RawModelData],
                  nodes: Map[String, Node]): Vector[RES] =
    for (entry <- rawData) yield {
      val node = EntityModel.getNode(entry.get(NODE), nodes)
      buildModel(entry, node)
    }

  /**
    * Factory method to build one model from a mapping from field id to value
    *
    * @param rawData  mapping from field id to value
    * @param node     Node, at which the res is installed
    * @return A model
    */
  def buildModel(rawData: RawModelData, node: Node): RES = {
    val (id, subnet, voltLvl) = EntityModel.getBaseInformation(rawData)
    val resType = ResType(rawData.get(TYPE))
    val profile = ResProfileType(rawData.get(PROFILE))
    val calcType = CalculationType(rawData.get(CALC_TYPE))
    val p = BigDecimal(rawData.get(P))
    val q = BigDecimal(rawData.get(Q))
    val sRated = BigDecimal(rawData.get(S_RATED))

    RES(id, node, resType, profile, calcType, p, q, sRated, subnet, voltLvl)
  }
}
