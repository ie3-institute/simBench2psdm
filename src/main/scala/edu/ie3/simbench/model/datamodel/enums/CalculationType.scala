package edu.ie3.simbench.model.datamodel.enums

import edu.ie3.simbench.exception.io.SimbenchDataModelException

/** Available calculation types for e.g. power plants
  */
sealed trait CalculationType

case object CalculationType {

  /** Simulation is focused on active and reactive power provision
    */
  case object PQ extends CalculationType

  /** Simulation is focused on active power and voltage magnitude regulation
    */
  case object PVm extends CalculationType

  /** Simulation is focused on voltage magnitude and angle regulation (Slack
    * node)
    */
  case object VaVm extends CalculationType

  /** Ward equivalent
    */
  case object Ward extends CalculationType

  /** Extended Ward equivalent
    */
  case object WardExtended extends CalculationType

  /** Hands back a suitable [[CalculationType]] based on the entries given in
    * SimBench csv files
    *
    * @param typeString
    *   Entry in csv file
    * @return
    *   The matching [[CalculationType]]
    * @throws SimbenchDataModelException
    *   if a non valid type string has been provided
    */
  @throws[SimbenchDataModelException]
  def apply(typeString: String): CalculationType =
    typeString.toLowerCase.replaceAll("[_ ]*", "") match {
      case "pq"    => PQ
      case "pvm"   => PVm
      case "vavm"  => VaVm
      case "ward"  => Ward
      case "xward" => WardExtended
      case whatever =>
        throw SimbenchDataModelException(
          s"I cannot handle the calculation type $whatever"
        )
    }
}
