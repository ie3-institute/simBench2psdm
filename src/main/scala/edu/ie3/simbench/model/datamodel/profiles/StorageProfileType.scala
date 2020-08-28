/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.simbench.model.datamodel.profiles

/**
  * Trait to denote the different available storage profiles. But currently no data is known for this, therefore it's an
  * empty trait, yet.
  */
sealed trait StorageProfileType extends ProfileType
case object StorageProfileType {
  case object DummyStorageProfileType extends StorageProfileType
}
