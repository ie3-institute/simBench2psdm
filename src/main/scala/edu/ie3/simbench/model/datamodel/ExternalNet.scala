/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.simbench.model.datamodel

import edu.ie3.simbench.exception.io.SimbenchDataModelException
import edu.ie3.simbench.io.HeadLineField
import edu.ie3.simbench.io.HeadLineField.MandatoryField
import edu.ie3.simbench.model.RawModelData
import edu.ie3.simbench.model.datamodel.EntityModel.EntityModelCompanionObject
import edu.ie3.simbench.model.datamodel.enums.CalculationType

/**
  * Model of different types of external nets
  */
sealed trait ExternalNet extends ShuntModel {
  val calculationType: CalculationType
  val dspf: Double
  val pExt: Option[Double]
  val qExt: Option[Double]
}

object ExternalNet extends EntityModelCompanionObject[ExternalNet] {

  /**
    * A simple external net only providing power and some voltage regulation facilities
    *
    * @param id Identifier
    * @param node [[Node]] it is connected to
    * @param calculationType [[CalculationType]] to consider in simulation
    * @param dspf Distributed Slack Power Factor in p.u.
    * @param pExt Active power contribution in MW
    * @param qExt Reactive power contribution in MVAr
    * @param subnet Subnet it belongs to
    * @param voltLvl Voltage level
    */
  final case class Simple(
      id: String,
      node: Node,
      calculationType: CalculationType,
      dspf: Double,
      pExt: Option[Double],
      qExt: Option[Double],
      subnet: String,
      voltLvl: Int
  ) extends ExternalNet

  /**
    * Ward equivalent model of the not detailed modeled external grid
    *
    * @param id Identifier
    * @param node [[Node]] it is connected to
    * @param dspf Distributed Slack Power Factor in p.u.
    * @param pExt Active power contribution in MW
    * @param qExt Reactive power contribution in MVAr
    * @param pWard Active power of the ward model in MW
    * @param qWard Reactive power of the ward model in MVAr
    * @param subnet Subnet it belongs to
    * @param voltLvl Voltage level
    */
  final case class Ward(
      id: String,
      node: Node,
      dspf: Double,
      pExt: Option[Double],
      qExt: Option[Double],
      pWard: Double,
      qWard: Double,
      subnet: String,
      voltLvl: Int
  ) extends ExternalNet {
    override val calculationType: CalculationType = CalculationType.Ward
  }

  /**
    * Extended Ward model of the not detailed modeled external grid
    *
    * @param id Identifier
    * @param node [[Node]] it is connected to
    * @param dspf Distributed Slack Power Factor in p.u.
    * @param pExt Active power contribution in MW
    * @param qExt Reactive power contribution in MVAr
    * @param rWardExtended Resistance of the extended Ward model in Ohm
    * @param xWardExtended Reactance of the extended Ward model in Ohm
    * @param vmWardExtended Voltage magnitude of the extended Ward model in p.u.
    * @param subnet Subnet it belongs to
    * @param voltLvl Voltage level
    */
  final case class WardExtended(
      id: String,
      node: Node,
      dspf: Double,
      pExt: Option[Double],
      qExt: Option[Double],
      rWardExtended: Double,
      xWardExtended: Double,
      vmWardExtended: Double,
      subnet: String,
      voltLvl: Int
  ) extends ExternalNet {
    override val calculationType: CalculationType = CalculationType.WardExtended
  }

  private val NODE = "node"
  private val CALC_TYPE = "calc_type"
  private val DSPF = "dspf"
  private val P_EXT_NET = "pExtNet"
  private val Q_EXT_NET = "qExtNet"
  private val P_WARD_SHUNT = "pWardShunt"
  private val Q_WARD_SHUNT = "qWardShunt"
  private val R_X_WARD = "rXWard"
  private val X_X_WARD = "xXWard"
  private val V_M_X_WARD = "vmXWard"

  /**
    * Get an Array of table fields denoting the mapping to the model's attributes
    *
    * @return Array of table headings
    */
  override def getFields: Array[HeadLineField] =
    Array(
      ID,
      NODE,
      CALC_TYPE,
      DSPF,
      P_EXT_NET,
      Q_EXT_NET,
      P_WARD_SHUNT,
      Q_WARD_SHUNT,
      R_X_WARD,
      X_X_WARD,
      V_M_X_WARD,
      SUBNET,
      VOLT_LVL
    ).map(id => MandatoryField(id))

  /**
    * Factory method to build one model from a mapping from field id to value
    *
    * @param rawData mapping from field id to value
    * @return A model
    */
  override def apply(rawData: RawModelData): ExternalNet =
    throw SimbenchDataModelException(
      s"No basic implementation of model creation available for ${this.getClass.getSimpleName}"
    )

  /**
    * Factory method to build a batch of models from a mapping from field id to value
    *
    * @param rawData  mapping from field id to value
    * @param nodes    Mapping of node ids to actual nodes
    * @return A [[Vector]] of models
    */
  def buildModels(
      rawData: Vector[RawModelData],
      nodes: Map[String, Node]
  ): Vector[ExternalNet] =
    for (entry <- rawData) yield {
      val node = getNode(entry.get(NODE), nodes)
      buildModel(entry, node)
    }

  /**
    * Factory method to build one model from a mapping from field id to value
    *
    * @param rawData  mapping from field id to value
    * @param node     Node at which the external net is connected
    * @return A model
    */
  def buildModel(rawData: RawModelData, node: Node): ExternalNet = {
    val (id, subnet, voltLvl) = getBaseInformation(rawData)
    val calculationType = CalculationType(rawData.get(CALC_TYPE))
    val dspf = rawData.getDouble(DSPF)
    val pExt = rawData.getDoubleOption(P_EXT_NET)
    val qExt = rawData.getDoubleOption(Q_EXT_NET)
    calculationType match {
      case CalculationType.PQ | CalculationType.PVm | CalculationType.VaVm =>
        Simple(id, node, calculationType, dspf, pExt, qExt, subnet, voltLvl)
      case CalculationType.Ward =>
        val pWard = rawData.getDouble(P_WARD_SHUNT)
        val qWard = rawData.getDouble(Q_WARD_SHUNT)
        Ward(id, node, dspf, pExt, qExt, pWard, qWard, subnet, voltLvl)
      case CalculationType.WardExtended =>
        val rWard = rawData.getDouble(R_X_WARD)
        val xWard = rawData.getDouble(X_X_WARD)
        val vWard = rawData.getDouble(V_M_X_WARD)
        WardExtended(
          id,
          node,
          dspf,
          pExt,
          qExt,
          rWard,
          xWard,
          vWard,
          subnet,
          voltLvl
        )
    }
  }
}
