package edu.ie3.simbench.model.datamodel

import edu.ie3.simbench.exception.io.SimbenchDataModelException
import edu.ie3.simbench.model.RawModelData
import edu.ie3.simbench.model.datamodel.SimbenchModel.ID

/**
  * Common attributes to actual entities
  */
trait EntityModel extends SimbenchModel {

  /**
    * Subnet it belongs to
    */
  val subnet: String

  /**
    * Voltage level
    */
  val voltLvl: Int
}

case object EntityModel {

  /**
    * Table field for subnet
    */
  val SUBNET: String = "subnet"

  /**
    * Table field for voltage level
    */
  val VOLT_LVL: String = "voltLvl"

  /**
    * Get the base information, that every entity has (id, subnet, voltage level)
    *
    * @param rawData Raw information to derive base information from
    * @return A tuple of id, subnet and voltage level
    */
  def getBaseInformation(rawData: RawModelData): (String, String, Int) = {
    (rawData.get(ID), rawData.get(SUBNET), rawData.get(VOLT_LVL).toInt)
  }

  /**
    * Extract two nodes with the given ids from a map of node id to node
    *
    * @param nodeId0  Id of the first node to get
    * @param nodeId1  Id of the second node to get
    * @param nodes    Map of node id to node
    * @return         A tuple of two nodes
    */
  def getNodes(nodeId0: String,
               nodeId1: String,
               nodes: Map[String, Node]): (Node, Node) = {
    (nodes.getOrElse(
       nodeId0,
       throw SimbenchDataModelException(s"Cannot find node $nodeId0.")),
     nodes.getOrElse(
       nodeId1,
       throw SimbenchDataModelException(s"Cannot find node $nodeId1.")))
  }
}
