package edu.ie3.simbench.model.datamodel.enums

import edu.ie3.simbench.exception.io.SimbenchDataModelException

/**
  * "Enum like" sealed trait to list all possible node types
  */
sealed trait NodeType

object NodeType {

  /**
    * An actual bus bar
    */
  object BusBar extends NodeType

  /**
    * A double bus bar
    */
  object DoubleBusBar extends NodeType

  /**
    * A simple node like a bushing
    */
  object Node extends NodeType

  /**
    * Somewhat an artificial, auxiliary node
    */
  object Auxiliary extends NodeType

  /**
    * Hands back a suitable [[NodeType]] based on the entries given in SimBench csv files
    *
    * @param typeString Entry in csv file
    * @return The matching [[NodeType]]
    * @throws SimbenchDataModelException if a non valid type string has been provided
    */
  @throws[SimbenchDataModelException]
  def apply(typeString: String): NodeType =
    typeString.toLowerCase.replaceAll("[_]*", "") match {
      case "busbar"        => BusBar
      case "double busbar" => DoubleBusBar
      case "node"          => Node
      case "auxiliary"     => Auxiliary
      case whatever =>
        throw SimbenchDataModelException(
          s"I cannot handle the node type $whatever"
        )
    }
}
