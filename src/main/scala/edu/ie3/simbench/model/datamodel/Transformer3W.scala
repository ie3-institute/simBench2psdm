package edu.ie3.simbench.model.datamodel

import edu.ie3.simbench.model.datamodel.SimbenchModel.SimbenchCompanionObject
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
case class Transformer3W(id: String,
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
                         voltLvl: Int)
    extends EntityModel

case object Transformer3W extends SimbenchCompanionObject[Transformer3W] {

  /**
    * Get an Array of table fields denoting the mapping to the model's attributes
    *
    * @return Array of table headings
    */
  override def getFields: Array[String] = Array.empty[String]
}
