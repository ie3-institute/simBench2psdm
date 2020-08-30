/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.simbench.model

import edu.ie3.simbench.exception.io.IoException

import scala.util.{Failure, Success, Try}

final case class RawModelData(
    modelClass: Class[_],
    fieldToValues: Map[String, String]
) {

  /**
    * Extract the id from the field values
    *
    * @param field        Field to extract
    * @return The actual information
    */
  def get(field: String): String =
    fieldToValues.getOrElse(
      field,
      throw IoException(s"Cannot build $modelClass, as field $field is missing")
    )

  /**
    * Getting Int entry from field
    *
    * @param field  Field to extract
    * @return       Actual information as Int
    */
  def getInt(field: String): Int = {
    val entry = get(field)
    Try(entry.toInt) match {
      case Success(value) => value
      case Failure(exception) =>
        throw IoException(
          s"Cannot build Int from $field, as the underlying entry $entry cannot be converted to Int.",
          exception
        )
    }
  }

  /**
    * Get the field and compare it to common String representations of Boolean
    *
    * @param field Id of the field to get
    * @return true or false
    */
  def getBoolean(field: String): Boolean =
    get(field) == "1" || get(field).toLowerCase == "true"

  /**
    * Getting Double entry from field
    *
    * @param field  Field to extract
    * @return       Actual information as Double
    */
  def getDouble(field: String): Double = {
    val entry = get(field)
    Try(entry.toDouble) match {
      case Success(value) => value
      case Failure(exception) =>
        throw IoException(
          s"Cannot build Double from $field, as the underlying entry $entry cannot be converted to Double.",
          exception
        )
    }
  }

  /**
    * Get an option to the entry and convert it to Double if possible
    *
    * @param field  Field to extract
    * @return       Option to the actual information
    */
  def getDoubleOption(field: String): Option[Double] =
    get(field) match {
      case "NULL" => None
      case ""     => None
      case _      => Some(getDouble(field))
    }
}
