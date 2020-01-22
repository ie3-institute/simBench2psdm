package edu.ie3.simbench.model.datamodel.profiles

import java.time.LocalDateTime

/**
  * A load profile consisting of an identifier and a mapping of the date to (p,q) pair
  *
  * @param profileType The type of the profile
  * @param profile The actual profile as scaling factor in p.u.
  */
case class LoadProfile(profileType: LoadProfileType,
                       profile: Map[LocalDateTime, (BigDecimal, BigDecimal)])
    extends ProfileModel[LoadProfileType]
