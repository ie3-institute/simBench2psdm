package edu.ie3.simbench.model.datamodel

import edu.ie3.simbench.model.RawModelData

/**
  * Common fields, every SimBench model has
  */
trait SimbenchModel {

  /**
    * Identifier
    */
  val id: String
}

object SimbenchModel {

  /**
    * Table field for id
    */
  val ID: String = "id"

  /**
    * Outline of a companion object for all Simbench companion objects
    */
  abstract class SimbenchCompanionObject[C <: SimbenchModel] {

    /**
      * Get an Array of table fields denoting the mapping to the model's attributes
      *
      * @return Array of table headings
      */
    def getFields: Array[String]

    /**
      * Factory method to build a batch of models from a mapping from field id to value
      *
      * @param rawData  mapping from field id to value
      * @return A [[Vector]] of models
      */
    def buildModels(rawData: Vector[RawModelData]): Vector[C] =
      for (map <- rawData) yield {
        buildModel(map)
      }

    /**
      * Factory method to build one model from a mapping from field id to value
      *
      * @param rawData  mapping from field id to value
      * @return A model
      */
    def buildModel(rawData: RawModelData): C
  }
}
