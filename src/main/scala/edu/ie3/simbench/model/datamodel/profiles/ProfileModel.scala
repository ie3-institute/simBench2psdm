package edu.ie3.simbench.model.datamodel.profiles

import java.time.ZonedDateTime

import edu.ie3.simbench.model.datamodel.SimbenchModel

/**
  * Super class of a profile model
  *
  * @tparam T Type parameter to describe the type of the profile
  */
trait ProfileModel[T <: ProfileType] extends SimbenchModel {

  /**
    * The type of the profile
    */
  val profileType: T

  /**
    * The actual profile
    */
  val profile: Map[ZonedDateTime, (BigDecimal, BigDecimal)]
}
