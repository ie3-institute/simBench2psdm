package edu.ie3.simbench.model.datamodel.profiles

import java.time.LocalDateTime

import edu.ie3.simbench.exception.io.SimbenchDataModelException
import edu.ie3.simbench.io.HeadLineField
import edu.ie3.simbench.io.HeadLineField.{MandatoryField, OptionalField}
import edu.ie3.simbench.model.RawModelData
import edu.ie3.simbench.model.datamodel.SimbenchModel.SimbenchCompanionObject

/**
  * A load profile consisting of an identifier and a mapping of the date to (p,q) pair
  *
  * @param id           Identifier of the profile
  * @param profileType  The type of the profile
  * @param profile      The actual profile as scaling factor in p.u.
  */
case class LoadProfile(id: String,
                       profileType: LoadProfileType,
                       profile: Map[LocalDateTime, (BigDecimal, BigDecimal)])
    extends ProfileModel[LoadProfileType]

case object LoadProfile extends SimbenchCompanionObject[LoadProfile] {
  val TIME = "time"
  val BL_H_Q = "BL-H_qload"
  val BL_H_P = "BL-H_pload"
  val G0_A_Q = "G0-A_qload"
  val G0_A_P = "G0-A_pload"
  val G0_M_Q = "G0-M_qload"
  val G0_M_P = "G0-M_pload"
  val G1_A_Q = "G1-A_qload"
  val G1_A_P = "G1-A_pload"
  val G1_B_Q = "G1-B_qload"
  val G1_B_P = "G1-B_pload"
  val G1_C_Q = "G1-C_qload"
  val G1_C_P = "G1-C_pload"
  val G2_A_Q = "G2-A_qload"
  val G2_A_P = "G2-A_pload"
  val G3_A_Q = "G3-A_qload"
  val G3_A_P = "G3-A_pload"
  val G3_H_Q = "G3-H_qload"
  val G3_H_P = "G3-H_pload"
  val G3_M_Q = "G3-M_qload"
  val G3_M_P = "G3-M_pload"
  val G4_A_Q = "G4-A_qload"
  val G4_A_P = "G4-A_pload"
  val G4_B_Q = "G4-B_qload"
  val G4_B_P = "G4-B_pload"
  val G4_M_Q = "G4-M_qload"
  val G4_M_P = "G4-M_pload"
  val G5_A_Q = "G5-A_qload"
  val G5_A_P = "G5-A_pload"
  val G6_A_Q = "G6-A_qload"
  val G6_A_P = "G6-A_pload"
  val H0_A_Q = "H0-A_qload"
  val H0_A_P = "H0-A_pload"
  val H0_B_Q = "H0-B_qload"
  val H0_B_P = "H0-B_pload"
  val H0_C_Q = "H0-C_qload"
  val H0_C_P = "H0-C_pload"
  val H0_G_Q = "H0-G_qload"
  val H0_G_P = "H0-G_pload"
  val H0_L_Q = "H0-L_qload"
  val H0_L_P = "H0-L_pload"
  val L0_A_Q = "L0-A_qload"
  val L0_A_P = "L0-A_pload"
  val L1_A_Q = "L1-A_qload"
  val L1_A_P = "L1-A_pload"
  val L2_A_Q = "L2-A_qload"
  val L2_A_P = "L2-A_pload"
  val L2_M_Q = "L2-M_qload"
  val L2_M_P = "L2-M_pload"
  val WB_H_Q = "WB-H_qload"
  val WB_H_P = "WB-H_pload"
  val LV_RURAL1_Q = "lv_rural1_qload"
  val LV_RURAL1_P = "lv_rural1_pload"
  val LV_RURAL2_Q = "lv_rural2_qload"
  val LV_RURAL2_P = "lv_rural2_pload"
  val LV_RURAL3_Q = "lv_rural3_qload"
  val LV_RURAL3_P = "lv_rural3_pload"
  val LV_SEMIURB4_P = "lv_semiurb4_pload"
  val LV_SEMIURB4_Q = "lv_semiurb4_qload"
  val LV_SEMIURB5_P = "lv_semiurb5_pload"
  val LV_SEMIURB5_Q = "lv_semiurb5_qload"
  val LV_URBAN_6_P = "lv_urban6_pload"
  val LV_URBAN6_Q = "lv_urban6_qload"
  val MV_RURAL_P = "mv_rural_pload"
  val MV_RURAL_Q = "mv_rural_qload"
  val MV_SEMIURB_P = "mv_semiurb_pload"
  val MV_SEMIURB_Q = "mv_semiurb_qload"
  val MV_URBAN_P = "mv_urban_pload"
  val MV_URBAN_Q = "mv_urban_qload"
  val MV_COMM_P = "mv_comm_pload"
  val MV_COMM_Q = "mv_comm_qload"
  val MV_ADD1_Q = "mv_add1_qload"
  val MV_ADD1_P = "mv_add1_pload"
  val MV_ADD2_Q = "mv_add2_qload"
  val MV_ADD2_P = "mv_add2_pload"
  val HV_MIXED1_P = "hv_mixed1_pload"
  val HV_MIXED2_P = "hv_mixed2_pload"
  val HV_MIXED3_P = "hv_mixed3_pload"
  val HV_MIXED1_Q = "hv_mixed1_qload"
  val HV_MIXED2_Q = "hv_mixed2_qload"
  val HV_MIXED3_Q = "hv_mixed3_qload"
  val HV_URBAN_P = "hv_urban_pload"
  val HV_URBAN_Q = "hv_urban_qload"
  val HS0_P = "HS0_pload"
  val HS0_Q = "HS0_qload"
  val HS1_P = "HS1_pload"
  val HS1_Q = "HS1_qload"
  val HS2_P = "HS2_pload"
  val HS2_Q = "HS2_qload"
  val HS3_P = "HS3_pload"
  val HS3_Q = "HS3_qload"
  val HS4_P = "HS4_pload"
  val HS4_Q = "HS4_qload"
  val HS5_P = "HS5_pload"
  val HS5_Q = "HS5_qload"
  val HS6_P = "HS6_pload"
  val HA6_Q = "HS6_qload"
  val HS7_P = "HS7_pload"
  val HS7_Q = "HS7_qload"
  val HS8_P = "HS8_pload"
  val HS8_Q = "HS8_qload"
  val HS9_P = "HS9_pload"
  val HS9_Q = "HS9_qload"
  val HS10_P = "HS10_pload"
  val HS10_Q = "HS10_qload"
  val HS11_P = "HS11_pload"
  val HS11_Q = "HS11_qload"
  val HS12_P = "HS12_pload"
  val HS12_Q = "HS12_qload"
  val HS13_P = "HS13_pload"
  val HS13_Q = "HS13_qload"
  val HS14_P = "HS14_pload"
  val HS14_Q = "HS14_qload"
  val HS15_P = "HS15_pload"
  val HS15_Q = "HS15_qload"
  val HS16_P = "HS16_pload"
  val HS16_Q = "HS16_qload"
  val HS17_P = "HS17_pload"
  val HS17_Q = "HS17_qload"
  val HA18_P = "HS18_pload"
  val HS18_Q = "HS18_qload"
  val HS19_P = "HS19_pload"
  val HS19_Q = "HS19_qload"
  val HS20_P = "HS20_pload"
  val HS20_Q = "HS20_qload"
  val HS21_P = "HS21_pload"
  val HS21_Q = "HS21_qload"
  val HS22_P = "HS22_pload"
  val HS22_Q = "HS22_qload"
  val HS23_P = "HS23_pload"
  val HS23_Q = "HS23_qload"
  val HS24_P = "HS24_pload"
  val HS24_Q = "HS24_qload"
  val HS25_P = "HS25_pload"
  val HS25_Q = "HS25_qload"
  val HS26_P = "HS26_pload"
  val HS26_Q = "HS26_qload"
  val HS27_P = "HS27_pload"
  val HS27_Q = "HS27_qload"
  val HS_EXP0_P = "HSexp0_pload"
  val HS_EXP0_Q = "HSexp0_qload"
  val HS_EXP1_P = "HSexp1_pload"
  val HS_EXP1_Q = "HSexp1_qload"

  /**
    * Get an Array of table fields denoting the mapping to the model's attributes
    *
    * @return Array of table headings
    */
  override def getFields: Array[HeadLineField] = Array(
    MandatoryField(TIME),
    OptionalField(BL_H_Q),
    OptionalField(BL_H_P),
    OptionalField(G0_A_Q),
    OptionalField(G0_A_P),
    OptionalField(G0_M_Q),
    OptionalField(G0_M_P),
    OptionalField(G1_A_Q),
    OptionalField(G1_A_P),
    OptionalField(G1_B_Q),
    OptionalField(G1_B_P),
    OptionalField(G1_C_Q),
    OptionalField(G1_C_P),
    OptionalField(G2_A_Q),
    OptionalField(G2_A_P),
    OptionalField(G3_A_Q),
    OptionalField(G3_A_P),
    OptionalField(G3_H_Q),
    OptionalField(G3_H_P),
    OptionalField(G3_M_Q),
    OptionalField(G3_M_P),
    OptionalField(G4_A_Q),
    OptionalField(G4_A_P),
    OptionalField(G4_B_Q),
    OptionalField(G4_B_P),
    OptionalField(G4_M_Q),
    OptionalField(G4_M_P),
    OptionalField(G5_A_Q),
    OptionalField(G5_A_P),
    OptionalField(G6_A_Q),
    OptionalField(G6_A_P),
    OptionalField(H0_A_Q),
    OptionalField(H0_A_P),
    OptionalField(H0_B_Q),
    OptionalField(H0_B_P),
    OptionalField(H0_C_Q),
    OptionalField(H0_C_P),
    OptionalField(H0_G_Q),
    OptionalField(H0_G_P),
    OptionalField(H0_L_Q),
    OptionalField(H0_L_P),
    OptionalField(L0_A_Q),
    OptionalField(L0_A_P),
    OptionalField(L1_A_Q),
    OptionalField(L1_A_P),
    OptionalField(L2_A_Q),
    OptionalField(L2_A_P),
    OptionalField(L2_M_Q),
    OptionalField(L2_M_P),
    OptionalField(WB_H_Q),
    OptionalField(WB_H_P),
    OptionalField(LV_RURAL1_Q),
    OptionalField(LV_RURAL1_P),
    OptionalField(LV_RURAL2_Q),
    OptionalField(LV_RURAL2_P),
    OptionalField(LV_RURAL3_Q),
    OptionalField(LV_RURAL3_P),
    OptionalField(LV_SEMIURB4_P),
    OptionalField(LV_SEMIURB4_Q),
    OptionalField(LV_SEMIURB5_P),
    OptionalField(LV_SEMIURB5_Q),
    OptionalField(LV_URBAN_6_P),
    OptionalField(LV_URBAN6_Q),
    OptionalField(MV_RURAL_P),
    OptionalField(MV_RURAL_Q),
    OptionalField(MV_SEMIURB_P),
    OptionalField(MV_SEMIURB_Q),
    OptionalField(MV_URBAN_P),
    OptionalField(MV_URBAN_Q),
    OptionalField(MV_COMM_P),
    OptionalField(MV_COMM_Q),
    OptionalField(MV_ADD1_Q),
    OptionalField(MV_ADD1_P),
    OptionalField(MV_ADD2_Q),
    OptionalField(MV_ADD2_P),
    OptionalField(HV_MIXED1_P),
    OptionalField(HV_MIXED2_P),
    OptionalField(HV_MIXED3_P),
    OptionalField(HV_MIXED1_Q),
    OptionalField(HV_MIXED2_Q),
    OptionalField(HV_MIXED3_Q),
    OptionalField(HV_URBAN_P),
    OptionalField(HV_URBAN_Q),
    OptionalField(HS0_P),
    OptionalField(HS0_Q),
    OptionalField(HS1_P),
    OptionalField(HS1_Q),
    OptionalField(HS2_P),
    OptionalField(HS2_Q),
    OptionalField(HS3_P),
    OptionalField(HS3_Q),
    OptionalField(HS4_P),
    OptionalField(HS4_Q),
    OptionalField(HS5_P),
    OptionalField(HS5_Q),
    OptionalField(HS6_P),
    OptionalField(HA6_Q),
    OptionalField(HS7_P),
    OptionalField(HS7_Q),
    OptionalField(HS8_P),
    OptionalField(HS8_Q),
    OptionalField(HS9_P),
    OptionalField(HS9_Q),
    OptionalField(HS10_P),
    OptionalField(HS10_Q),
    OptionalField(HS11_P),
    OptionalField(HS11_Q),
    OptionalField(HS12_P),
    OptionalField(HS12_Q),
    OptionalField(HS13_P),
    OptionalField(HS13_Q),
    OptionalField(HS14_P),
    OptionalField(HS14_Q),
    OptionalField(HS15_P),
    OptionalField(HS15_Q),
    OptionalField(HS16_P),
    OptionalField(HS16_Q),
    OptionalField(HS17_P),
    OptionalField(HS17_Q),
    OptionalField(HA18_P),
    OptionalField(HS18_Q),
    OptionalField(HS19_P),
    OptionalField(HS19_Q),
    OptionalField(HS20_P),
    OptionalField(HS20_Q),
    OptionalField(HS21_P),
    OptionalField(HS21_Q),
    OptionalField(HS22_P),
    OptionalField(HS22_Q),
    OptionalField(HS23_P),
    OptionalField(HS23_Q),
    OptionalField(HS24_P),
    OptionalField(HS24_Q),
    OptionalField(HS25_P),
    OptionalField(HS25_Q),
    OptionalField(HS26_P),
    OptionalField(HS26_Q),
    OptionalField(HS27_P),
    OptionalField(HS27_Q),
    OptionalField(HS_EXP0_P),
    OptionalField(HS_EXP0_Q),
    OptionalField(HS_EXP1_P),
    OptionalField(HS_EXP1_Q)
  )

  /**
    * Factory method to build one model from a mapping from field id to value
    *
    * @param rawData mapping from field id to value
    * @return A model
    */
  override def buildModel(rawData: RawModelData): LoadProfile =
    throw SimbenchDataModelException(
      s"No basic implementation of model creation available for ${this.getClass.getSimpleName}")

  /**
    * Factory method to build a batch of models from a mapping from field id to value
    *
    * @param rawData mapping from field id to value
    * @return A [[Vector]] of models
    */
  override def buildModels(rawData: Vector[RawModelData]): Vector[LoadProfile] =
    ???
}
