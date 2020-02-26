package edu.ie3.simbench.convert

import java.util.UUID

import edu.ie3.models.OperationTime
import edu.ie3.models.input.{NodeInput, OperatorInput}
import edu.ie3.simbench.model.datamodel.Node.NodeKey
import edu.ie3.simbench.model.datamodel.enums.CalculationType
import edu.ie3.simbench.model.datamodel.enums.CalculationType.{
  VaVm,
  Ward,
  WardExtended
}
import edu.ie3.simbench.model.datamodel.{
  EntityModel,
  ExternalNet,
  Node,
  PowerPlant,
  RES
}
import edu.ie3.util.quantities.PowerSystemUnits.PU
import edu.ie3.util.quantities.PowerSystemUnits.KILOVOLT
import tec.uom.se.quantity.Quantities

/**
  * Not covered:
  *   - node type
  *   - voltage angle set point
  *   - substation information
  */
case object NodeConverter {

  /**
    * Converts a SimBench node to a PowerSystemDataModel node
    *
    * @param input            SimBench [[Node]] to convert
    * @param slackNodeKeys    Vector of keys, undoubtedly identifying slack nodes by (id, subnet, voltLvl)
    * @param subnetConverter  Subnet converter, that is initialized with the apparent SimBench subnets
    * @param givenUuid        Optional UUID to use for the model generation
    * @return                 A [[NodeInput]]
    */
  def convert(input: Node,
              slackNodeKeys: Vector[NodeKey],
              subnetConverter: SubnetConverter,
              givenUuid: Option[UUID] = None): NodeInput = {
    val uuid = givenUuid match {
      case Some(value) => value
      case None        => UUID.randomUUID()
    }
    val vTarget = input.vmSetp match {
      case Some(value) => Quantities.getQuantity(value, PU)
      case None        => Quantities.getQuantity(1d, PU)
    }
    val vRated = Quantities.getQuantity(input.vmR, KILOVOLT)
    val isSlack =
      slackNodeKeys.contains(input.getKey)
    val geopPosition = CoordinateConverter.convert(input.coordinate)
    val voltLvl = VoltLvlConverter.convert(input.voltLvl)
    val subnet = subnetConverter.convert(input.subnet)

    new NodeInput(uuid,
                  OperationTime.notLimited(),
                  OperatorInput.NO_OPERATOR_ASSIGNED,
                  input.id,
                  vTarget,
                  vRated,
                  isSlack,
                  geopPosition,
                  voltLvl,
                  subnet)
  }

  /**
    * Deriving the total list of slack node keys from all possible assets introducing slack simulation mode to a node
    *
    * @param externalNets [[Vector]] of [[ExternalNet]]s
    * @param powerPlants  [[Vector]] of [[PowerPlant]]s
    * @param res          [[Vector]] of [[RES]]s
    * @return             [[Vector]] of distinct slack node keys (id, subnet, voltLvl)
    */
  def getSlackNodeKeys(externalNets: Vector[ExternalNet],
                       powerPlants: Vector[PowerPlant],
                       res: Vector[RES]): Vector[NodeKey] = {
    val calcTypeFromExternalNet = (externalNet: ExternalNet) =>
      externalNet.calculationType
    val calcTypeFromPowerPlant = (powerPlant: PowerPlant) =>
      powerPlant.calculationType
    val calcTypeFromRes = (res: RES) => res.calculationType
    val nodeFromExternalNet = (externalNet: ExternalNet) => externalNet.node
    val nodeFromPowerPlant = (powerPlant: PowerPlant) => powerPlant.node
    val nodeFromRes = (res: RES) => res.node

    (extractSlackNodeKeys(externalNets,
                          calcTypeFromExternalNet,
                          nodeFromExternalNet) ++
      extractSlackNodeKeys(powerPlants,
                           calcTypeFromPowerPlant,
                           nodeFromPowerPlant) ++
      extractSlackNodeKeys(res, calcTypeFromRes, nodeFromRes)).distinct
  }

  /**
    * Extracting the slack node keys from a [[Vector]] of models
    *
    * @param models           [[Vector]] of models
    * @param getCalcTypeFunc  Partial function to extract calculation type from the model
    * @param getNode          Partial function to extract the node from the model
    * @tparam T               Type of the models
    * @return                 A vector of slack node keys
    */
  private def extractSlackNodeKeys[T <: EntityModel](
      models: Vector[T],
      getCalcTypeFunc: T => CalculationType,
      getNode: T => Node): Vector[NodeKey] = {
    val slackCalculationModes = Vector(VaVm, Ward, WardExtended)
    models
      .filter(model => slackCalculationModes.contains(getCalcTypeFunc(model)))
      .map(model => getNode(model).getKey)
  }
}
