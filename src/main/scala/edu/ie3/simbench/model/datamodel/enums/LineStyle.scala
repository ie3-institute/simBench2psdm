package edu.ie3.simbench.model.datamodel.enums

import edu.ie3.simbench.exception.io.SimbenchDataModelException

/** Different building shapes of a line
  */
sealed trait LineStyle

object LineStyle {
  object Cable extends LineStyle

  object OverheadLine extends LineStyle

  /** Hands back a suitable [[LineStyle]] based on the entries given in SimBench
    * csv files
    *
    * @param typeString
    *   Entry in csv file
    * @return
    *   The matching [[LineStyle]]
    * @throws SimbenchDataModelException
    *   if a non valid type string has been provided
    */
  @throws[SimbenchDataModelException]
  def apply(typeString: String): LineStyle =
    typeString.toLowerCase.replaceAll("[_]*", "") match {
      case "cable" => Cable
      case "ohl"   => OverheadLine
      case whatever =>
        throw SimbenchDataModelException(
          s"I cannot handle the line style $whatever"
        )
    }
}
