package edu.ie3.simbench.model.datamodel.profiles
import java.time.LocalDateTime

/**
  * Profile to be applied to storages
  * @param profileType The type if profile
  * @param profile The actual profile
  */
case class StorageProfile(profileType: StorageProfile,
                          profile: Map[LocalDateTime, (BigDecimal, BigDecimal)])
    extends ProfileModel[StorageProfile]
