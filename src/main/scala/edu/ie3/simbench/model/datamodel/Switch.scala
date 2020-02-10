package edu.ie3.simbench.model.datamodel

import edu.ie3.simbench.model.datamodel.enums.SwitchType

/**
  * Modeling switches between nodes
  *
  * @param id Identifier
  * @param nodeA Node at end A
  * @param nodeB Node at end B
  * @param switchType Type of the switch
  * @param cond true, if the switch ist closed
  * @param substation Substation it belongs to
  * @param subnet Subnet it belongs to
  * @param voltLvl Voltage level
  */
case class Switch(id: String,
                  nodeA: Node,
                  nodeB: Node,
                  switchType: SwitchType,
                  cond: Boolean,
                  substation: Substation,
                  subnet: String,
                  voltLvl: Int)
    extends EntityModel
