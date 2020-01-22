package edu.ie3.simbench.model.datamodel

/**
  * Denoting elements, that are connected to only one [[Node]]
  */
trait ShuntModel extends EntityModel {
  val node: Node
}
