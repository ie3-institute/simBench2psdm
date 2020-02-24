package edu.ie3.simbench.model.datamodel.profiles
import java.time.ZonedDateTime

/**
  * Profile to be applied to storages
  *
  * @param id           Identifier of the profile
  * @param profileType  The type if profile
  * @param profile      The actual profile
  */
case class StorageProfile(id: String,
                          profileType: StorageProfileType,
                          profile: Map[ZonedDateTime, (BigDecimal, BigDecimal)])
    extends ProfileModel[StorageProfileType, (BigDecimal, BigDecimal)]
