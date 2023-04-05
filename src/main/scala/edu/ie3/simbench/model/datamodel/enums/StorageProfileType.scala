package edu.ie3.simbench.model.datamodel.enums

import edu.ie3.simbench.exception.io.SimbenchDataModelException

sealed trait StorageProfileType

object StorageProfileType {

  case object Dummy extends StorageProfileType

  /** Hands back a suitable [[StorageProfileType]] based on the entries given in
    * SimBench csv files
    *
    * @param typeString
    *   Entry in csv file
    * @return
    *   The matching [[StorageProfileType]]
    * @throws SimbenchDataModelException
    *   if a non valid type string has been provided
    */
  @throws[SimbenchDataModelException]
  def apply(typeString: String): StorageProfileType =
    typeString.toLowerCase.replaceAll("[_ ]*", "") match {
      case whatever =>
        throw SimbenchDataModelException(
          s"I cannot handle the storage type $whatever"
        )
    }
}
