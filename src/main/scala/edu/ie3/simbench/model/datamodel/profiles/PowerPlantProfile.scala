package edu.ie3.simbench.model.datamodel.profiles

import java.time.LocalDateTime

/**
  * A power plant's profile consisting of an identifier and a mapping of the date to (p,q) pair
  *
  * @param profileType The type of the profile
  * @param profile The actual profile as scaling factor in p.u.
  */
case class PowerPlantProfile(
    profileType: PowerPlantProfileType,
    profile: Map[LocalDateTime, (BigDecimal, BigDecimal)])
    extends ProfileModel[PowerPlantProfileType]
