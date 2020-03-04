package edu.ie3.simbench.model

import edu.ie3.simbench.exception.io.IoException

final case class RawModelData(modelClass: Class[_],
                              fieldToValues: Map[String, String]) {

  /**
    * Extract the id from the field values
    *
    * @param field        Field to extract
    * @return The actual information
    */
  def get(field: String): String =
    fieldToValues.getOrElse(
      field,
      throw IoException(
        s"Cannot build $modelClass, as field $field is missing"))

  /**
    * Get the field and compare it to common String representations of Boolean
    *
    * @param field Id of the field to get
    * @return true or false
    */
  def getBoolean(field: String): Boolean =
    get(field) == "1" || get(field).toLowerCase == "true"

  /**
    * Getting BigDecimal entry from field
    *
    * @param field  Field to extract
    * @return       Actual information as BigDecimal
    */
  def getBigDecimal(field: String): BigDecimal = {
    val entry = get(field)
    try {
      BigDecimal(entry)
    } catch {
      case nfe: NumberFormatException =>
        throw IoException(
          s"Cannot build BigDecimal from $field, as the underlying entry $entry cannot be converted to BigDecimal.",
          nfe)
    }
  }

  /**
    * Get an option to the entry and convert it to BigDecimal if possible
    *
    * @param field  Field to extract
    * @return       Option to the actual information
    */
  def getBigDecimalOption(field: String): Option[BigDecimal] =
    get(field) match {
      case "NULL" => None
      case ""     => None
      case _      => Some(getBigDecimal(field))
    }
}
