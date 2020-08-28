package edu.ie3.simbench.convert.types

import java.util.UUID

import edu.ie3.datamodel.models.input.connector.`type`.Transformer2WTypeInput
import edu.ie3.simbench.exception.ConversionException
import edu.ie3.simbench.model.datamodel.enums.BranchElementPort.LV
import edu.ie3.simbench.model.datamodel.types.Transformer2WType
import edu.ie3.util.quantities.dep.PowerSystemUnits.{
  DEGREE_GEOM,
  PU,
  VOLTAMPERE
}
import tec.uom.se.quantity.Quantities
import tec.uom.se.unit.Units.{OHM, SIEMENS, VOLT}

import scala.math.sqrt

/**
  * Not covered
  *   - Phase shifting based on the assembly style of the transformer
  */
case object Transformer2wTypeConverter {

  def convert(
      types: Vector[Transformer2WType]
  ): Map[Transformer2WType, Transformer2WTypeInput] =
    types.map(input => input -> convert(input)).toMap

  /**
    * Converts a singe [[Transformer2WType]] into ie3's [[Transformer2WTypeInput]]
    *
    * @param input  Input model to use
    * @param uuid   UUID to use for the model generation (default: Random UUID)
    * @return       A ie3 [[Transformer2WTypeInput]]
    */
  def convert(
      input: Transformer2WType,
      uuid: UUID = UUID.randomUUID()
  ): Transformer2WTypeInput = {
    val id = input.id
    val tapSide = input.tapside == LV
    val dV = input.dVm
    val dPhi = input.dVa
    val tapNeutr = input.tapNeutr
    val tapMin = input.tapMin
    val tapMax = input.tapMax

    /*
     * Conversion of parameters
     * ------------------------
     * The parameters for the equivalent circuit are given from the perspective of the high voltage side. This can be
     * looked up in the official SimBench documentation:
     * https://simbench.de/wp-content/uploads/2020/01/simbench_documentation_de_1.0.1.pdf
     */
    val sRated = input.sR * 1e6 // Rated apparent power in VA
    val vmHV = input.vmHV * 1e3 // Rated voltage magnitude at high voltage port in V
    val vmLV = input.vmLV * 1e3 // Rated voltage magnitude at low voltage port in V
    val vImp = input.vmImp / 100 * vmHV // Voltage magnitude in short circuit experiment in V
    val pCu = input.pCu * 1e3 // Copper losses in W
    val pFe = input.pFe * 1e3 // Iron losses in W

    /* Short circuit experiment */
    val iRated = sRated / (sqrt(3) * vmHV) // Rated current on high voltage side in Ampere
    val zSc = vImp / (iRated * sqrt(3)) // Short circuit impedance of the total branch in Ohm
    val rSc = pCu / (3 * iRated * iRated) // Short circuit resistance in Ohm
    if (rSc > zSc)
      throw ConversionException(
        s"Cannot convert two winding transformer type $id into ie3 type, as the conversion of short circuit parameters is not possible."
      )
    val xSc = sqrt((zSc * zSc - rSc * rSc).doubleValue) // Short circuit reactance in Ohm

    /* No load experiment */
    val iNoLoad = input.iNoLoad / 100 * iRated // No load current in Ampere
    val vM = vmHV / sqrt(3) // Voltage at the main field admittance in V
    val yNoLoad = iNoLoad / vM // No load admittance in Ohm
    val gNoLoad = pFe / (3 * vM * vM) // No load conductance in Ohm
    if (gNoLoad > yNoLoad)
      throw ConversionException(
        s"Cannot convert two winding transformer type $id into ie3 type, as the conversion of no load parameters is not possible."
      )
    val bNoLoad = sqrt((yNoLoad * yNoLoad - gNoLoad * gNoLoad).doubleValue) // No load susceptance in Ohm

    new Transformer2WTypeInput(
      uuid,
      id,
      Quantities.getQuantity(rSc, OHM),
      Quantities.getQuantity(xSc, OHM),
      Quantities.getQuantity(sRated, VOLTAMPERE),
      Quantities.getQuantity(vmHV, VOLT),
      Quantities.getQuantity(vmLV, VOLT),
      Quantities.getQuantity(gNoLoad, SIEMENS),
      Quantities.getQuantity(bNoLoad, SIEMENS),
      Quantities.getQuantity(dV, PU),
      Quantities.getQuantity(dPhi, DEGREE_GEOM),
      tapSide,
      tapNeutr,
      tapMin,
      tapMax
    )
  }
}
