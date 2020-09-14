package edu.ie3.simbench.model.datamodel.profiles

import edu.ie3.simbench.exception.io.SimbenchDataModelException

/**
  * Different load profile types available in SimBench
  */
sealed trait LoadProfileType extends ProfileType

object LoadProfileType {
  case object BLH extends LoadProfileType
  case object G0A extends LoadProfileType
  case object G0M extends LoadProfileType
  case object G1A extends LoadProfileType
  case object G1B extends LoadProfileType
  case object G1C extends LoadProfileType
  case object G2A extends LoadProfileType
  case object G3A extends LoadProfileType
  case object G3H extends LoadProfileType
  case object G3M extends LoadProfileType
  case object G4A extends LoadProfileType
  case object G4B extends LoadProfileType
  case object G4M extends LoadProfileType
  case object G5A extends LoadProfileType
  case object G6A extends LoadProfileType
  case object H0A extends LoadProfileType
  case object H0B extends LoadProfileType
  case object H0C extends LoadProfileType
  case object H0G extends LoadProfileType
  case object H0L extends LoadProfileType
  case object L0A extends LoadProfileType
  case object L1A extends LoadProfileType
  case object L2A extends LoadProfileType
  case object L2M extends LoadProfileType
  case object WBH extends LoadProfileType
  case object SoilAlternative1 extends LoadProfileType
  case object SoilAlternative2 extends LoadProfileType
  case object LvRural1 extends LoadProfileType
  case object LvRural2 extends LoadProfileType
  case object LvRural3 extends LoadProfileType
  case object LvSemiurb4 extends LoadProfileType
  case object LvSemiurb5 extends LoadProfileType
  case object LvUrban6 extends LoadProfileType
  case object MvRural extends LoadProfileType
  case object MvSemiurb extends LoadProfileType
  case object MvUrban extends LoadProfileType
  case object MvComm extends LoadProfileType
  case object MvAdd1 extends LoadProfileType
  case object MvAdd2 extends LoadProfileType
  case object HvMixed1 extends LoadProfileType
  case object HvMixed2 extends LoadProfileType
  case object HvMixed3 extends LoadProfileType
  case object HvUrban extends LoadProfileType
  case object HS0 extends LoadProfileType
  case object HS1 extends LoadProfileType
  case object HS2 extends LoadProfileType
  case object HS3 extends LoadProfileType
  case object HS4 extends LoadProfileType
  case object HS5 extends LoadProfileType
  case object HS6 extends LoadProfileType
  case object HS7 extends LoadProfileType
  case object HS8 extends LoadProfileType
  case object HS9 extends LoadProfileType
  case object HS10 extends LoadProfileType
  case object HS11 extends LoadProfileType
  case object HS12 extends LoadProfileType
  case object HS13 extends LoadProfileType
  case object HS14 extends LoadProfileType
  case object HS15 extends LoadProfileType
  case object HS16 extends LoadProfileType
  case object HS17 extends LoadProfileType
  case object HS18 extends LoadProfileType
  case object HS19 extends LoadProfileType
  case object HS20 extends LoadProfileType
  case object HS21 extends LoadProfileType
  case object HS22 extends LoadProfileType
  case object HS23 extends LoadProfileType
  case object HS24 extends LoadProfileType
  case object HS25 extends LoadProfileType
  case object HS26 extends LoadProfileType
  case object HS27 extends LoadProfileType
  case object HsExp0 extends LoadProfileType
  case object HsExp1 extends LoadProfileType
  case object APLSA37 extends LoadProfileType
  case object APLSA110 extends LoadProfileType
  case object APLSA220 extends LoadProfileType
  case object APLSA500 extends LoadProfileType
  case object APLSB37 extends LoadProfileType
  case object APLSB110 extends LoadProfileType
  case object APLSB220 extends LoadProfileType
  case object APLS01014 extends LoadProfileType
  case object APLS0033 extends LoadProfileType
  case object APLS05202 extends LoadProfileType
  case object HLSA37 extends LoadProfileType
  case object HLSA110 extends LoadProfileType
  case object HLSA220 extends LoadProfileType
  case object HLSB37 extends LoadProfileType
  case object HLSB110 extends LoadProfileType
  case object HLSC37 extends LoadProfileType
  case object AirAlternative1 extends LoadProfileType
  case object AirAlternative2 extends LoadProfileType
  case object AirParallel1 extends LoadProfileType
  case object AirParallel2 extends LoadProfileType
  case object AirSemiParallel1 extends LoadProfileType
  case object AirSemiParallel2 extends LoadProfileType

