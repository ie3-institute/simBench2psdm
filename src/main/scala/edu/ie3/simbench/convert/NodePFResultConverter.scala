package edu.ie3.simbench.convert

import java.time.ZonedDateTime
import java.util.UUID

import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.result.NodeResult
import edu.ie3.simbench.model.datamodel.NodePFResult
import edu.ie3.util.quantities.dep.PowerSystemUnits.{PU, DEGREE_GEOM}
import tec.uom.se.quantity.Quantities

/**
  * Converts [[NodePFResult]] to [[NodeResult]]
  */
object NodePFResultConverter {
  def convert(
      input: NodePFResult,
      node: NodeInput,
      uuid: UUID = UUID.randomUUID(),
      timeStamp: ZonedDateTime = ZonedDateTime.parse("1970-01-01T00:00:00Z")
  ): NodeResult = {
    new NodeResult(
      uuid,
      timeStamp,
      node.getUuid,
      Quantities.getQuantity(input.vm.doubleValue, PU),
      Quantities.getQuantity(input.va.doubleValue, DEGREE_GEOM)
    )
  }
}
