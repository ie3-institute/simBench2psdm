package edu.ie3.simbench.convert

import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.simbench.model.datamodel.Coordinate
import org.locationtech.jts.geom.{
  GeometryFactory,
  Point,
  Coordinate => JTSCoordinate
}

/**
  * Converts a SimBench coordinate to the needed geometry position of PowerSystemDataModel
  */
case object CoordinateConverter {
  private val geometryFactory: GeometryFactory = new GeometryFactory()

  /**
    * Converts a SimBench coordinate to the needed geometry position of PowerSystemDataModel. If no Coordinate is given,
    * the coordinates of the ie³ institute are taken as fallback
    *
    * @param coordinateOpt Option to a coordinate
    * @return Geo position
    */
  def convert(coordinateOpt: Option[Coordinate]): Point = {
    coordinateOpt match {
      case Some(coordinate) =>
        val geoPosition = geometryFactory.createPoint(
          new JTSCoordinate(coordinate.x.toDouble, coordinate.y.toDouble)
        )
        geoPosition.setSRID(4326) // Use WGS84 reference system.
        geoPosition
      case None => NodeInput.DEFAULT_GEO_POSITION
    }
  }
}
