package edu.ie3.simbench.model.datamodel.profiles

import java.time.ZonedDateTime

/**
  * A renewable energy source's profile consisting of an identifier and a mapping of the date to
  * (p,q) pair
  *
  * @param id           Identifier of the profile
  * @param profileType  The type of the profile
  * @param profile      The actual profile as scaling factor in p.u.
  */
case class ResProfile(id: String,
                      profileType: ResProfileType,
                      profile: Map[ZonedDateTime, (BigDecimal, BigDecimal)])
    extends ProfileModel[ResProfileType, (BigDecimal, BigDecimal)]
