package edu.ie3.simbench.model.datamodel

import java.time.ZoneId
import java.util.Locale

import edu.ie3.simbench.io.HeadLineField
import edu.ie3.simbench.model.RawModelData
import edu.ie3.util.TimeUtil

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
    * Outline of a companion object for all Simbench companion objects
    */
  abstract class SimbenchCompanionObject[C <: SimbenchModel] {

    /**
      * Table field for id
      */
    protected val ID: String = "id"

    protected val simbenchTimeUtil =
      new TimeUtil(ZoneId.of("UTC"), Locale.GERMANY, "dd.MM.yyyy HH:mm")

    /**
      * Get an Array of table fields denoting the mapping to the model's attributes
      *
      * @return Array of table headings
      */
    def getFields: Array[HeadLineField]

    /**
      * Factory method to build a batch of models from a mapping from field id to value
      *
      * @param rawData  mapping from field id to value
      * @return A [[Vector]] of models
      */
    def buildModels(rawData: Vector[RawModelData]): Vector[C] =
      for (entry <- rawData) yield {
        apply(entry)
      }

    /**
      * Factory method to build one model from a mapping from field id to value
      *
      * @param rawData  mapping from field id to value
      * @return A model
      */
    def apply(rawData: RawModelData): C
  }
}
