/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.simbench.model.datamodel.profiles
import java.time.ZonedDateTime

/**
  * Profile to be applied to storages
  *
  * @param id           Identifier of the profile
  * @param profileType  The type if profile
  * @param profile      The actual profile
  */
final case class StorageProfile(
    id: String,
    profileType: StorageProfileType,
    profile: Map[ZonedDateTime, (Double, Double)]
) extends ProfileModel[StorageProfileType, (Double, Double)]
