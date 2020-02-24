package edu.ie3.simbench.model.datamodel.profiles

import java.time.ZonedDateTime

import edu.ie3.simbench.exception.io.SimbenchDataModelException
import edu.ie3.simbench.model.RawModelData
import edu.ie3.simbench.model.datamodel.SimbenchModel
import edu.ie3.simbench.model.datamodel.SimbenchModel.SimbenchCompanionObject

/**
  * Super class of a profile model
  *
  * @tparam T Type parameter to describe the type of the profile
  * @tparam D Type parameter of the profile's data
  */
trait ProfileModel[T <: ProfileType, D] extends SimbenchModel {

  /**
    * The type of the profile
    */
  val profileType: T

  /**
    * The actual profile
    */
  val profile: Map[ZonedDateTime, D]
}

object ProfileModel {
  val TIME = "time"

  abstract class ProfileCompanionObject[
      C <: ProfileModel[_ <: ProfileType, D], D]
      extends SimbenchCompanionObject[C] {

    /**
      * Factory method to build one model from a mapping from field id to value
      *
      * @param rawData mapping from field id to value
      * @return A model
      */
    override def buildModel(rawData: RawModelData): C =
      throw SimbenchDataModelException(
        s"No basic implementation of model creation available for profiles")

    /**
      * Determine the ids of the available profile types by filtering the head line fields
      *
      * @param values                 [[Vector]] of [[RawModelData]]
      * @param idManipulationFuncOpt  Option to a partial function to manipulate ids
      * @return                       A [[Vector]] available field ids
      */
    def determineAvailableProfileIds(
        values: Vector[RawModelData],
        idManipulationFuncOpt: Option[String => String]): Vector[String] = {
      val availableTypes = determineAvailableProfileTypes(
        values
          .find(_ => true)
          .getOrElse(throw SimbenchDataModelException(
            "Raw data has no content. Unable to determine available profile types."))
          .fieldToValues,
        idManipulationFuncOpt
      )
      availableTypes.distinct
    }

    /**
      * Determine the available profile types from the given raw data
      *
      * @param values                 The map of field id to string content
      * @param idManipulationFuncOpt  Option to a partial function to manipulate ids
      * @return                       A [[Vector]] of available field ids
      */
    def determineAvailableProfileTypes(
        values: Map[String, String],
        idManipulationFuncOpt: Option[String => String]): Vector[String] =
      (for (entry <- values.filterNot(entry => entry._1 == TIME)) yield {
        idManipulationFuncOpt match {
          case Some(value) => value(entry._1)
          case None        => entry._1
        }
      }).toVector.distinct
  }
}
