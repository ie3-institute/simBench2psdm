package edu.ie3.simbench.model.datamodel.profiles

import edu.ie3.simbench.exception.io.SimbenchDataModelException

sealed trait ResProfileType extends ProfileType

case object ResProfileType {
  case object BM1 extends ResProfileType
  case object BM2 extends ResProfileType
  case object BM3 extends ResProfileType
  case object BM4 extends ResProfileType
  case object BM5 extends ResProfileType
  case object Hydro1 extends ResProfileType
  case object Hydro2 extends ResProfileType
  case object Hydro3 extends ResProfileType
  case object LvRural1 extends ResProfileType
  case object LvRural2 extends ResProfileType
  case object LvRural3 extends ResProfileType
  case object LvSemiurb4 extends ResProfileType
  case object LvSemiurb5 extends ResProfileType
  case object LvSemiurb6 extends ResProfileType
  case object MvAdd1 extends ResProfileType
  case object MvAdd2 extends ResProfileType
  case object MvComm extends ResProfileType
  case object MvRural extends ResProfileType
  case object MvSemiurb extends ResProfileType
  case object MvUrban extends ResProfileType
  case object PV1 extends ResProfileType
  case object PV2 extends ResProfileType
  case object PV3 extends ResProfileType
  case object PV4 extends ResProfileType
  case object PV5 extends ResProfileType
  case object PV6 extends ResProfileType
  case object PV7 extends ResProfileType
  case object PV8 extends ResProfileType
  case object WP1 extends ResProfileType
  case object WP10 extends ResProfileType
  case object WP11 extends ResProfileType
  case object WP12 extends ResProfileType
  case object WP2 extends ResProfileType
  case object WP3 extends ResProfileType
  case object WP4 extends ResProfileType
  case object WP5 extends ResProfileType
  case object WP6 extends ResProfileType
  case object WP7 extends ResProfileType
  case object WP8 extends ResProfileType
  case object WP9 extends ResProfileType

  /**
    * Hands back a suitable [[ResProfileType]] based on the entries given in SimBench csv files
    *
    * @param typeString Entry in csv file
    * @return The matching [[ResProfileType]]
    * @throws SimbenchDataModelException if a non valid type string has been provided
    */
  @throws[SimbenchDataModelException]
  def apply(typeString: String): ResProfileType =
    typeString.toLowerCase
      .replaceAll("[_-]+", "") match {
      case "bm1"        => BM1
      case "bm2"        => BM2
      case "bm3"        => BM3
      case "bm4"        => BM4
      case "bm5"        => BM5
      case "hydro1"     => Hydro1
      case "hydro2"     => Hydro2
      case "hydro3"     => Hydro3
      case "lvrural1"   => LvRural1
      case "lvrural2"   => LvRural2
      case "lvrural3"   => LvRural3
      case "lvsemiurb4" => LvSemiurb4
      case "lvsemiurb5" => LvSemiurb5
      case "lvurban6"   => LvSemiurb6
      case "mvadd1"     => MvAdd1
      case "mvadd2"     => MvAdd2
      case "mvcomm"     => MvComm
      case "mvrural"    => MvRural
      case "mvsemiurb"  => MvSemiurb
      case "mvurban"    => MvUrban
      case "pv1"        => PV1
      case "pv2"        => PV2
      case "pv3"        => PV3
      case "pv4"        => PV4
      case "pv5"        => PV5
      case "pv6"        => PV6
      case "pv7"        => PV7
      case "pv8"        => PV8
      case "wp1"        => WP1
      case "wp10"       => WP10
      case "wp11"       => WP11
      case "wp12"       => WP12
      case "wp2"        => WP2
      case "wp3"        => WP3
      case "wp4"        => WP4
      case "wp5"        => WP5
      case "wp6"        => WP6
      case "wp7"        => WP7
      case "wp8"        => WP8
      case "wp9"        => WP9
      case whatever =>
        throw SimbenchDataModelException(
          s"I cannot handle the measurement variable $whatever"
        )
    }
}
