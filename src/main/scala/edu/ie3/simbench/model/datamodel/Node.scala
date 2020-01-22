package edu.ie3.simbench.model.datamodel

import edu.ie3.simbench.model.datamodel.enums.NodeType

/**
  * Electrical node
  *
  * @param id Identifier
  * @param nodeType Type of the node
  * @param vmSetp Setpoint for the voltage magnitude in p.u.
  * @param vaSetp Setpoint for the voltage angle in ° (Optional)
  * @param vmR Rated voltage magnitude in kV
  * @param vmMin Minimum permissible voltage magnitude in p.u.
  * @param vmMax Maximum permissible voltage magnitude in p.u.
  * @param substation Substation the node belongs to
  * @param coordinate Coordinate at which it is located
  * @param subnet Subnet it belongs to
  * @param voltLvl Voltage level
  */
case class Node(id: String,
                nodeType: NodeType,
                vmSetp: BigDecimal,
                vaSetp: Option[BigDecimal] = None,
                vmR: BigDecimal,
                vmMin: BigDecimal,
                vmMax: BigDecimal,
                substation: Substation,
                coordinate: Coordinate,
                subnet: String,
                voltLvl: Int)
    extends EntityModel
