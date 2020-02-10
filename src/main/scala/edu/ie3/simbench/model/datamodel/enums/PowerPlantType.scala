package edu.ie3.simbench.model.datamodel.enums

import edu.ie3.simbench.exception.io.SimbenchDataModelException

/**
  * Available power plant types to SimBench
  */
sealed trait PowerPlantType

case object PowerPlantType {
  case object Gas extends PowerPlantType
  case object HardCoal extends PowerPlantType
  case object Imp0 extends PowerPlantType
  case object Imp1 extends PowerPlantType
  case object Lignite extends PowerPlantType
  case object Nuclear extends PowerPlantType
  case object Oil extends PowerPlantType
  case object Waste extends PowerPlantType

  /**
    * Hands back a suitable [[PowerPlantType]] based on the entries given in SimBench csv files
    *
    * @param typeString Entry in csv file
    * @return The matching [[PowerPlantType]]
    * @throws SimbenchDataModelException if a non valid type string has been provided
    */
  @throws[SimbenchDataModelException]
  def apply(typeString: String): PowerPlantType =
    typeString.toLowerCase.replaceAll("[_ ]*", "") match {
      case "gas"      => Gas
      case "hardcoal" => HardCoal
      case "imp0"     => Imp0
      case "imp1"     => Imp1
      case "lignite"  => Lignite
      case "nuclear"  => Nuclear
      case "oil"      => Oil
      case "waste"    => Waste
      case whatever =>
        throw SimbenchDataModelException(
          s"I cannot handle the power plant type $whatever")
    }
}
