package edu.ie3.simbench.convert

import com.vividsolutions.jts.geom.{
  GeometryFactory,
  Point,
  Coordinate => JTSCoordinate
}
import edu.ie3.simbench.model.datamodel.Coordinate

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
    val (lat, lon): (Double, Double) = coordinateOpt match {
      case Some(coordinate) => (coordinate.y.toDouble, coordinate.x.toDouble)
      case None             => (51.492689, 7.412262)
    }

    val geoPosition = geometryFactory.createPoint(new JTSCoordinate(lon, lat))
    geoPosition.setSRID(4326) // Use WGS84 reference system.
    geoPosition
  }
}
