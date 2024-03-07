package edu.ie3.simbench.convert

import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.result.NodeResult
import edu.ie3.simbench.model.datamodel.NodePFResult
import edu.ie3.util.quantities.PowerSystemUnits.{DEGREE_GEOM, PU}
import tech.units.indriya.quantity.Quantities

import java.time.ZonedDateTime

/** Converts [[NodePFResult]] to [[NodeResult]]
  */
object NodePFResultConverter {
  def convert(
      input: NodePFResult,
      node: NodeInput,
      timeStamp: ZonedDateTime = ZonedDateTime.parse("1970-01-01T00:00:00Z")
  ): NodeResult = {
    new NodeResult(
      timeStamp,
      node.getUuid,
      Quantities.getQuantity(input.vm.doubleValue, PU),
      Quantities.getQuantity(input.va.doubleValue, DEGREE_GEOM)
    )
  }
}
