package edu.ie3.simbench.model.datamodel.types

import edu.ie3.simbench.model.datamodel.SimbenchModel
import edu.ie3.simbench.model.datamodel.enums.LineStyle

sealed trait LineType extends SimbenchModel

object LineType {

  /**
    * Describing the attributes of a piece of AC line
    *
    * @param id Identifier
    * @param r Relative resistance in Ω/km
    * @param x Relative reactance in Ω/km
    * @param b Relative susceptance in µS/km
    * @param iMax Maximum permissible current in A
    * @param style The construction type of the line
    */
  case class ACLineType(id: String,
                        r: BigDecimal,
                        x: BigDecimal,
                        b: BigDecimal,
                        iMax: BigDecimal,
                        style: LineStyle)
      extends LineType

  /**
    * Denoting the type of a DC line
    *
    * @param id Identifier
    * @param p Active power setpoint of the line in MW
    * @param relPLoss Relative active power losses in %
    * @param fixPLoss Fix active power losses in MW
    * @param pMax Maximum permissible active power to be transferred in MW
    * @param qMinA Minimum permissible reactive power provision at port A in MVAr
    * @param qMaxA Maximum permissible reactive power provision at port A in MVAr
    * @param qMinB Minimum permissible reactive power provision at port B in MVAr
    * @param qMaxB Maximum permissible reactive power provision at port B in MVAr
    */
  case class DCLineType(id: String,
                        p: BigDecimal,
                        relPLoss: BigDecimal,
                        fixPLoss: BigDecimal,
                        pMax: BigDecimal,
                        qMinA: BigDecimal,
                        qMaxA: BigDecimal,
                        qMinB: BigDecimal,
                        qMaxB: BigDecimal)
      extends LineType
}
