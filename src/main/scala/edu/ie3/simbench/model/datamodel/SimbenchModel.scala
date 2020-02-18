package edu.ie3.simbench.model.datamodel

import edu.ie3.simbench.exception.io.SimbenchDataModelException

import scala.reflect.ClassTag

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
      * @param fieldToValueMaps  mapping from field id to value
      * @return A [[Vector]] of models
      */
    def buildModels(fieldToValueMaps: Vector[Map[String, String]]): Vector[C] =
      for (map <- fieldToValueMaps) yield {
        buildModel(map)
      }

    /**
      * Factory method to build one model from a mapping from field id to value
      *
      * @param fieldToValueMap  mapping from field id to value
      * @return A model
      */
    def buildModel(fieldToValueMap: Map[String, String]): C

    /**
      * Check, if all needed fields are covered by the provided field values
      *
      * @param fieldValues provided field values
      */
    protected def checkFields(fieldValues: Map[String, String]): Unit = {
      if (getFields.toSet.diff(fieldValues.keySet).nonEmpty) {
        throw SimbenchDataModelException(
          s"Cannot instantiate a model of ${this.getClass.getSimpleName}, as the provided field values are not sufficient.")
      }
    }

    /**
      * Extract the id from the field values
      *
      * @param fieldValues Field values to get information from
      * @return The information
      */
    protected def extractId(fieldValues: Map[String, String]): String = {
      fieldValues.getOrElse(ID,
                            throw getFieldNotFoundException(ID)(
                              ClassTag.apply(classOf[SimbenchModel])))
    }

    /**
      * Create an exception, when a certain field cannot be found
      *
      * @param fieldName Name of the field, that cannot be found
      * @return [[SimbenchDataModelException]] with the relevant error message
      */
    def getFieldNotFoundException(fieldName: String)(
        implicit tag: ClassTag[C]): SimbenchDataModelException = {
      SimbenchDataModelException(
        s"Cannot build $tag, as field $fieldName is missing")
    }
  }
}
