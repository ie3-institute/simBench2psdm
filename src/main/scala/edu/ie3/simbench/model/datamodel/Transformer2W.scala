package edu.ie3.simbench.model.datamodel

import edu.ie3.simbench.model.datamodel.SimbenchModel.SimbenchCompanionObject
import edu.ie3.simbench.model.datamodel.enums.BranchElementPort
import edu.ie3.simbench.model.datamodel.types.Transformer2WType

/**
  * Model of a two winding transformer
  *
  * @param id Identifier
  * @param nodeHV Node at the high voltage end of the transformer
  * @param nodeLV Node at the low voltage end of the transformer
  * @param transformerType Type of the transformer
  * @param tappos Current tap position
  * @param autoTap Is the transformer able to automatically adapt the tap position?
  * @param autoTapSide The transformer port's nodal voltage magnitude to be regulated (HV or LV)
  * @param loadingMax Maximum permissible loading in %
  * @param substation Substation it belongs to
  * @param subnet Subnet it belongs to
  * @param voltLvl Voltage level
  */
case class Transformer2W(id: String,
                         nodeHV: Node,
                         nodeLV: Node,
                         transformerType: Transformer2WType,
                         tappos: Int,
                         autoTap: Boolean,
                         autoTapSide: BranchElementPort,
                         loadingMax: BigDecimal,
                         substation: Substation,
                         subnet: String,
                         voltLvl: Int)
    extends EntityModel

case object Transformer2W extends SimbenchCompanionObject[Transformer2W] {
  /**
    * Get an Array of table fields denoting the mapping to the model's attributes
    *
    * @return Array of table headings
    */
  override def getFields: Array[String] = Array("id", "nodeHV", "nodeLV", "type", "tappos", "autoTap", "autoTapSide", "loadingMax", "substation", "subnet", "voltLvl")
}
