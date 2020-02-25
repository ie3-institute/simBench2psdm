package edu.ie3.simbench.convert

import edu.ie3.models.GermanVoltageLevel._
import edu.ie3.models.VoltageLevel
import edu.ie3.simbench.model.GermanTransformationVoltageLevel.{
  EHV2HV,
  HV2MV,
  MV2LV
}

case object VoltLvlConverter {
  val mapping: Map[Int, VoltageLevel] = Map(
    1 -> EHV,
    2 -> EHV2HV,
    3 -> HV,
    4 -> HV2MV,
    5 -> MV,
    6 -> MV2LV,
    7 -> LV
  )

  def convert(simbenchVoltLvl: Int): VoltageLevel =
    mapping.getOrElse(
      simbenchVoltLvl,
      throw new IllegalArgumentException(
        s"The desired voltage level id $simbenchVoltLvl is not covered by the commonly known voltage levels.")
    )
}
