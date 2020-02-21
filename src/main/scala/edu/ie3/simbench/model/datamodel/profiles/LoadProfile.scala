package edu.ie3.simbench.model.datamodel.profiles

import java.time.LocalDateTime

import edu.ie3.simbench.exception.io.SimbenchDataModelException
import edu.ie3.simbench.model.RawModelData
import edu.ie3.simbench.model.datamodel.SimbenchModel.SimbenchCompanionObject

/**
  * A load profile consisting of an identifier and a mapping of the date to (p,q) pair
  *
  * @param profileType The type of the profile
  * @param profile The actual profile as scaling factor in p.u.
  */
case class LoadProfile(profileType: LoadProfileType,
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
  override def getFields: Array[String] = Array(
    TIME,
    BL_H_Q,
    BL_H_P,
    G0_A_Q,
    G0_A_P,
    G0_M_Q,
    G0_M_P,
    G1_A_Q,
    G1_A_P,
    G1_B_Q,
    G1_B_P,
    G1_C_Q,
    G1_C_P,
    G2_A_Q,
    G2_A_P,
    G3_A_Q,
    G3_A_P,
    G3_H_Q,
    G3_H_P,
    G3_M_Q,
    G3_M_P,
    G4_A_Q,
    G4_A_P,
    G4_B_Q,
    G4_B_P,
    G4_M_Q,
    G4_M_P,
    G5_A_Q,
    G5_A_P,
    G6_A_Q,
    G6_A_P,
    H0_A_Q,
    H0_A_P,
    H0_B_Q,
    H0_B_P,
    H0_C_Q,
    H0_C_P,
    H0_G_Q,
    H0_G_P,
    H0_L_Q,
    H0_L_P,
    L0_A_Q,
    L0_A_P,
    L1_A_Q,
    L1_A_P,
    L2_A_Q,
    L2_A_P,
    L2_M_Q,
    L2_M_P,
    WB_H_Q,
    WB_H_P,
    LV_RURAL1_Q,
    LV_RURAL1_P,
    LV_RURAL2_Q,
    LV_RURAL2_P,
    LV_RURAL3_Q,
    LV_RURAL3_P,
    LV_SEMIURB4_P,
    LV_SEMIURB4_Q,
    LV_SEMIURB5_P,
    LV_SEMIURB5_Q,
    LV_URBAN_6_P,
    LV_URBAN6_Q,
    MV_RURAL_P,
    MV_RURAL_Q,
    MV_SEMIURB_P,
    MV_SEMIURB_Q,
    MV_URBAN_P,
    MV_URBAN_Q,
    MV_COMM_P,
    MV_COMM_Q,
    MV_ADD1_Q,
    MV_ADD1_P,
    MV_ADD2_Q,
    MV_ADD2_P,
    HV_MIXED1_P,
    HV_MIXED2_P,
    HV_MIXED3_P,
    HV_MIXED1_Q,
    HV_MIXED2_Q,
    HV_MIXED3_Q,
    HV_URBAN_P,
    HV_URBAN_Q,
    HS0_P,
    HS0_Q,
    HS1_P,
    HS1_Q,
    HS2_P,
    HS2_Q,
    HS3_P,
    HS3_Q,
    HS4_P,
    HS4_Q,
    HS5_P,
    HS5_Q,
    HS6_P,
    HA6_Q,
    HS7_P,
    HS7_Q,
    HS8_P,
    HS8_Q,
    HS9_P,
    HS9_Q,
    HS10_P,
    HS10_Q,
    HS11_P,
    HS11_Q,
    HS12_P,
    HS12_Q,
    HS13_P,
    HS13_Q,
    HS14_P,
    HS14_Q,
    HS15_P,
    HS15_Q,
    HS16_P,
    HS16_Q,
    HS17_P,
    HS17_Q,
    HA18_P,
    HS18_Q,
    HS19_P,
    HS19_Q,
    HS20_P,
    HS20_Q,
    HS21_P,
    HS21_Q,
    HS22_P,
    HS22_Q,
    HS23_P,
    HS23_Q,
    HS24_P,
    HS24_Q,
    HS25_P,
    HS25_Q,
    HS26_P,
    HS26_Q,
    HS27_P,
    HS27_Q,
    HS_EXP0_P,
    HS_EXP0_Q,
    HS_EXP1_P,
    HS_EXP1_Q
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
  override def buildModels(rawData: Vector[RawModelData]): Vector[LoadProfile] = ???
}
