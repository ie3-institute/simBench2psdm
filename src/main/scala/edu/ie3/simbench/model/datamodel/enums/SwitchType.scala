package edu.ie3.simbench.model.datamodel.enums

import edu.ie3.simbench.exception.io.SimbenchDataModelException

/**
  * Available switch types to SimBench
  */
sealed trait SwitchType

case object SwitchType {
  case object CircuitBreaker extends SwitchType

  case object LoadBreakingSwitch extends SwitchType

  case object LoadSwitch extends SwitchType

  /**
    * Hands back a suitable [[SwitchType]] based on the entries given in SimBench csv files
    *
    * @param typeString Entry in csv file
    * @return The matching [[SwitchType]]
    * @throws SimbenchDataModelException if a non valid type string has been provided
    */
  @throws[SimbenchDataModelException]
  def apply(typeString: String): SwitchType =
    typeString.toLowerCase.replaceAll("[_ ]*", "") match {
      case "cb"  => CircuitBreaker
      case "lbs" => LoadBreakingSwitch
      case "ls"  => LoadSwitch
      case whatever =>
        throw SimbenchDataModelException(
          s"I cannot handle the switch type $whatever"
        )
    }
}
