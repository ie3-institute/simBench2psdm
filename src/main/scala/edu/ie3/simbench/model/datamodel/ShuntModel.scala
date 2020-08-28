/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.simbench.model.datamodel

/**
  * Denoting elements, that are connected to only one [[Node]]
  */
trait ShuntModel extends EntityModel {
  val node: Node
}
