package edu.ie3.simbench.model.datamodel.enums

import edu.ie3.simbench.exception.io.SimbenchDataModelException
import edu.ie3.simbench.util.ParsableEnumeration

import scala.util.{Failure, Success, Try}

sealed trait StorageType

object StorageType extends ParsableEnumeration {
  val WIND_OFFSHORE: Value = Value("Wind_Offshore_Storage")
  val STORAGE: Value = Value("Storage")
  val PV: Value = Value("PV_Storage")
  val WIND: Value = Value("Wind_Storage")
  val PV_MV: Value = Value("PV_Storage_MV")

  case object Dummy extends StorageType

  /**
    * Hands back a suitable [[StorageType]] based on the entries given in SimBench csv files
    *
    * @param typeString Entry in csv file
    * @return The matching [[StorageType]]
    */
  def apply(typeString: String): Try[StorageType.Value] =
    parse(typeString) match {
      case Some(value) => Success(value)
      case None =>
        Failure(
          SimbenchDataModelException(
            s"There is no defined storage for identifier type '$typeString'"
          )
        )
    }
}
