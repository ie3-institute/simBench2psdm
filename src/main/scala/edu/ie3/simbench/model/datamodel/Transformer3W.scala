/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.simbench.model.datamodel

import edu.ie3.simbench.exception.io.SimbenchDataModelException
import edu.ie3.simbench.io.HeadLineField
import edu.ie3.simbench.model.RawModelData
import edu.ie3.simbench.model.datamodel.EntityModel.EntityModelCompanionObject
import edu.ie3.simbench.model.datamodel.enums.BranchElementPort
import edu.ie3.simbench.model.datamodel.types.Transformer3WType

/**
  * Three winding transformer model
  *
  * @param id Identifier
  * @param nodeHV [[Node]] of high voltage side
  * @param nodeMV [[Node]] of medium voltage side
  * @param nodeLV [[Node]] of low voltage side
  * @param transformerType Transformer type
  * @param tapPosHV Current tap position, if tap changer is on high voltage side
  * @param tapPosMV Current tap position, if tap changer is on medium voltage side
  * @param tapPosLV Current tap position, if tap changer is on low voltage side
  * @param autoTap Is the transformer able to automatically adapt the tap position?
  * @param autoTapSide The transformer port's nodal voltage magnitude to be regulated (HV, MV or LV)
  * @param loadingMax Maximum permissible loading in %
  * @param substation Substation it belongs to
  * @param subnet Subnet it belongs to
  * @param voltLvl Voltage level
  */
final case class Transformer3W(
    id: String,
    nodeHV: Node,
    nodeMV: Node,
    nodeLV: Node,
    transformerType: Transformer3WType,
    tapPosHV: Int,
    tapPosMV: Int,
    tapPosLV: Int,
    autoTap: Boolean,
    autoTapSide: BranchElementPort,
    loadingMax: BigDecimal,
    substation: Substation,
    subnet: String,
    voltLvl: Int
) extends EntityModel

case object Transformer3W extends EntityModelCompanionObject[Transformer3W] {

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
  override def apply(rawData: RawModelData): Transformer3W =
    throw SimbenchDataModelException(
      s"Currently, the concrete characteristics of the data for ${this.getClass.getSimpleName} are not known, so that" +
        s" the factory method cannot be implemented safely."
    )
}
