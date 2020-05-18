package edu.ie3.simbench.model.datamodel.enums

import edu.ie3.simbench.exception.io.SimbenchDataModelException
import edu.ie3.simbench.util.ParsableEnumeration

/**
  * Available renewable energy sources to SimBench
  */
case object ResType extends ParsableEnumeration {
  val Biomass: Value = Value("biomass")
  val PV: Value = Value("pv")
  val RunOfRiver: Value = Value("runofriver")
  val WindOnshore: Value = Value("windonshore")
  val WindOffshore: Value = Value("windoffshore")
  val BiomassMv: Value = Value("biomassmv")
  val HydroMv: Value = Value("hydromv")
  val PvMv: Value = Value("pvmv")
  val WindMv: Value = Value("windmv")
  val LvRural1: Value = Value("lvrural1")
  val LvRural2: Value = Value("lvrural2")
  val LvRural3: Value = Value("lvrural3")
  val LvSemiurb4: Value = Value("lvsemiurb4")
  val LvRes: Value = Value("lvres")
  val MvAdd0: Value = Value("mvadd0")
  val MvAdd1: Value = Value("mvadd1")
  val MvComm: Value = Value("mvcomm")
  val MvRural: Value = Value("mvrural")
  val MvSemiurb: Value = Value("mvsemiurb")

  /**
    * Hands back a suitable [[ResType]] based on the entries given in SimBench csv files
    *
    * @param typeString Entry in csv file
    * @return The matching [[ResType]]
    * @throws SimbenchDataModelException if a non valid type string has been provided
    */
  @throws[SimbenchDataModelException]
  def apply(typeString: String): ResType.Value =
    try {
      parse(typeString)
    } catch {
      case e: NoSuchElementException =>
        throw SimbenchDataModelException(
          s"I cannot handle the RES type $typeString",
          e
        )
    }
}
