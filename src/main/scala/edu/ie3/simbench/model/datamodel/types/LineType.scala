package edu.ie3.simbench.model.datamodel.types

import edu.ie3.simbench.io.HeadLineField
import edu.ie3.simbench.io.HeadLineField.MandatoryField
import edu.ie3.simbench.model.RawModelData
import edu.ie3.simbench.model.datamodel.SimbenchModel
import edu.ie3.simbench.model.datamodel.SimbenchModel.SimbenchCompanionObject
import edu.ie3.simbench.model.datamodel.enums.LineStyle

sealed trait LineType extends SimbenchModel

object LineType extends SimbenchCompanionObject[LineType] {

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

  private val R = "r"
  private val X = "x"
  private val B = "b"
  private val I_MAX = "iMax"
  private val LINE_TYPE = "type"

  /**
    * Get an Array of table fields denoting the mapping to the model's attributes
    *
    * @return Array of table headings
    */
  override def getFields: Array[HeadLineField] =
    Array(ID, R, X, B, I_MAX, LINE_TYPE).map(id => MandatoryField(id))

  /**
    * Factory method to build one model from a mapping from field id to value
    *
    * @param rawData mapping from field id to value
    * @return A model
    */
  override def apply(rawData: RawModelData): LineType = {
    val id = rawData.get(ID)
    val r = rawData.getBigDecimal(R)
    val x = rawData.getBigDecimal(X)
    val b = rawData.getBigDecimal(B)
    val iMax = rawData.getBigDecimal(I_MAX)
    val lineStyle = LineStyle(rawData.get(LINE_TYPE))

    /* Normally, here a distinction between AC and DC line has to be made. But until now I did not find a line type file
     * containing the needed scheme for DC lines... */
    ACLineType(id, r, x, b, iMax, lineStyle)
  }
}
