package edu.ie3.simbench.model.datamodel.types

import edu.ie3.simbench.exception.io.SimbenchDataModelException
import edu.ie3.simbench.io.HeadLineField
import edu.ie3.simbench.model.RawModelData
import edu.ie3.simbench.model.datamodel.SimbenchModel
import edu.ie3.simbench.model.datamodel.SimbenchModel.SimbenchCompanionObject
import edu.ie3.simbench.model.datamodel.enums.BranchElementPort

/**
  * Modeling a type of a two winding transformer
  *
  * @param id Identifier
  * @param sRHV Rated apparent power at high voltage side in MVA
  * @param sRMV Rated apparent power at medium voltage side in MVA
  * @param sRLV Rated apparent power at low voltage side in MVA
  * @param vmHV Rated voltage magnitude at high voltage side in kV
  * @param vmMV Rated voltage magnitude at medium voltage side in kV
  * @param vmLV Rated voltage magnitude at low voltage site in kV
  * @param vaHVMV Voltage angle deviation between high and medium voltage side in Degree
  * @param vaHVLV Voltage angle deviation between high and low voltage side in Degree
  * @param vmImpHV Voltage magnitude in short circuit experiment on high voltage side in %
  * @param vmImpMV Voltage magnitude in short circuit experiment on medium voltage side in %
  * @param vmImpLV Voltage magnitude in short circuit experiment on low voltage side in %
  * @param pCuHV Copper losses in short circuit experiment on high voltage side in kW
  * @param pCuMV Copper losses in short circuit experiment on medium voltage side in kW
  * @param pCuLV Copper losses in short circuit experiment on low voltage side in kW
  * @param pFe Iron losses in no load experiment in kW
  * @param iNoLoad No load current in no load experiment in %
  * @param tapable Is the transformer tapable?
  * @param tapSide On which side is the tap changer installed?
  * @param dVmHV Voltage magnitude increase per step, if the tap changer is on high voltage side in % / tap
  * @param dVmMV Voltage magnitude increase per step, if the tap changer is on medium voltage side in % / tap
  * @param dVmLV Voltage magnitude increase per step, if the tap changer is on low voltage side in % / tap
  * @param dVaHV Voltage angle increase per step, if the tap changer is on high voltage side in % / tap
  * @param dVaMV Voltage angle increase per step, if the tap changer is on medium voltage side in % / tap
  * @param dVaLV Voltage angle increase per step, if the tap changer is on low voltage side in % / tap
  * @param tapNeutrHV Neutral tap position, if the tap changer is on high voltage side
  * @param tapNeutrMV Neutral tap position, if the tap changer is on medium voltage side
  * @param tapNeutrLV Neutral tap position, if the tap changer is on low voltage side
  * @param tapMinHV  Minimum permissible tap position, if the tap changer is on high voltage side
  * @param tapMinMV  Minimum permissible tap position, if the tap changer is on medium voltage side
  * @param tapMinLV  Minimum permissible tap position, if the tap changer is on medium voltage side
  * @param tapMaxHV  Maximum permissible tap position, if the tap changer is on high voltage side
  * @param tapMaxMV  Maximum permissible tap position, if the tap changer is on medium voltage side
  * @param tapMaxLV  Maximum permissible tap position, if the tap changer is on low voltage side
  */
case class Transformer3WType(
    id: String,
    sRHV: BigDecimal,
    sRMV: BigDecimal,
    sRLV: BigDecimal,
    vmHV: BigDecimal,
    vmMV: BigDecimal,
    vmLV: BigDecimal,
    vaHVMV: BigDecimal,
    vaHVLV: BigDecimal,
    vmImpHV: BigDecimal,
    vmImpMV: BigDecimal,
    vmImpLV: BigDecimal,
    pCuHV: BigDecimal,
    pCuMV: BigDecimal,
    pCuLV: BigDecimal,
    pFe: BigDecimal,
    iNoLoad: BigDecimal,
    tapable: Boolean,
    tapSide: BranchElementPort,
    dVmHV: BigDecimal,
    dVmMV: BigDecimal,
    dVmLV: BigDecimal,
    dVaHV: BigDecimal,
    dVaMV: BigDecimal,
    dVaLV: BigDecimal,
    tapNeutrHV: Int,
    tapNeutrMV: Int,
    tapNeutrLV: Int,
    tapMinHV: Int,
    tapMinMV: Int,
    tapMinLV: Int,
    tapMaxHV: Int,
    tapMaxMV: Int,
    tapMaxLV: Int
) extends SimbenchModel

case object Transformer3WType
    extends SimbenchCompanionObject[Transformer3WType] {

  /**
    * Get an Array of table fields denoting the mapping to the model's attributes
    *
    * @return Array of table headings
    */
  override def getFields: Array[HeadLineField] = Array.empty[HeadLineField]

  /**
    * Factory method to build one model from a mapping from field id to value
    *
    * @param rawData mapping from field id to value
    * @return A model
    */
  override def apply(rawData: RawModelData): Transformer3WType =
    throw SimbenchDataModelException(
      s"Currently, the concrete characteristics of the data for ${this.getClass.getSimpleName} are not known, so that" +
        s" the factory method cannot be implemented safely."
    )
}
