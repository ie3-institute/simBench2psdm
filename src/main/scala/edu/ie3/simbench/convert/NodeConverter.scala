package edu.ie3.simbench.convert

import java.util.UUID

import edu.ie3.models.OperationTime
import edu.ie3.models.input.{NodeInput, OperatorInput}
import edu.ie3.simbench.model.datamodel.Node
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
              slackNodeKeys: Vector[(String, String, Int)],
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
      slackNodeKeys.contains((input.id, input.subnet, input.voltLvl))
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
}
