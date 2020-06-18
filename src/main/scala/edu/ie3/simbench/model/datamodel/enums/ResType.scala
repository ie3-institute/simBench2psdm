package edu.ie3.simbench.model.datamodel.enums

import edu.ie3.simbench.exception.io.SimbenchDataModelException
import edu.ie3.simbench.util.ParsableEnumeration

import scala.util.{Failure, Success, Try}

/**
  * Available renewable energy sources to SimBench
  */
case object ResType extends ParsableEnumeration {
  val Biomass: Value = Value("biomass")
  val PV: Value = Value("pv")
  val RunOfRiver: Value = Value("run of river")
  val WindOnshore: Value = Value("wind onshore")
  val WindOffshore: Value = Value("wind offshore")
  val BiomassMv: Value = Value("biomass_mv")
  val HydroMv: Value = Value("hydro_mv")
  val PvMv: Value = Value("pv_mv")
  val Wind: Value = Value("wind")
  val WindMv: Value = Value("wind_mv")
  val LvRural1: Value = Value("lv_rural1")
  val LvRural2: Value = Value("lv_rural2")
  val LvRural3: Value = Value("lv_rural3")
  val LvSemiurb4: Value = Value("lv_semiurb4")
  val LvRes: Value = Value("lv_res")
  val MvAdd0: Value = Value("mv_add0")
  val MvAdd1: Value = Value("mv_add1")
  val MvComm: Value = Value("mv_comm")
  val MvRural: Value = Value("mv_rural")
  val MvSemiurb: Value = Value("mv_semiurb")
  val MvUrban: Value = Value("mv_urban")

  /**
    * Hands back a suitable [[ResType]] based on the entries given in SimBench csv files
    *
    * @param typeString Entry in csv file
    * @return The matching [[ResType]]
    * @throws SimbenchDataModelException if a non valid type string has been provided
    */
  def apply(typeString: String): Try[ResType.Value] = parse(typeString) match {
    case Some(value) => Success(value)
    case None =>
      Failure(
        SimbenchDataModelException(
          s"There is no defined energy source for identifier type '$typeString'"
        )
      )
  }
}
