package edu.ie3.simbench.model.datamodel

import edu.ie3.simbench.io.HeadLineField
import edu.ie3.simbench.model.RawModelData
import edu.ie3.util.TimeUtil

import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/** Common fields, every SimBench model has
  */
trait SimbenchModel {

  /** Identifier
    */
  val id: String
}

object SimbenchModel {

  /** Outline of a companion object for all Simbench companion objects
    */
  abstract class SimbenchCompanionObject[C <: SimbenchModel] {

    /** Table field for id
      */
    protected val ID: String = "id"

    protected val simbenchTimeUtil: TimeUtil = new TimeUtil(
      DateTimeFormatter
        .ofPattern("dd.MM.yyyy HH:mm")
        .withZone(ZoneId.of("UTC"))
        .withLocale(Locale.GERMANY)
    )

    /** Get an Array of table fields denoting the mapping to the model's
      * attributes
      *
      * @return
      *   Array of table headings
      */
    def getFields: Array[HeadLineField]

    /** Factory method to build a batch of models from a mapping from field id
      * to value
      *
      * @param rawData
      *   mapping from field id to value
      * @return
      *   A [[Vector]] of models
      */
    def buildModels(rawData: Vector[RawModelData]): Vector[C] =
      for (entry <- rawData) yield {
        apply(entry)
      }

    /** Factory method to build one model from a mapping from field id to value
      *
      * @param rawData
      *   mapping from field id to value
      * @return
      *   A model
      */
    def apply(rawData: RawModelData): C
  }
}