  /**
    * Hands back a suitable [[LoadProfileType]] based on the entries given in SimBench csv files
    *
    * @param typeString Entry in csv file
    * @return The matching [[LoadProfileType]]
    * @throws SimbenchDataModelException if a non valid type string has been provided
    */
  @throws[SimbenchDataModelException]
  def apply(typeString: String): LoadProfileType =
    "[-_.]+".r.replaceAllIn(stripSuffix(typeString), "").toLowerCase match {
      case "blh"              => BLH
      case "g0a"              => G0A
      case "g0m"              => G0M
      case "g1a"              => G1A
      case "g1b"              => G1B
      case "g1c"              => G1C
      case "g2a"              => G2A
      case "g3a"              => G3A
      case "g3h"              => G3H
      case "g3m"              => G3M
      case "g4a"              => G4A
      case "g4b"              => G4B
      case "g4m"              => G4M
      case "g5a"              => G5A
      case "g6a"              => G6A
      case "h0a"              => H0A
      case "h0b"              => H0B
      case "h0c"              => H0C
      case "h0g"              => H0G
      case "h0l"              => H0L
      case "l0a"              => L0A
      case "l1a"              => L1A
      case "l2a"              => L2A
      case "l2m"              => L2M
      case "wbh"              => WBH
      case "soilalternative1" => SoilAlternative1
      case "soilalternative2" => SoilAlternative2
      case "lvrural1"         => LvRural1
      case "lvrural2"         => LvRural2
      case "lvrural3"         => LvRural3
      case "lvsemiurb4"       => LvSemiurb4
      case "lvsemiurb5"       => LvSemiurb5
      case "lvurban6"         => LvUrban6
      case "mvrural"          => MvRural
      case "mvsemiurb"        => MvSemiurb
      case "mvurban"          => MvUrban
      case "mvcomm"           => MvComm
      case "mvadd1"           => MvAdd1
      case "mvadd2"           => MvAdd2
      case "hvmixed1"         => HvMixed1
      case "hvmixed2"         => HvMixed2
      case "hvmixed3"         => HvMixed3
      case "hvurban"          => HvUrban
      case "hs0"              => HS0
      case "hs1"              => HS1
      case "hs2"              => HS2
      case "hs3"              => HS3
      case "hs4"              => HS4
      case "hs5"              => HS5
      case "hs6"              => HS6
      case "hs7"              => HS7
      case "hs8"              => HS8
      case "hs9"              => HS9
      case "hs10"             => HS10
      case "hs11"             => HS11
      case "hs12"             => HS12
      case "hs13"             => HS13
      case "hs14"             => HS14
      case "hs15"             => HS15
      case "hs16"             => HS16
      case "hs17"             => HS17
      case "hs18"             => HS18
      case "hs19"             => HS19
      case "hs20"             => HS20
      case "hs21"             => HS21
      case "hs22"             => HS22
      case "hs23"             => HS23
      case "hs24"             => HS24
      case "hs25"             => HS25
      case "hs26"             => HS26
      case "hs27"             => HS27
      case "hsexp0"           => HsExp0
      case "hsexp1"           => HsExp1
      case "aplsa37"          => APLSA37
      case "aplsa110"         => APLSA110
      case "aplsa220"         => APLSA220
      case "aplsa500"         => APLSA500
      case "aplsb37"          => APLSB37
      case "aplsb110"         => APLSB110
      case "aplsb220"         => APLSB220
      case "apls0033"         => APLS0033
      case "apls01014"        => APLS01014
      case "apls05202"        => APLS05202
      case "hlsa37"           => HLSA37
      case "hlsa110"          => HLSA110
      case "hlsa220"          => HLSA220
      case "hlsb37"           => HLSB37
      case "hlsb110"          => HLSB110
      case "hlsc37"           => HLSB37
      case "airalternative1"  => AirAlternative1
      case "airalternative2"  => AirAlternative2
      case "airparallel1"     => AirParallel1
      case "airparallel2"     => AirParallel2
      case "airsemiparallel1" => AirSemiParallel1
      case "airsemiparallel2" => AirSemiParallel2
      case whatever =>
        throw SimbenchDataModelException(
          s"I cannot handle the load profile id $whatever"
        )
    }

  /**
    * Removes the "pLoad" and "qLoad" suffix of table head line fields in order to extract the identifiable string
    * representation of profile types
    */
  val stripSuffix: String => String = (input: String) =>
    "[-_](?:qload|pload)$".r.replaceAllIn(input, "")
}
