package edu.ie3.simbench.model.datamodel.types

import edu.ie3.simbench.model.RawModelData
import edu.ie3.simbench.model.datamodel.SimbenchModel
import edu.ie3.simbench.model.datamodel.SimbenchModel.SimbenchCompanionObject
import edu.ie3.simbench.model.datamodel.enums.BranchElementPort

/**
  * Modeling a type of a two winding transformer
  *
  * @param id Identifier
  * @param sR Rated apparent power in MVA
  * @param vmHV Rated voltage magnitude at high voltage side
  * @param vmLV Rated voltage magnitude at low voltage side
  * @param va0 Phase shifting based on the assembly style of the transformer (e.g. Dy5 => 150°) in °
  * @param vmImp Relative voltage magnitude in the short circuit experiment in %
  * @param pCu Copper losses in short circuit experiment in kW
  * @param pFe Iron losses in no load experiment in kW
  * @param iNoLoad No load current in no load experiment in %
  * @param tapable Is the transformer tapable?
  * @param tapside Winding at which side the transformer is installed
  * @param dVm Voltage magnitude increase per step in p.u. / step
  * @param dVa Voltage angle increase per step in ° / step
  * @param tapNeutr Neutral tap position
  * @param tapMin Minimum permissible tap position
  * @param tapMax Maximum permissible tap position
  */
case class Transformer2WType(id: String,
                             sR: BigDecimal,
                             vmHV: BigDecimal,
                             vmLV: BigDecimal,
                             va0: BigDecimal,
                             vmImp: BigDecimal,
                             pCu: BigDecimal,
                             pFe: BigDecimal,
                             iNoLoad: BigDecimal,
                             tapable: Boolean,
                             tapside: BranchElementPort,
                             dVm: BigDecimal,
                             dVa: BigDecimal,
                             tapNeutr: Int,
                             tapMin: Int,
                             tapMax: Int)
    extends SimbenchModel

case object Transformer2WType
    extends SimbenchCompanionObject[Transformer2WType] {
  val S_RATED = "sR"
  val V_M_HV = "vmHV"
  val V_M_LV = "vmLV"
  val V_A_0 = "va0"
  val V_M_IMP = "vmImp"
  val P_CU = "pCu"
  val P_FE = "pFe"
  val I_NO_LOAD = "iNoLoad"
  val TAPPABLE = "tapable"
  val TAP_SIDE = "tapside"
  val D_V_M = "dVm"
  val D_V_A = "dVa"
  val TAP_NEUTR = "tapNeutr"
  val TAP_MIN = "tapMin"
  val TAP_MAX = "tapMax"

  /**
    * Get an Array of table fields denoting the mapping to the model's attributes
    *
    * @return Array of table headings
    */
  override def getFields: Array[String] =
    Array(SimbenchModel.ID,
          S_RATED,
          V_M_HV,
          V_M_LV,
          V_A_0,
          V_M_IMP,
          P_CU,
          P_FE,
          I_NO_LOAD,
          TAPPABLE,
          TAP_SIDE,
          D_V_M,
          D_V_A,
          TAP_NEUTR,
          TAP_MIN,
          TAP_MAX)

  /**
    * Factory method to build one model from a mapping from field id to value
    *
    * @param rawData mapping from field id to value
    * @return A model
    */
  override def buildModel(rawData: RawModelData): Transformer2WType = {
    val id = rawData.get(SimbenchModel.ID)
    val sRated = BigDecimal(rawData.get(S_RATED))
    val vMHv = BigDecimal(rawData.get(V_M_HV))
    val vMLv = BigDecimal(rawData.get(V_M_LV))
    val vA0 = BigDecimal(rawData.get(V_A_0))
    val vMImp = BigDecimal(rawData.get(V_M_IMP))
    val pCu = BigDecimal(rawData.get(P_CU))
    val pFe = BigDecimal(rawData.get(P_FE))
    val iNoLoad = BigDecimal(rawData.get(I_NO_LOAD))
    val tappable = rawData.get(TAPPABLE) == "1"
    val tapSide = BranchElementPort(rawData.get(TAP_SIDE))
    val dVm = BigDecimal(rawData.get(D_V_M))
    val dVa = BigDecimal(rawData.get(D_V_A))
    val tapNeutr = rawData.get(TAP_NEUTR).toInt
    val tapMin = rawData.get(TAP_MIN).toInt
    val tapMax = rawData.get(TAP_MAX).toInt

    Transformer2WType(id,
                      sRated,
                      vMHv,
                      vMLv,
                      vA0,
                      vMImp,
                      pCu,
                      pFe,
                      iNoLoad,
                      tappable,
                      tapSide,
                      dVm,
                      dVa,
                      tapNeutr,
                      tapMin,
                      tapMax)
  }
}
