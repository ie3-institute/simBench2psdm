package edu.ie3.simbench.model.datamodel.enums

import edu.ie3.simbench.exception.io.SimbenchDataModelException

/**
  * Available renewable energy sources to SimBench
  */
sealed trait ResType

case object ResType {
  case object Biomass extends ResType
  case object PV extends ResType
  case object RunOfRiver extends ResType
  case object WindOnshore extends ResType
  case object WindOffshore extends ResType
  case object BiomassMv extends ResType
  case object HydroMv extends ResType
  case object PvMv extends ResType
  case object WindMv extends ResType
  case object LvRural1 extends ResType
  case object LvRural2 extends ResType
  case object LvRural3 extends ResType
  case object LvSemiurb4 extends ResType
  case object LvRes extends ResType
  case object MvAdd0 extends ResType
  case object MvAdd1 extends ResType
  case object MvComm extends ResType
  case object MvRural extends ResType
  case object MvSemiurb extends ResType

  /**
    * Hands back a suitable [[ResType]] based on the entries given in SimBench csv files
    *
    * @param typeString Entry in csv file
    * @return The matching [[ResType]]
    * @throws SimbenchDataModelException if a non valid type string has been provided
    */
  @throws[SimbenchDataModelException]
  def apply(typeString: String): ResType =
    typeString.toLowerCase.replaceAll("[_ ]*", "") match {
      case "biomass"      => Biomass
      case "pv"           => PV
      case "runofriver"   => RunOfRiver
      case "windonshore"  => WindOnshore
      case "windoffshore" => WindOffshore
      case "biomassmv"    => BiomassMv
      case "hydromv"      => HydroMv
      case "pvmv"         => PvMv
      case "windmv"       => WindMv
      case "lvrural1"     => LvRural1
      case "lvrural2"     => LvRural2
      case "lvrural3"     => LvRural3
      case "lvsemiurb4"   => LvSemiurb4
      case "lvres"        => LvRes
      case "mvadd0"       => MvAdd0
      case "mvadd1"       => MvAdd1
      case "mvcomm"       => MvComm
      case "mvrural"      => MvRural
      case "mvsemiurb"    => MvSemiurb
      case whatever =>
        throw SimbenchDataModelException(
          s"I cannot handle the RES type $whatever"
        )
    }
}
