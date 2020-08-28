/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.simbench.model.datamodel.enums

import edu.ie3.simbench.exception.io.SimbenchDataModelException

sealed trait StorageType

object StorageType {

  case object Dummy extends StorageType

  /**
    * Hands back a suitable [[StorageType]] based on the entries given in SimBench csv files
    *
    * @param typeString Entry in csv file
    * @return The matching [[StorageType]]
    * @throws SimbenchDataModelException if a non valid type string has been provided
    */
  @throws[SimbenchDataModelException]
  def apply(typeString: String): StorageType =
    typeString.toLowerCase.replaceAll("[_ ]*", "") match {
      case whatever =>
        throw SimbenchDataModelException(
          s"I cannot handle the storage type $whatever"
        )
    }
}
