package edu.ie3.simbench.model

import edu.ie3.simbench.exception.io.SimbenchDataModelException

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
      throw SimbenchDataModelException(
        s"Cannot build $modelClass, as field $field is missing"))
}
