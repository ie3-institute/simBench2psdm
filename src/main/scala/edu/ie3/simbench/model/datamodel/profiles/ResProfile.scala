package edu.ie3.simbench.model.datamodel.profiles

import java.time.LocalDateTime

/**
  * A renewable energy source's profile consisting of an identifier and a mapping of the date to
  * (p,q) pair
  *
  * @param profileType The type of the profile
  * @param profile The actual profile as scaling factor in p.u.
  */
case class ResProfile(profileType: ResProfileType,
                      profile: Map[LocalDateTime, (BigDecimal, BigDecimal)])
    extends ProfileModel[ResProfileType]
