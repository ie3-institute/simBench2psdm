package edu.ie3.simbench.convert.types

import java.util.UUID

import com.typesafe.scalalogging.LazyLogging
import edu.ie3.datamodel.models.input.connector.`type`.LineTypeInput
import edu.ie3.simbench.exception.ConversionException
import edu.ie3.simbench.exception.io.SimbenchDataModelException
import edu.ie3.simbench.model.datamodel.Line
import edu.ie3.simbench.model.datamodel.types.LineType
import edu.ie3.util.quantities.PowerSystemUnits.{
  KILOVOLT,
  OHM_PER_KILOMETRE,
  SIEMENS_PER_KILOMETRE
}
import javax.measure.quantity.ElectricPotential
import tec.uom.se.ComparableQuantity
import tec.uom.se.quantity.Quantities
import tec.uom.se.unit.MetricPrefix
import tec.uom.se.unit.Units.AMPERE

/**
  * Currently not supported by ie³'s data model:
  *   - line style
  */
case object LineTypeConverter extends LazyLogging {

  /**
    * Converts the [[LineType]]s of the given lines into [[LineTypeInput]]s
    *
    * @param lines  [[Vector]] of SimBench [[LineType]]s
    * @return       A [[Vector]] of [[LineTypeInput]]s
    */
  def convert(lines: Vector[Line[_ <: LineType]]): Vector[LineTypeInput] = {
    val ratedVoltageMapping = getRatedVoltages(lines)
    val lineTypes = lines.map(line => line.lineType).distinct

    lineTypes.map(
      lineType =>
        convert(
          lineType,
          ratedVoltageMapping.getOrElse(
            lineType,
            throw SimbenchDataModelException(
              s"Cannot find the rated voltage vor line type ${lineType}"
            )
          )
        )
    )
  }

  /**
    * Converts a given SimBench [[LineType]] into a ie³ [[LineTypeInput]]. The [[LineType.DCLineType]]s are currently
    * not supported by the ie³ data model. Therefore an [[IllegalArgumentException]] is thrown.
    *
    * @param input    SimBench [[LineType]] to convert
    * @param vRated   Externally provided rated voltage, as the SimBench [[LineType]] does not provide this information
    * @param uuid     UUID to use for the model generation (default: Random UUID)
    * @return         A ie³ [[LineTypeInput]]
    */
  def convert(
      input: LineType,
      vRated: ComparableQuantity[ElectricPotential],
      uuid: UUID = UUID.randomUUID()
  ): LineTypeInput = {
    input match {
      case LineType.ACLineType(id, r, x, b, iMax, _) =>
        val rQty = Quantities.getQuantity(r, OHM_PER_KILOMETRE)
        val xQty = Quantities.getQuantity(x, OHM_PER_KILOMETRE)
        val gQty =
          Quantities.getQuantity(0d, MetricPrefix.MICRO(SIEMENS_PER_KILOMETRE))
        val bQty =
          Quantities.getQuantity(b, MetricPrefix.MICRO(SIEMENS_PER_KILOMETRE))
        val iMaxQty = Quantities.getQuantity(iMax, AMPERE)

        new LineTypeInput(uuid, id, bQty, gQty, rQty, xQty, iMaxQty, vRated)
      case _: LineType.DCLineType =>
        throw ConversionException(
          "DC line types are currently not supported by ie³'s data model."
        )
    }
  }

  /**
    * Extracts a mapping of [[LineType]]s to the rated voltages of those types. Unambiguousness is ensured.
    *
    * @param lines  [[Vector]] of [[Line]]s
    * @return       Mapping of [[LineType]] to [[ComparableQuantity]] of type [[ElectricPotential]]
    */
  def getRatedVoltages(
      lines: Vector[Line[_ <: LineType]]
  ): Map[LineType, ComparableQuantity[ElectricPotential]] = {
    val rawMapping = lines
      .distinctBy(line => line.lineType)
      .map(line => determineRatedVoltage(line))
      .groupBy(tuple => tuple._1)

    /* Sanity check, that there is no ambiguous mapping */
    rawMapping.find(entry => entry._2.length > 1) match {
      case Some(ambiguousEntry) =>
        throw SimbenchDataModelException(
          s"Found ambiguous rated voltages for at least one entry: $ambiguousEntry"
        )
      case None =>
        logger.debug(
          "Great! Found only unambiguous line type to rated voltage mappings."
        )
    }

    /* Mapping the line type to the rated voltage of the first entry of the Vector of each raw mapping. That nothing
     * is missed is ensured by the sanity check beforehand */
    rawMapping.map(rawEntry => rawEntry._1 -> rawEntry._2(0)._2)
  }

  /**
    * Maps the [[LineType]] of the specific line to it's rated voltage based on the line's nodes' rated voltages
    *
    * @param line Specific line to examine
    * @return The rated voltage of the used line type
    */
  private def determineRatedVoltage(
      line: Line[_ <: LineType]
  ): (LineType, ComparableQuantity[ElectricPotential]) = {
    val vRatedA = Quantities.getQuantity(line.nodeA.vmR, KILOVOLT)
    val vRatedB = Quantities.getQuantity(line.nodeB.vmR, KILOVOLT)
    if (vRatedA != vRatedB)
      throw SimbenchDataModelException(
        s"The line ${line.id} connects two nodes with different rated voltages, which physically is not possible"
      )
    (line.lineType, vRatedA)
  }
}
