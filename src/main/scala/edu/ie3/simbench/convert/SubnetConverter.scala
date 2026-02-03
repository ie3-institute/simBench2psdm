package edu.ie3.simbench.convert

import edu.ie3.simbench.convert.SubnetConverter.RatedVoltId
import edu.ie3.simbench.exception.ConversionException

import scala.util.matching.Regex

/** SimBench's concept of "subnet" is a little bit different to ie3 power system
  * data model's understanding of it. This class does the mapping between the
  * SimBench and the power system data model understanding. Therefore, SimBench
  * nodes' subnet and rated voltage information are used and mapped onto an
  * integer.
  *
  * The pair of rated voltage and subnet string is in first order sorted
  * descending with respect to the rated voltage and in second order ascending
  * in its id and mapped against an ascending integer counting from 1 onward. In
  * general the subnet ids in SimBench look like "EHV1" or "LV5". However,
  * within substations the ids like "EHV1_HV1" are used. For usage in power
  * system data model those ids are not taken into account, but in later
  * identification split up to the distinct part. E.g. if a node with 220 kV
  * rated voltage and subnet "EHV1_HV1" needs mapping, it is assigned to subnet
  * "EHV" for example.
  *
  * Please note, that there is another difference in subnet mapping when
  * switchgear comes into play upstream of a transformer. As this can only be
  * considered, when the whole grid structure is available, this is addressed in
  * [[GridConverter.determineSubnetOverrides()]]
  *
  * @param ratedVoltageIdPairs
  *   Vector of known combinations of rated voltage and subnet id
  */
final case class SubnetConverter(ratedVoltageIdPairs: Vector[RatedVoltId]) {
  val mapping: Map[RatedVoltId, Int] = ratedVoltageIdPairs.distinct
    .map {
      case (
            ratedVoltage,
            SubnetConverter.validSimbenchSubnetId(
              firstSubnetId,
              firstVoltLvl,
              maybeSecondSubnetId,
              maybeSecondVoltLvl,
              _
            )
          ) =>
        (Option(maybeSecondSubnetId), Option(maybeSecondVoltLvl)) match {
          case (Some(secondSubnetId), Some(secondVoltLvl)) =>
            /* The id refers to a transformer level subnet --> Split up according to the rated voltage and the fitting
             * part of the id */
            SubnetConverter.permissibleRatedVoltages.getOrElse(
              firstVoltLvl.toLowerCase,
              throw ConversionException(
                s"'$firstVoltLvl' is no known voltage level."
              )
            ) match {
              case (lb, ub) if ratedVoltage > lb && ratedVoltage <= ub =>
                (ratedVoltage, firstSubnetId)
              case _ =>
                SubnetConverter.permissibleRatedVoltages.getOrElse(
                  secondVoltLvl.toLowerCase,
                  throw ConversionException(
                    s"'$maybeSecondVoltLvl' is no known voltage level."
                  )
                ) match {
                  case (lb, ub) if ratedVoltage > lb && ratedVoltage <= ub =>
                    (ratedVoltage, secondSubnetId)
                  case _ =>
                    throw ConversionException(
                      s"Neither '$firstVoltLvl', nor '$maybeSecondVoltLvl' fit the given rated voltage $ratedVoltage kV."
                    )
                }
            }
          case (Some(secondSubnetId), None) =>
            throw new IllegalArgumentException(
              s"Cannot handle subnet id '$secondSubnetId', as it does not contain voltage level information"
            )
          case _ =>
            /* Use the original information, but without feeder information */
            (ratedVoltage, firstSubnetId)
        }
    }
    .distinct
    .sortWith {
      // sort descending in rated voltage and ascending in id
      case ((thisRatedVolt,thisId),(thatRatedVolt,thatId)) =>
        thisRatedVolt.compareTo(thatRatedVolt) match {
          case 1  => true
          case -1 => false
          case 0  => thisId < thatId
        }
    }
    // 🔹 group all entries with equal numeric voltage together
    .groupBy(_._1)
    // 🔹 sort groups descending by their voltage value
    .toSeq.sortBy(-_._1)
    // 🔹 assign one subgrid number per voltage group
    .foldLeft((Map.empty[(BigDecimal,String),Int], 0)) {
      case ((acc,currentNumber),(volt,pairs)) =>
        val nextNumber = currentNumber + 1

        val updatedAcc = pairs.foldLeft(acc) {
          case (innerAcc,pair @ (_,id)) =>
            innerAcc + (pair -> nextNumber)
        }

        (updatedAcc,nextNumber)
    }._1


  /** Get the converted subnet as Int
    *
    * @param ratedVoltage
    *   Rated voltage
    * @param id
    *   Identifier of the subnet
    * @return
    *   Int representation of the SimBench subnet
    */
  def convert(ratedVoltage: BigDecimal, id: String): Int = {
    id match {
      case SubnetConverter.validSimbenchSubnetId(
            firstSubnetId,
            _,
            maybeSecondSubnetId,
            _,
            _
          ) =>
        Option(maybeSecondSubnetId) match {
          case Some(secondSubnetId) =>
            /* The id refers to a transformer level subnet --> Split up according to the rated voltage and the fitting
             * part of the id */
            mapping.getOrElse(
              (ratedVoltage, firstSubnetId),
              mapping.getOrElse(
                (ratedVoltage, secondSubnetId),
                throw new IllegalArgumentException(
                  s"The SimBench subnet '$id' with rated voltage $ratedVoltage kV has not been initialized with the converter."
                )
              )
            )
          case None =>
            mapping.getOrElse(
              (ratedVoltage, firstSubnetId),
              throw new IllegalArgumentException(
                s"The SimBench subnet '$id' with rated voltage $ratedVoltage kV has not been initialized with the converter."
              )
            )
        }
      case malformedId =>
        throw new IllegalArgumentException(
          s"The provided id '$malformedId' is no valid subnet identifier."
        )
    }
  }
}

case object SubnetConverter {
  type RatedVoltId = (BigDecimal, String)

  /** Valid subnet ids are of one of the following forms:
    *   - MV4.101_LV4.101_Feeder5
    *   - MV4.101_LV4.101
    *   - MV4.101_Feeder5
    *   - MV4.101
    *   - HV1
    *
    * The capturing groups catch 1) The full first subnet identifier (e.g.
    * MV4.101) 2) The volt level information of the first subnet (e.g. MV) 3)
    * The full second subnet identifier (e.g. LV4.101) 4) The volt level
    * information of the second subnet (e.g. LV) 5) The feeder number (e.g. 5)
    */
  val validSimbenchSubnetId: Regex =
    "^(([a-zA-Z]{1,3})[.\\d]*)(?:_(([a-zA-Z]{1,3})[.\\d]*))?(?:_Feeder(\\d+))?".r

  val permissibleRatedVoltages = Map(
    "ehv" -> (BigDecimal("110"), BigDecimal("420")),
    "hv" -> (BigDecimal("30"), BigDecimal("110")),
    "mv" -> (BigDecimal("10"), BigDecimal("30")),
    "lv" -> (BigDecimal("0"), BigDecimal("10"))
  )
}
