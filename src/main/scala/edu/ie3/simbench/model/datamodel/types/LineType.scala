/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.simbench.model.datamodel.types

import edu.ie3.simbench.io.HeadLineField
import edu.ie3.simbench.io.HeadLineField.MandatoryField
import edu.ie3.simbench.model.RawModelData
import edu.ie3.simbench.model.datamodel.SimbenchModel
import edu.ie3.simbench.model.datamodel.SimbenchModel.SimbenchCompanionObject
import edu.ie3.simbench.model.datamodel.enums.LineStyle

sealed trait LineType extends SimbenchModel

object LineType {

  /**
    * Describing the attributes of a piece of AC line
    *
    * @param id Identifier
    * @param r Relative resistance in Ohm/km
    * @param x Relative reactance in Ohm/km
    * @param b Relative susceptance in microS/km
    * @param iMax Maximum permissible current in A
    * @param style The construction type of the line
    */
  final case class ACLineType(
      id: String,
      r: Double,
      x: Double,
      b: Double,
      iMax: Double,
      style: LineStyle
  ) extends LineType

  case object ACLineType extends SimbenchCompanionObject[ACLineType] {
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
    override def apply(rawData: RawModelData): ACLineType = {
      val id = rawData.get(ID)
      val r = rawData.getDouble(R)
      val x = rawData.getDouble(X)
      val b = rawData.getDouble(B)
      val iMax = rawData.getDouble(I_MAX)
      val lineStyle = LineStyle(rawData.get(LINE_TYPE))

      ACLineType(id, r, x, b, iMax, lineStyle)
    }
  }

  /**
    * Denoting the type of a DC line
    *
    * @param id Identifier
    * @param p Active power setpoint of the line in MW
    * @param relPLoss Relative active power losses in %
    * @param fixPLoss Fix active power losses in MW
    * @param pMax Maximum permissible active power to be transferred in MW (optional)
    * @param qMinA Minimum permissible reactive power provision at port A in MVAr (optional)
    * @param qMaxA Maximum permissible reactive power provision at port A in MVAr (optional)
    * @param qMinB Minimum permissible reactive power provision at port B in MVAr (optional)
    * @param qMaxB Maximum permissible reactive power provision at port B in MVAr (optional)
    */
  final case class DCLineType(
      id: String,
      p: Double,
      relPLoss: Double,
      fixPLoss: Double,
      pMax: Option[Double] = None,
      qMinA: Option[Double] = None,
      qMaxA: Option[Double] = None,
      qMinB: Option[Double] = None,
      qMaxB: Option[Double] = None
  ) extends LineType

  case object DCLineType extends SimbenchCompanionObject[DCLineType] {
    private val P = "pDCLine"
    private val REL_P_LOSS = "relPLosses"
    private val FIX_P_LOSS = "fixPLosses"
    private val P_MAX = "pMax"
    private val Q_MIN_A = "qMinA"
    private val Q_MIN_B = "qMinB"
    private val Q_MAX_A = "qMaxA"
    private val Q_MAX_B = "qMaxB"

    /**
      * Get an Array of table fields denoting the mapping to the model's attributes
      *
      * @return Array of table headings
      */
    override def getFields: Array[HeadLineField] =
      Array(
        ID,
        P,
        REL_P_LOSS,
        FIX_P_LOSS,
        P_MAX,
        Q_MIN_A,
        Q_MIN_B,
        Q_MAX_A,
        Q_MAX_B
      ).map(id => MandatoryField(id))

    /**
      * Factory method to build one model from a mapping from field id to value
      *
      * @param rawData mapping from field id to value
      * @return A model
      */
    override def apply(rawData: RawModelData): DCLineType = {
      val id = rawData.get(ID)
      val p = rawData.getDouble(P)
      val relPLoss = rawData.getDouble(REL_P_LOSS)
      val fixPLoss = rawData.getDouble(FIX_P_LOSS)
      val pMax = rawData.getDoubleOption(P_MAX)
      val qMinA = rawData.getDoubleOption(Q_MIN_A)
      val qMaxA = rawData.getDoubleOption(Q_MAX_A)
      val qMinB = rawData.getDoubleOption(Q_MIN_B)
      val qMaxB = rawData.getDoubleOption(Q_MAX_B)

      DCLineType(id, p, relPLoss, fixPLoss, pMax, qMinA, qMaxA, qMinB, qMaxB)
    }
  }
}
