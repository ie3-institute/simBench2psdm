package edu.ie3.simbench.io

/** Trait to denote different kinds of head line fields to read from csv
  */
sealed trait HeadLineField {
  val id: String
}

object HeadLineField {

  /** This field is mandatory and the headline of the file has to contain that
    * field
    *
    * @param id
    *   Identifier of the field
    */
  final case class MandatoryField(id: String) extends HeadLineField

  /** This field may be apparent
    *
    * @param id
    *   Identifier of the field
    */
  final case class OptionalField(id: String) extends HeadLineField
}
