package edu.ie3.simbench.model.datamodel

import edu.ie3.simbench.exception.io.SimbenchDataModelException
import edu.ie3.simbench.model.RawModelData
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
                         autoTapSide: Option[BranchElementPort],
                         loadingMax: BigDecimal,
                         substation: Option[Substation],
                         subnet: String,
                         voltLvl: Int)
    extends EntityModel

case object Transformer2W extends SimbenchCompanionObject[Transformer2W] {

  val NODE_HV = "nodeHV"
  val NODE_LV = "nodeLV"
  val TYPE = "type"
  val TAPPOS = "tappos"
  val AUTOTAP = "autoTap"
  val AUTOTAP_SIDE = "autoTapSide"
  val LOADING_MAX = "loadingMax"
  val SUBSTATION = "substation"

  /**
    * Get an Array of table fields denoting the mapping to the model's attributes
    *
    * @return Array of table headings
    */
  override def getFields: Array[String] =
    Array(SimbenchModel.ID,
          NODE_HV,
          NODE_LV,
          TYPE,
          TAPPOS,
          AUTOTAP,
          AUTOTAP_SIDE,
          LOADING_MAX,
          SUBSTATION,
          EntityModel.SUBNET,
          EntityModel.VOLT_LVL)

  /**
    * Factory method to build one model from a mapping from field id to value
    *
    * @param rawData mapping from field id to value
    * @return A model
    */
  override def buildModel(rawData: RawModelData): Transformer2W =
    throw SimbenchDataModelException(
      s"No basic implementation of model creation available for ${this.getClass.getSimpleName}")

  /**
    * Factory method to build a batch of models from a mapping from field id to value
    *
    * @param rawData          mapping from field id to value
    * @param nodes            Nodes to use for mapping
    * @param transformerTypes Transformer types to use for mapping
    * @param substations      Substations to use for mapping
    * @return A [[Vector]] of models
    */
  def buildModels(rawData: Vector[RawModelData],
                  nodes: Map[String, Node],
                  transformerTypes: Map[String, Transformer2WType],
                  substations: Map[String, Substation]): Vector[Transformer2W] =
    for (entry <- rawData) yield {
      val (nodeHv, nodeLv) =
        EntityModel.getNodes(entry.get(NODE_HV), entry.get(NODE_LV), nodes)
      val transformerType = transformerTypes.getOrElse(
        entry.get(TYPE),
        throw SimbenchDataModelException(
          s"Cannot build ${this.getClass.getSimpleName}, as suitable reference to $TYPE cannot be found."))
      val substation = substations.get(entry.get(SUBSTATION))
      buildModel(entry, nodeHv, nodeLv, transformerType, substation)
    }

  /**
    * Factory method to build one model from a mapping from field id to value
    *
    * @param rawData          mapping from field id to value
    * @param nodeHv           Node at port hv
    * @param nodeLv           Node at port lv
    * @param transformerType  Transformer type to use
    * @param substation       Substation to use
    * @return A model
    */
  def buildModel(rawData: RawModelData,
                 nodeHv: Node,
                 nodeLv: Node,
                 transformerType: Transformer2WType,
                 substation: Option[Substation]): Transformer2W = {
    val (id, subnet, voltLvl) = EntityModel.getBaseInformation(rawData)
    val tappos = rawData.get(TAPPOS).toInt
    val autoTap = rawData.getBoolean(AUTOTAP)
    val autoTapSide = rawData.get(AUTOTAP_SIDE).toLowerCase() match {
      case "hv" => Some(BranchElementPort.HV)
      case "lv" => Some(BranchElementPort.LV)
      case _    => None
    }
    val loadingMax = BigDecimal(rawData.get(LOADING_MAX))

    Transformer2W(id,
                  nodeHv,
                  nodeLv,
                  transformerType,
                  tappos,
                  autoTap,
                  autoTapSide,
                  loadingMax,
                  substation,
                  subnet,
                  voltLvl)
  }
}
