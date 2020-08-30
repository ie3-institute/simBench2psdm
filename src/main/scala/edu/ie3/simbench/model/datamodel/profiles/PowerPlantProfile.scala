/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.simbench.model.datamodel.profiles

import java.time.ZonedDateTime

import edu.ie3.simbench.io.HeadLineField
import edu.ie3.simbench.io.HeadLineField.{MandatoryField, OptionalField}
import edu.ie3.simbench.model.RawModelData
import edu.ie3.simbench.model.datamodel.profiles.ProfileModel.ProfileCompanionObject

/**
  * A power plant's profile consisting of an identifier and a mapping of the date to (p,q) pair
  *
  * @param id           Identifier of the profile
  * @param profileType  The type of the profile
  * @param profile      The actual profile as scaling factor in p.u.
  */
final case class PowerPlantProfile(
    id: String,
    profileType: PowerPlantProfileType,
    profile: Map[ZonedDateTime, Double]
) extends ProfileModel[PowerPlantProfileType, Double]

case object PowerPlantProfile
    extends ProfileCompanionObject[PowerPlantProfile, Double] {

  /**
    * Get an Array of table fields denoting the mapping to the model's attributes
    *
    * @return Array of table headings
    */
  override def getFields: Array[HeadLineField] =
    Array(MandatoryField("time")) ++ (1 to 318).map(
      cnt => OptionalField("pp_" + cnt)
    ) ++ Array(OptionalField("imp0"), OptionalField("imp1"))

  /**
    * Factory method to build a batch of models from a mapping from field id to value
    *
    * @param rawData mapping from field id to value
    * @return A [[Vector]] of models
    */
  override def buildModels(
      rawData: Vector[RawModelData]
  ): Vector[PowerPlantProfile] = {
    /* Determine the ids of the available load profiles by filtering the head line fields */
    val profileTypeStrings =
      super.determineAvailableProfileIds(rawData, None)

    /* Go through each line of the raw data table and extract the time stamp */
    (for (rawTableLine <- rawData) yield {
      val time = simbenchTimeUtil.toZonedDateTime(rawTableLine.get(TIME))

      /* Get the active and reactive power for each available load profile */
      for (typeString <- profileTypeStrings) yield {
        val profileType = PowerPlantProfileType(typeString)
        val factor = rawTableLine.get(typeString).toDouble
        (profileType, time, factor)
      }
    }).flatten /* Flatten everything to have Vector((profileType, time, factor)) */
      .groupBy(collectionEntry => collectionEntry._1) /* Build a Map(profileType -> (profileType, time, factor)) */
      .map {
        /* Extract the needed information to build a LoadProfile for each profile type */
        case (profileType, profileEntry) =>
          val profileValues =
            profileEntry.map {
              case (_, time, factor) => time -> factor
            }.toMap

          PowerPlantProfile(
            "\\$$".r.replaceAllIn(profileType.getClass.getSimpleName, ""),
            profileType,
            profileValues
          )
      }
      .toVector /* Finally build the Vector(PowerPlantProfile) */
  }
}
