package edu.ie3.simbench.convert

import edu.ie3.simbench.convert.SubnetConverter.RatedVoltId
import edu.ie3.simbench.exception.ConversionException

import scala.util.matching.Regex

/**
  * SimBench's concept of "sub net" is a little bit different to ie3 power system data model's understanding of it.
  * This class does the mapping between the SimBench and the power system data model understanding. Therefore,
  * SimBench nodes' subnet and rated voltage information are used and mapped onto an integer.
  *
  * The pair of rated voltage and subnet string is in first order sorted descending with respect to the rated voltage
  * and in second order ascending in it's id and mapped against an ascending integer counting from 1 onwards. In general
  * the subnet ids in SimBench look like "EHV1" or "LV5". However, within substations the ids like "EHV1_HV1" are used.
  * For usage in power system data model those ids are not taken into account, but in later identification split up to
  * the distinct part. E.g. if a node with 220 kV rated voltage and subnet "EHV1_HV1" needs mapping, it is assigned to
  * subnet "EHV" for example.
  *
  * Please note, that there is another difference in subnet mapping when switchgear comes into play upstream of a
  * transformer. As this can only be considered, when the whole grid structure is available, this is addressed in
  * TODO: Reference
  *
  * @param ratedVoltageIdPairs Vector of known combinations of rated voltage and subnet id
  */
final case class SubnetConverter(ratedVoltageIdPairs: Vector[RatedVoltId]) {
  val mapping: Map[RatedVoltId, Int] = ratedVoltageIdPairs.distinct
    .map {
      case (
          ratedVoltage,
          SubnetConverter.transformationVoltLvlIdRegex(
            firstVoltLvl @ SubnetConverter.voltLvlIdentifierRegex(
              firstVoltLvlId
            ),
            secondVoltLvl @ SubnetConverter.voltLvlIdentifierRegex(
              secondVoltLvlId
            )
          )
          ) =>
        SubnetConverter.permissibleRatedVoltages.getOrElse(
          firstVoltLvlId.toLowerCase,
          throw ConversionException(
            s"'$firstVoltLvl' is no known voltage level."
          )
        ) match {
          case (lb, ub) if ratedVoltage > lb && ratedVoltage <= ub =>
            (ratedVoltage, firstVoltLvl)
          case _ =>
            SubnetConverter.permissibleRatedVoltages.getOrElse(
              secondVoltLvlId.toLowerCase,
              throw ConversionException(
                s"'$secondVoltLvl' is no known voltage level."
              )
            ) match {
              case (lb, ub) if ratedVoltage > lb && ratedVoltage <= ub =>
                (ratedVoltage, secondVoltLvl)
              case _ =>
                throw ConversionException(
                  s"Neither '$firstVoltLvl', nor '$secondVoltLvl' fit the given rated voltage $ratedVoltage kV."
                )
            }
        }
      case other => other
    }
    .distinct
    .sortWith {
      /* Sort the input descending in rated voltage and ascending in id */
      case ((thisRatedVolt, thisId), (thatRatedVolt, thatId)) =>
        thisRatedVolt.compareTo(thatRatedVolt) match {
          case 1  => true
          case -1 => false
          case 0  => thisId < thatId
        }
    }
    .zipWithIndex
    .map {
      case (ratedVoltIdPair, subGridId) =>
        ratedVoltIdPair -> (subGridId + 1)
    }
    .toMap

  /**
    * Get the converted subnet as Int
    *
    * @param ratedVoltage Rated voltage
    * @param id           Identifier of the subnet
    * @return Int representation of the SimBench sub net
    */
  def convert(ratedVoltage: BigDecimal, id: String): Int =
    id match {
      case SubnetConverter.transformationVoltLvlIdRegex(firstId, secondId) =>
        mapping.getOrElse(
          (ratedVoltage, firstId),
          mapping.getOrElse(
            (ratedVoltage, secondId),
            throw new IllegalArgumentException(
              s"The SimBench subnet '$id' with rated voltage $ratedVoltage kV has not been initialized with the converter."
            )
          )
        )
      case _ =>
        mapping.getOrElse(
          (ratedVoltage, id),
          throw new IllegalArgumentException(
            s"The SimBench subnet '$id' with rated voltage $ratedVoltage kV has not been initialized with the converter."
          )
        )
    }
}

case object SubnetConverter {
  type RatedVoltId = (BigDecimal, String)

  val transformationVoltLvlIdRegex: Regex =
    "([a-zA-Z]{1,3}[.\\d]+)_([a-zA-Z]{1,3}[.\\d]+).*".r

  val voltLvlIdentifierRegex: Regex = "([a-zA-Z]{1,3})[.\\d]+".r

  val permissibleRatedVoltages = Map(
    "ehv" -> (BigDecimal("110"), BigDecimal("420")),
    "hv" -> (BigDecimal("30"), BigDecimal("110")),
    "mv" -> (BigDecimal("10"), BigDecimal("30")),
    "lv" -> (BigDecimal("0"), BigDecimal("10"))
  )
}
