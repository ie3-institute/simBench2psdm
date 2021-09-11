package edu.ie3.simbench.model.datamodel.profiles

import edu.ie3.simbench.exception.io.SimbenchDataModelException

/**
  * Enumeration of available storage profiles
  */
sealed trait StorageProfileType extends ProfileType
case object StorageProfileType {
  case object STORAGE_PV8_L1_A extends StorageProfileType
  case object STORAGE_PV5_L2_A extends StorageProfileType
  case object STORAGE_PV5_L1_A extends StorageProfileType
  case object STORAGE_PV8_L2_A extends StorageProfileType
  case object STORAGE_PV3_H0_A extends StorageProfileType
  case object STORAGE_PV7_H0_G extends StorageProfileType
  case object STORAGE_PV4_H0_A extends StorageProfileType
  case object STORAGE_PV3_G1_B extends StorageProfileType
  case object STORAGE_PV7_H0_C extends StorageProfileType
  case object STORAGE_PV1_H0_B extends StorageProfileType
  case object STORAGE_PV3_G6_A extends StorageProfileType
  case object STORAGE_PV3_H0_G extends StorageProfileType
  case object STORAGE_PV4_H0_G extends StorageProfileType
  case object STORAGE_PV3_H0_B extends StorageProfileType
  case object STORAGE_PV4_H0_L extends StorageProfileType
  case object STORAGE_PV1_H0_C extends StorageProfileType
  case object STORAGE_PV7_H0_B extends StorageProfileType
  case object STORAGE_PV7_H0_L extends StorageProfileType
  case object STORAGE_PV4_H0_C extends StorageProfileType
  case object STORAGE_PV5_H0_A extends StorageProfileType
  case object STORAGE_PV5_G2_A extends StorageProfileType
  case object STORAGE_PV5_H0_G extends StorageProfileType
  case object STORAGE_PV5_G4_A extends StorageProfileType
  case object STORAGE_PV6_H0_C extends StorageProfileType
  case object STORAGE_PV5_H0_L extends StorageProfileType
  case object STORAGE_PV5_G4_B extends StorageProfileType
  case object STORAGE_PV8_H0_B extends StorageProfileType
  case object STORAGE_PV2_H0_G extends StorageProfileType
  case object STORAGE_PV2_H0_B extends StorageProfileType
  case object STORAGE_PV5_H0_B extends StorageProfileType
  case object STORAGE_PV8_G5_A extends StorageProfileType
  case object STORAGE_PV5_G1_C extends StorageProfileType
  case object STORAGE_PV2_H0_A extends StorageProfileType
  case object STORAGE_PV2_G3_A extends StorageProfileType
  case object STORAGE_PV8_H0_L extends StorageProfileType
  case object STORAGE_PV6_H0_B extends StorageProfileType
  case object STORAGE_PV8_H0_C extends StorageProfileType
  case object STORAGE_PV4_G4_M extends StorageProfileType
  case object STORAGE_PV1_G3_M extends StorageProfileType
  case object STORAGE_PV4_G2_A extends StorageProfileType
  case object STORAGE_PV4_3_PERCENT_E_MWH extends StorageProfileType
  case object STORAGE_PV6_3_PERCENT_E_MWH extends StorageProfileType
  case object STORAGE_WP10_3_PERCENT_E_MWH extends StorageProfileType
  case object STORAGE_WP4_3_PERCENT_E_MWH extends StorageProfileType
  case object STORAGE_WP7_3_PERCENT_E_MWH extends StorageProfileType
  case object STORAGE_WP11_3_PERCENT_E_MWH extends StorageProfileType
  case object STORAGE_WP5_3_PERCENT_E_MWH extends StorageProfileType
  case object STORAGE_WP2_3_PERCENT_E_MWH extends StorageProfileType
  case object LV_RURAL1 extends StorageProfileType
  case object LV_RURAL2 extends StorageProfileType
  case object LV_RURAL3 extends StorageProfileType
  case object LV_SEMIURB4 extends StorageProfileType
  case object LV_SEMIURB5 extends StorageProfileType
  case object LV_URBAN6 extends StorageProfileType
  case object MV_COMM extends StorageProfileType
  case object MV_RURAL extends StorageProfileType
  case object MV_SEMIURB extends StorageProfileType
  case object MV_URBAN extends StorageProfileType
  case object HV_MIXED extends StorageProfileType
  case object HV_URBAN extends StorageProfileType

  /**
    * Hands back a suitable [[PowerPlantProfileType]] based on the entries given in SimBench csv files
    *
    * @param typeString Entry in csv file
    * @return The matching [[PowerPlantProfileType]]
    * @throws SimbenchDataModelException if a non valid type string has been provided
    */
  @throws[SimbenchDataModelException]
  def apply(typeString: String): StorageProfileType =
    typeString.toLowerCase
      .replaceAll("[_-]+|(storage)+", "") match {
      case "pv8l1a"     => STORAGE_PV8_L1_A
      case "pv5l2a"     => STORAGE_PV5_L2_A
      case "pv5l1a"     => STORAGE_PV5_L1_A
      case "pv8l2a"     => STORAGE_PV8_L2_A
      case "pv3h0a"     => STORAGE_PV3_H0_A
      case "pv7h0g"     => STORAGE_PV7_H0_G
      case "pv4h0a"     => STORAGE_PV4_H0_A
      case "pv3g1b"     => STORAGE_PV3_G1_B
      case "pv7h0c"     => STORAGE_PV7_H0_C
      case "pv1h0b"     => STORAGE_PV1_H0_B
      case "pv3g6a"     => STORAGE_PV3_G6_A
      case "pv3h0g"     => STORAGE_PV3_H0_G
      case "pv4h0g"     => STORAGE_PV4_H0_G
      case "pv3h0b"     => STORAGE_PV3_H0_B
      case "pv4h0l"     => STORAGE_PV4_H0_L
      case "pv1h0c"     => STORAGE_PV1_H0_C
      case "pv7h0b"     => STORAGE_PV7_H0_B
      case "pv7h0l"     => STORAGE_PV7_H0_L
      case "pv4h0c"     => STORAGE_PV4_H0_C
      case "pv5h0a"     => STORAGE_PV5_H0_A
      case "pv5g2a"     => STORAGE_PV5_G2_A
      case "pv5h0g"     => STORAGE_PV5_H0_G
      case "pv5g4a"     => STORAGE_PV5_G4_A
      case "pv6h0c"     => STORAGE_PV6_H0_C
      case "pv5h0l"     => STORAGE_PV5_H0_L
      case "pv5g4b"     => STORAGE_PV5_G4_B
      case "pv8h0b"     => STORAGE_PV8_H0_B
      case "pv2h0g"     => STORAGE_PV2_H0_G
      case "pv2h0b"     => STORAGE_PV2_H0_B
      case "pv5h0b"     => STORAGE_PV5_H0_B
      case "pv8g5a"     => STORAGE_PV8_G5_A
      case "pv5g1c"     => STORAGE_PV5_G1_C
      case "pv2h0a"     => STORAGE_PV2_H0_A
      case "pv2g3a"     => STORAGE_PV2_G3_A
      case "pv8h0l"     => STORAGE_PV8_H0_L
      case "pv6h0b"     => STORAGE_PV6_H0_B
      case "pv8h0c"     => STORAGE_PV8_H0_C
      case "pv4g4m"     => STORAGE_PV4_G4_M
      case "pv1g3m"     => STORAGE_PV1_G3_M
      case "pv4g2a"     => STORAGE_PV4_G2_A
      case "pv43%emwh"  => STORAGE_PV4_3_PERCENT_E_MWH
      case "pv63%emwh"  => STORAGE_PV6_3_PERCENT_E_MWH
      case "wp103%emwh" => STORAGE_WP10_3_PERCENT_E_MWH
      case "wp43%emwh"  => STORAGE_WP4_3_PERCENT_E_MWH
      case "wp73%emwh"  => STORAGE_WP7_3_PERCENT_E_MWH
      case "wp113%emwh" => STORAGE_WP11_3_PERCENT_E_MWH
      case "wp53%emwh"  => STORAGE_WP5_3_PERCENT_E_MWH
      case "wp23%emwh"  => STORAGE_WP2_3_PERCENT_E_MWH
      case "lvrural1"   => LV_RURAL1
      case "lvrural2"   => LV_RURAL2
      case "lvrural3"   => LV_RURAL3
      case "lvsemiurb4" => LV_SEMIURB4
      case "lvsemiurb5" => LV_SEMIURB5
      case "lvurban6"   => LV_URBAN6
      case "mvcomm"     => MV_COMM
      case "mvrural"    => MV_RURAL
      case "mvsemiurb"  => MV_SEMIURB
      case "mvurban"    => MV_URBAN
      case "hvmixed"    => HV_MIXED
      case "hvurban"    => HV_URBAN
      case whatever =>
        throw SimbenchDataModelException(
          s"I cannot handle the storage profile variable $whatever"
        )
    }
}
