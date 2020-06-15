package edu.ie3.simbench.model.datamodel.enums

import edu.ie3.simbench.exception.io.SimbenchDataModelException

/**
  * Denoting the ports of a branch element
  */
sealed trait BranchElementPort

object BranchElementPort {
  case object HV extends BranchElementPort
  case object MV extends BranchElementPort
  case object LV extends BranchElementPort

  /**
    * Hands back a suitable [[BranchElementPort]] based on the entries given in SimBench csv files
    *
    * @param typeString Entry in csv file
    * @return The matching [[BranchElementPort]]
    * @throws SimbenchDataModelException if a non valid type string has been provided
    */
  @throws[SimbenchDataModelException]
  def apply(typeString: String): BranchElementPort =
    typeString.toLowerCase.replaceAll("[_ ]*", "") match {
      case "hv" => HV
      case "mv" => MV
      case "lv" => LV
      case whatever =>
        throw SimbenchDataModelException(
          s"I cannot handle the node type $whatever"
        )
    }
}
