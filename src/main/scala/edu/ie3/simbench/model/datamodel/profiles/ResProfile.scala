package edu.ie3.simbench.model.datamodel.profiles

import java.time.ZonedDateTime

import edu.ie3.simbench.io.HeadLineField
import edu.ie3.simbench.io.HeadLineField.{MandatoryField, OptionalField}
import edu.ie3.simbench.model.RawModelData
import edu.ie3.simbench.model.datamodel.profiles.ProfileModel.ProfileCompanionObject
import edu.ie3.util.TimeUtil

/**
  * A renewable energy source's profile consisting of an identifier and a mapping of the date to
  * (p,q) pair
  *
  * @param id           Identifier of the profile
  * @param profileType  The type of the profile
  * @param profile      The actual profile as scaling factor in p.u.
  */
case class ResProfile(
    id: String,
    profileType: ResProfileType,
    profile: Map[ZonedDateTime, BigDecimal]
) extends ProfileModel[ResProfileType, BigDecimal]

case object ResProfile extends ProfileCompanionObject[ResProfile, BigDecimal] {
  private val PV1 = "PV1"
  private val PV2 = "PV2"
  private val PV3 = "PV3"
  private val PV4 = "PV4"
  private val PV5 = "PV5"
  private val PV6 = "PV6"
  private val PV7 = "PV7"
  private val PV8 = "PV8"
  private val WP1 = "WP1"
  private val WP2 = "WP2"
  private val WP3 = "WP3"
  private val WP4 = "WP4"
  private val WP5 = "WP5"
  private val WP6 = "WP6"
  private val WP7 = "WP7"
  private val WP8 = "WP8"
  private val WP9 = "WP9"
  private val WP10 = "WP10"
  private val WP11 = "WP11"
  private val WP12 = "WP12"
  private val BM1 = "BM1"
  private val BM2 = "BM2"
  private val BM3 = "BM3"
  private val BM4 = "BM4"
  private val BM5 = "BM5"
  private val HYDRO1 = "Hydro1"
  private val HYDRO2 = "Hydro2"
  private val HYDRO3 = "Hydro3"
  private val LV_RURAL1 = "lv_rural1"
  private val LV_RURAL2 = "lv_rural2"
  private val LV_RURAL3 = "lv_rural3"
  private val LV_SEMIURB4 = "lv_semiurb4"
  private val LV_SEMIURB5 = "lv_semiurb5"
  private val LV_URBAN6 = "lv_urban6"
  private val MV_RURAL = "mv_rural"
  private val MV_SEMIURB = "mv_semiurb"
  private val MV_URBAN = "mv_urban"
  private val MV_COMM = "mv_comm"
  private val MV_ADD1 = "mv_add1"
  private val MV_ADD2 = "mv_add2"
  private val HV_MIXED = "hv_mixed"
  private val HV_URBAN = "hv_urban"

  /**
    * Get an Array of table fields denoting the mapping to the model's attributes
    *
    * @return Array of table headings
    */
  override def getFields: Array[HeadLineField] = Array(
    MandatoryField(TIME),
    OptionalField(PV1),
    OptionalField(PV2),
    OptionalField(PV3),
    OptionalField(PV4),
    OptionalField(PV5),
    OptionalField(PV6),
    OptionalField(PV7),
    OptionalField(PV8),
    OptionalField(WP1),
    OptionalField(WP2),
    OptionalField(WP3),
    OptionalField(WP4),
    OptionalField(WP5),
    OptionalField(WP6),
    OptionalField(WP7),
    OptionalField(WP8),
    OptionalField(WP9),
    OptionalField(WP10),
    OptionalField(WP11),
    OptionalField(WP12),
    OptionalField(BM1),
    OptionalField(BM2),
    OptionalField(BM3),
    OptionalField(BM4),
    OptionalField(BM5),
    OptionalField(HYDRO1),
    OptionalField(HYDRO2),
    OptionalField(HYDRO3),
    OptionalField(LV_RURAL1),
    OptionalField(LV_RURAL2),
    OptionalField(LV_RURAL3),
    OptionalField(LV_SEMIURB4),
    OptionalField(LV_SEMIURB5),
    OptionalField(LV_URBAN6),
    OptionalField(MV_RURAL),
    OptionalField(MV_SEMIURB),
    OptionalField(MV_URBAN),
    OptionalField(MV_COMM),
    OptionalField(MV_ADD1),
    OptionalField(MV_ADD2),
    OptionalField(HV_MIXED),
    OptionalField(HV_URBAN)
  )

  /**
    * Factory method to build a batch of models from a mapping from field id to value
    *
    * @param rawData mapping from field id to value
    * @return A [[Vector]] of models
    */
  override def buildModels(
      rawData: Vector[RawModelData]
  ): Vector[ResProfile] = {
    /* Determine the ids of the available load profiles by filtering the head line fields */
    val profileTypeStrings =
      super.determineAvailableProfileIds(rawData, None)

    /* Go through each line of the raw data table and extract the time stamp */
    (for (rawTableLine <- rawData) yield {
      val time = simbenchTimeUtil.toZonedDateTime(rawTableLine.get(TIME))

      /* Get the active and reactive power for each available load profile */
      for (typeString <- profileTypeStrings) yield {
        val profileType = ResProfileType(typeString)
        val factor = rawTableLine.getBigDecimal(typeString)
        (profileType, time, factor)
      }
    }).flatten /* Flatten everything to have Vector((profileType, time, factor)) */
      .groupBy(collectionEntry => collectionEntry._1) /* Build a Map(profileType -> (profileType, time, factor)) */
      .map(profileEntry => {
        /* Extract the needed information to build a LoadProfile for each profile type */
        val profileType = profileEntry._1
        val profileValues =
          profileEntry._2.map(entry => entry._2 -> entry._3).toMap

        ResProfile(
          "\\$$".r.replaceAllIn(profileType.getClass.getSimpleName, ""),
          profileType,
          profileValues
        )
      })
      .toVector /* Finally build the Vector(PowerPlantProfile) */
  }
}
