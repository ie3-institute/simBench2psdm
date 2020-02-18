package edu.ie3.simbench.model.datamodel

import edu.ie3.simbench.model.RawModelData
import edu.ie3.simbench.model.datamodel.SimbenchModel.SimbenchCompanionObject
import edu.ie3.simbench.model.datamodel.enums.{CalculationType, ResType}
import edu.ie3.simbench.model.datamodel.profiles.ResProfile

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
               profile: ResProfile,
               calculationType: CalculationType,
               p: BigDecimal,
               q: BigDecimal,
               sR: BigDecimal,
               subnet: String,
               voltLvl: Int)
    extends ShuntModel

case object RES extends SimbenchCompanionObject[RES] {

  /**
    * Get an Array of table fields denoting the mapping to the model's attributes
    *
    * @return Array of table headings
    */
  override def getFields: Array[String] =
    Array("id",
          "node",
          "type",
          "profile",
          "calc_type",
          "pRES",
          "qRES",
          "sR",
          "subnet",
          "voltLvl")

  /**
    * Factory method to build one model from a mapping from field id to value
    *
    * @param rawData mapping from field id to value
    * @return A model
    */
  override def buildModel(rawData: RawModelData): RES = ???
}
