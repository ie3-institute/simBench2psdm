package edu.ie3.simbench.convert

import edu.ie3.datamodel.models.voltagelevels.{
  GermanVoltageLevelUtils,
  VoltageLevel
}
import javax.measure.quantity.ElectricPotential
import tech.units.indriya.ComparableQuantity

case object VoltLvlConverter {
  val mapping: Map[Int, String] = Map(
    1 -> "ehv",
    2 -> "ehv2hv",
    3 -> "hv",
    4 -> "hv2mv",
    5 -> "mv",
    6 -> "mv2lv",
    7 -> "lv"
  )

  /** Converts the given SimBench voltLvl id in conjunction with the rated
    * voltage to a voltage level object
    *
    * @param simbenchVoltLvl
    *   SimBench voltage level id
    * @param vRated
    *   Rated voltage
    * @return
    *   Voltage level object
    */
  def convert(
      simbenchVoltLvl: Int,
      vRated: ComparableQuantity[ElectricPotential]
  ): VoltageLevel = {
    val id = mapping.getOrElse(
      simbenchVoltLvl,
      throw new IllegalArgumentException(
        s"The desired voltage level id $simbenchVoltLvl is not covered by the commonly known voltage levels."
      )
    )

    GermanVoltageLevelUtils.parse(id, vRated)
  }
}
