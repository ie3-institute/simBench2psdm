package edu.ie3.simbench.model.datamodel.types

import edu.ie3.simbench.io.HeadLineField
import edu.ie3.simbench.io.HeadLineField.MandatoryField
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
  * @param va0 Phase shifting based on the assembly style of the transformer (e.g. Dy5 => 150Degree) in Degree
  * @param vmImp Relative voltage magnitude in the short circuit experiment in %
  * @param pCu Copper losses in short circuit experiment in kW
  * @param pFe Iron losses in no load experiment in kW
  * @param iNoLoad No load current in no load experiment in %
  * @param tapable Is the transformer tapable?
  * @param tapside Winding at which side the transformer is installed
  * @param dVm Voltage magnitude increase per step in p.u. / step
  * @param dVa Voltage angle increase per step in Degree / step
  * @param tapNeutr Neutral tap position
  * @param tapMin Minimum permissible tap position
  * @param tapMax Maximum permissible tap position
  */
case class Transformer2WType(
    id: String,
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
    tapMax: Int
) extends SimbenchModel

case object Transformer2WType
    extends SimbenchCompanionObject[Transformer2WType] {
  private val S_RATED = "sR"
  private val V_M_HV = "vmHV"
  private val V_M_LV = "vmLV"
  private val V_A_0 = "va0"
  private val V_M_IMP = "vmImp"
  private val P_CU = "pCu"
  private val P_FE = "pFe"
  private val I_NO_LOAD = "iNoLoad"
  private val TAPPABLE = "tapable"
  private val TAP_SIDE = "tapside"
  private val D_V_M = "dVm"
  private val D_V_A = "dVa"
  private val TAP_NEUTR = "tapNeutr"
  private val TAP_MIN = "tapMin"
  private val TAP_MAX = "tapMax"

  /**
    * Get an Array of table fields denoting the mapping to the model's attributes
    *
    * @return Array of table headings
    */
  override def getFields: Array[HeadLineField] =
    Array(
      ID,
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
      TAP_MAX
    ).map(id => MandatoryField(id))

  /**
    * Factory method to build one model from a mapping from field id to value
    *
    * @param rawData mapping from field id to value
    * @return A model
    */
  override def apply(rawData: RawModelData): Transformer2WType = {
    val id = rawData.get(ID)
    val sRated = rawData.getBigDecimal(S_RATED)
    val vMHv = rawData.getBigDecimal(V_M_HV)
    val vMLv = rawData.getBigDecimal(V_M_LV)
    val vA0 = rawData.getBigDecimal(V_A_0)
    val vMImp = rawData.getBigDecimal(V_M_IMP)
    val pCu = rawData.getBigDecimal(P_CU)
    val pFe = rawData.getBigDecimal(P_FE)
    val iNoLoad = rawData.getBigDecimal(I_NO_LOAD)
    val tappable = rawData.getBoolean(TAPPABLE)
    val tapSide = BranchElementPort(rawData.get(TAP_SIDE))
    val dVm = rawData.getBigDecimal(D_V_M)
    val dVa = rawData.getBigDecimal(D_V_A)
    val tapNeutr = rawData.getInt(TAP_NEUTR)
    val tapMin = rawData.getInt(TAP_MIN)
    val tapMax = rawData.getInt(TAP_MAX)

    Transformer2WType(
      id,
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
      tapMax
    )
  }
}
