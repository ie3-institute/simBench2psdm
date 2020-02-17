package edu.ie3.simbench.model.datamodel

import edu.ie3.simbench.model.datamodel.SimbenchModel.SimbenchCompanionObject
import edu.ie3.simbench.model.datamodel.enums.CalculationType

/**
  * Model of different types of external nets
  */
sealed trait ExternalNet extends ShuntModel {
  val calculationType: CalculationType
  val dspf: BigDecimal
  val pExt: BigDecimal
  val qExt: BigDecimal
}

object ExternalNet extends SimbenchCompanionObject[ExternalNet] {

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
  case class Simple(id: String,
                    node: Node,
                    calculationType: CalculationType,
                    dspf: BigDecimal,
                    pExt: BigDecimal,
                    qExt: BigDecimal,
                    subnet: String,
                    voltLvl: Int)
      extends ExternalNet

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
  case class Ward(id: String,
                  node: Node,
                  dspf: BigDecimal,
                  pExt: BigDecimal,
                  qExt: BigDecimal,
                  pWard: BigDecimal,
                  qWard: BigDecimal,
                  subnet: String,
                  voltLvl: Int)
      extends ExternalNet {
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
    * @param rWardExtended Resistance of the extended Ward model in Ω
    * @param xWardExtended Reactance of the extended Ward model in Ω
    * @param vmWardExtended Voltage magnitude of the extended Ward model in p.u.
    * @param subnet Subnet it belongs to
    * @param voltLvl Voltage level
    */
  case class WardExtended(id: String,
                          node: Node,
                          dspf: BigDecimal,
                          pExt: BigDecimal,
                          qExt: BigDecimal,
                          rWardExtended: BigDecimal,
                          xWardExtended: BigDecimal,
                          vmWardExtended: BigDecimal,
                          subnet: String,
                          voltLvl: Int)
      extends ExternalNet {
    override val calculationType: CalculationType = CalculationType.WardExtended
  }

  /**
    * Get an Array of table fields denoting the mapping to the model's attributes
    *
    * @return Array of table headings
    */
  override def getFields: Array[String] =
    Array("id",
          "node",
          "calc_type",
          "dspf",
          "pExtNet",
          "qExtNet",
          "pWardShunt",
          "qWardShunt",
          "rXWard",
          "xXWard",
          "vmXWard",
          "subnet",
          "voltLvl")
}
