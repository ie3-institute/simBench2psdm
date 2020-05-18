package edu.ie3.simbench.convert

import java.util.UUID

import com.typesafe.scalalogging.LazyLogging
import edu.ie3.datamodel.models.OperationTime
import edu.ie3.datamodel.models.input.{
  MeasurementUnitInput,
  NodeInput,
  OperatorInput
}
import edu.ie3.simbench.exception.ConversionException
import edu.ie3.simbench.model.datamodel.Measurement.NodeMeasurement
import edu.ie3.simbench.model.datamodel.enums.MeasurementVariable.{
  ActivePower,
  Current,
  ReactivePower,
  Voltage
}
import edu.ie3.simbench.model.datamodel.{Measurement, Node}

/**
  * Currently not supported:
  *   - LineMeasurement
  *   - TransformerMeasurement
  */
case object MeasurementConverter extends LazyLogging {

  /**
    * Converts a given set of measurement units. If there are two or more measurements at a node, they are joined
    * together.
    *
    * @param measurements Input models to be converted
    * @param nodes        Already known node conversion mapping
    * @return A vector of [[MeasurementUnitInput]]
    */
  def convert(
      measurements: Vector[Measurement],
      nodes: Map[Node, NodeInput]
  ): Vector[MeasurementUnitInput] = {
    /* Filter for node measurements (only supported yet) */
    measurements
      .filter {
        case _: Measurement.NodeMeasurement        => true
        case _: Measurement.LineMeasurement        => false
        case _: Measurement.TransformerMeasurement => false
      }
      .map(_.asInstanceOf[NodeMeasurement])
      /* group the measurements by their nodes */
      .groupMap(_.node)(identity)
      .values
      /* map the measurement groups onto one measurement */
      .map(measurementGroup => {
        val measurement = measurementGroup.head
        val node = nodes.getOrElse(
          measurement.node,
          throw ConversionException(
            s"Cannot find conversion for node ${measurement.node}"
          )
        )
        /* If there are at least two measurements, check if both together serve for functions, that can be joined */
        if (measurementGroup.size > 1) {
          val coveredVariables = measurementGroup.map(_.variable).toSet
          /* If both current and voltage are measured, power can be measured, too */
          val voltageMeasured = coveredVariables.contains(Voltage)
          val (pMeasured, qMeasured) = {
            if (coveredVariables
                  .contains(Current) && coveredVariables.contains(Voltage))
              (true, true)
            else
              (
                coveredVariables.contains(ActivePower),
                coveredVariables.contains(ReactivePower)
              )
          }
          Some(
            new MeasurementUnitInput(
              UUID.randomUUID(),
              measurement.id,
              OperatorInput.NO_OPERATOR_ASSIGNED,
              OperationTime.notLimited(),
              node,
              voltageMeasured,
              voltageMeasured,
              pMeasured,
              qMeasured
            )
          )
        } else {
          convert(measurement, node)
        }
      })
      .map(_.get)
      .toVector
  }

  /**
    * Converts a given measurement device.
    *
    * @param measurement Input model to convert
    * @param node        Node, the measurement will be attached to
    * @return Option on a [[MeasurementUnitInput]]
    */
  def convert(
      measurement: Measurement,
      node: NodeInput
  ): Option[MeasurementUnitInput] =
    measurement match {
      case Measurement.NodeMeasurement(id, _, variable, _, _) =>
        Some(
          new MeasurementUnitInput(
            UUID.randomUUID(),
            id,
            OperatorInput.NO_OPERATOR_ASSIGNED,
            OperationTime.notLimited(),
            node,
            variable == Voltage,
            variable == Voltage,
            variable == ActivePower,
            variable == ReactivePower
          )
        )
      case _: Measurement.LineMeasurement =>
        logger.warn("There is no matching model for LineMeasurement")
        None
      case _: Measurement.TransformerMeasurement =>
        logger.warn("There is no matching model for TransformerMeasurement")
        None
    }
}
