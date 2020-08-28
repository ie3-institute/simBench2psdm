/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.simbench.model.datamodel

import edu.ie3.simbench.exception.io.SimbenchDataModelException
import edu.ie3.simbench.io.HeadLineField
import edu.ie3.simbench.io.HeadLineField.MandatoryField
import edu.ie3.simbench.model.RawModelData
import edu.ie3.simbench.model.datamodel.EntityModel.EntityModelCompanionObject
import edu.ie3.simbench.model.datamodel.enums.MeasurementVariable
import edu.ie3.simbench.model.datamodel.types.LineType

sealed trait Measurement extends EntityModel

object Measurement extends EntityModelCompanionObject[Measurement] {

  /**
    * Measurement device, that is able to measure different information at a node
    *
    * @param id       Identifier
    * @param node     Node at which the variable is measured
    * @param variable The value, that is measured
    * @param subnet   Subnet it belongs to
    * @param voltLvl  Voltage level
    */
  final case class NodeMeasurement(
      id: String,
      node: Node,
      variable: MeasurementVariable,
      subnet: String,
      voltLvl: Int
  ) extends Measurement

  /**
    * Measurement device, that is able to measure different information at a certain node of a line
    *
    * @param id       Identifier
    * @param line     Line, which is measured
    * @param node     Node at which the variable is measured
    * @param variable The value, that is measured
    * @param subnet   Subnet it belongs to
    * @param voltLvl  Voltage level
    */
  final case class LineMeasurement(
      id: String,
      line: Line[_ <: LineType],
      node: Node,
      variable: MeasurementVariable,
      subnet: String,
      voltLvl: Int
  ) extends Measurement

  /**
    * Measurement device, that is able to measure different information at a certain node of a two winding transformer
    *
    * @param id             Identifier
    * @param transformer2W  Transformer, which is measured
    * @param node           Node at which the variable is measured
    * @param variable       The value, that is measured
    * @param subnet         Subnet it belongs to
    * @param voltLvl        Voltage level
    */
  final case class TransformerMeasurement(
      id: String,
      transformer2W: Transformer2W,
      node: Node,
      variable: MeasurementVariable,
      subnet: String,
      voltLvl: Int
  ) extends Measurement

  private val ELEMENT_1 = "element1"
  private val ELEMENT_2 = "element2"
  private val VARIABLE = "variable"

  /**
    * Get an Array of table fields denoting the mapping to the model's attributes
    *
    * @return Array of table headings
    */
  override def getFields: Array[HeadLineField] =
    Array(ID, ELEMENT_1, ELEMENT_2, VARIABLE, SUBNET, VOLT_LVL)
      .map(id => MandatoryField(id))

  /**
    * Factory method to build one model from a mapping from field id to value
    *
    * @param rawData mapping from field id to value
    * @return A model
    */
  override def apply(rawData: RawModelData): Measurement =
    throw SimbenchDataModelException(
      s"No basic implementation of model creation available for ${this.getClass.getSimpleName}"
    )

  /**
    * Factory method to build one model from a mapping from field id to value
    *
    * @param rawData        mapping from field id to value
    * @param nodes          Nodes to use for mapping
    * @param lines          Lines to use for mapping
    * @param transformers2W Transformers to use for mapping
    * @return A model
    */
  def buildModels(
      rawData: Vector[RawModelData],
      nodes: Map[String, Node],
      lines: Map[String, Line[_ <: LineType]],
      transformers2W: Map[String, Transformer2W]
  ): Vector[Measurement] =
    for (entry <- rawData) yield {
      val (id, subnet, voltLvl) = getBaseInformation(entry)
      val node = getNode(entry.get(ELEMENT_1), nodes)
      val variable = MeasurementVariable(entry.get(VARIABLE))
      val element2 = entry.get(ELEMENT_2)
      if (element2.toLowerCase == "null" || element2.isEmpty)
        NodeMeasurement(id, node, variable, subnet, voltLvl)
      else
        lines.get(element2) match {
          case Some(line) =>
            LineMeasurement(id, line, node, variable, subnet, voltLvl)
          case None =>
            transformers2W.get(element2) match {
              case Some(transformer2W) =>
                TransformerMeasurement(
                  id,
                  transformer2W,
                  node,
                  variable,
                  subnet,
                  voltLvl
                )
              case None =>
                throw SimbenchDataModelException(
                  s"The given measurement $id does not comply the assumed scheme. Cannot determine the type of measurement."
                )
            }
        }
    }
}
