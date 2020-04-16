package edu.ie3.simbench.model.datamodel.enums

import edu.ie3.simbench.exception.io.SimbenchDataModelException

/**
  * Information on what entities are measured with a given measurement device
  */
sealed trait MeasurementVariable

object MeasurementVariable {
  case object Voltage extends MeasurementVariable
  case object ActivePower extends MeasurementVariable
  case object ReactivePower extends MeasurementVariable
  case object Current extends MeasurementVariable

  /**
    * Hands back a suitable [[MeasurementVariable]] based on the entries given in SimBench csv files
    *
    * @param typeString Entry in csv file
    * @return The matching [[MeasurementVariable]]
    * @throws SimbenchDataModelException if a non valid type string has been provided
    */
  @throws[SimbenchDataModelException]
  def apply(typeString: String): MeasurementVariable =
    typeString.toLowerCase.replaceAll("[_]*", "") match {
      case "v" => Voltage
      case "p" => ActivePower
      case "q" => ReactivePower
      case "i" => Current
      case whatever =>
        throw SimbenchDataModelException(
          s"I cannot handle the measurement variable $whatever"
        )
    }
}
