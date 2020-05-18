package edu.ie3.simbench.model.datamodel.profiles

import java.time.ZonedDateTime

import edu.ie3.simbench.io.HeadLineField
import edu.ie3.simbench.io.HeadLineField.{MandatoryField, OptionalField}
import edu.ie3.simbench.model.RawModelData
import edu.ie3.simbench.model.datamodel.profiles.ProfileModel.ProfileCompanionObject

/**
  * A load profile consisting of an identifier and a mapping of the date to (p,q) pair
  *
  * @param id           Identifier of the profile
  * @param profileType  The type of the profile
  * @param profile      The actual profile as scaling factor in p.u.
  */
case class LoadProfile(
    id: String,
    profileType: LoadProfileType,
    profile: Map[ZonedDateTime, (BigDecimal, BigDecimal)]
) extends ProfileModel[LoadProfileType, (BigDecimal, BigDecimal)]

case object LoadProfile
    extends ProfileCompanionObject[LoadProfile, (BigDecimal, BigDecimal)] {
  private val activePowerSuffix = "_pload"
  private val reactivePowerSuffix = "_qload"

  private val profiles = Vector(
    "BL-H",
    "G0-A",
    "G0-M",
    "G1-A",
    "G1-B",
    "G1-C",
    "G2-A",
    "G3-A",
    "G3-H",
    "G3-M",
    "G4-A",
    "G4-B",
    "G4-M",
    "G5-A",
    "G6-A",
    "H0-A",
    "H0-B",
    "H0-C",
    "H0-G",
    "H0-L",
    "L0-A",
    "L1-A",
    "L2-A",
    "L2-M",
    "WB-H",
    "lv_rural1",
    "lv_rural2",
    "lv_rural3",
    "lv_semiurb4",
    "lv_semiurb5",
    "lv_urban6",
    "mv_rural",
    "mv_semiurb",
    "mv_urban",
    "mv_comm",
    "mv_add1",
    "mv_add2",
    "hv_mixed1",
    "hv_mixed2",
    "hv_mixed3",
    "hv_urban",
    "HS0",
    "HS1",
    "HS2",
    "HS3",
    "HS4",
    "HS5",
    "HS6",
    "HS7",
    "HS8",
    "HS9",
    "HS10",
    "HS11",
    "HS12",
    "HS13",
    "HS14",
    "HS15",
    "HS16",
    "HS17",
    "HS18",
    "HS19",
    "HS20",
    "HS21",
    "HS22",
    "HS23",
    "HS24",
    "HS25",
    "HS26",
    "HS27",
    "HSexp0",
    "HSexp1"
  )

  /**
    * Get an Array of table fields denoting the mapping to the model's attributes. Each available profile has a field
    * with suffix "_pload" and "_qload"
    *
    * @return Array of table headings
    */
  override def getFields: Array[HeadLineField] =
    Array(
      MandatoryField(TIME)
    ) ++ profiles.flatMap(
      profile =>
        Vector(
          OptionalField(profile + activePowerSuffix),
          OptionalField(profile + reactivePowerSuffix)
        )
    )

  /**
    * Factory method to build a batch of models from a mapping from field id to value
    *
    * @param rawData mapping from field id to value
    * @return A [[Vector]] of models
    */
  override def buildModels(
      rawData: Vector[RawModelData]
  ): Vector[LoadProfile] = {
    /* Determine the ids of the available load profiles by filtering the head line fields */
    val profileTypeStrings =
      super.determineAvailableProfileIds(
        rawData,
        Some(LoadProfileType.stripSuffix)
      )

    /* Go through each line of the raw data table and extract the time stamp */
    (for (rawTableLine <- rawData) yield {
      val time = simbenchTimeUtil.toZonedDateTime(rawTableLine.get(TIME))

      /* Get the active and reactive power for each available load profile */
      for (typeString <- profileTypeStrings) yield {
        val profileType = LoadProfileType(typeString)
        val p = BigDecimal(rawTableLine.get(typeString + activePowerSuffix))
        val q = BigDecimal(rawTableLine.get(typeString + reactivePowerSuffix))
        (profileType, time, p, q)
      }
    }).flatten /* Flatten everything to have Vector((profileType, time, p, q)) */
      .groupBy(collectionEntry => collectionEntry._1) /* Build a Map(profileType -> (profileType, time, p, q)) */
      .map(profileEntry => {
        /* Extract the needed information to build a LoadProfile for each profile type */
        val profileType = profileEntry._1
        val profileValues =
          profileEntry._2.map(entry => entry._2 -> (entry._3, entry._4)).toMap

        LoadProfile(
          "\\$$".r.replaceAllIn(profileType.getClass.getSimpleName, ""),
          profileType,
          profileValues
        )
      })
      .toVector /* Finally build the Vector(LoadProfile) */
  }
}
