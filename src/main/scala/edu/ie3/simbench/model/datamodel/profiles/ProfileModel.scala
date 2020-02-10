package edu.ie3.simbench.model.datamodel.profiles

import java.time.LocalDateTime

/**
  * Super class of a profile model
  *
  * @tparam T Type parameter to describe the type of the profile
  */
trait ProfileModel[T] {

  /**
    * The type of the profile
    */
  val profileType: T

  /**
    * The actual profile
    */
  val profile: Map[LocalDateTime, (BigDecimal, BigDecimal)]
}
