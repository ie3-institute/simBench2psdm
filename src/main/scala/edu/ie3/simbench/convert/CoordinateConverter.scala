package edu.ie3.simbench.convert

import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.simbench.model.datamodel.Coordinate
import edu.ie3.util.geo.GeoUtils
import org.locationtech.jts.geom.{Point, Coordinate => JTSCoordinate}

/** Converts a SimBench coordinate to the needed geometry position of
  * PowerSystemDataModel
  */
case object CoordinateConverter {

  /** Converts a SimBench coordinate to the needed geometry position of
    * PowerSystemDataModel. If no Coordinate is given, the coordinates of the
    * ie3 institute are taken as fallback
    *
    * @param coordinateOpt
    *   Option to a coordinate
    * @return
    *   Geo position
    */
  def convert(coordinateOpt: Option[Coordinate]): Point = {
    coordinateOpt match {
      case Some(coordinate) =>
        GeoUtils.DEFAULT_GEOMETRY_FACTORY.createPoint(
          new JTSCoordinate(coordinate.x.toDouble, coordinate.y.toDouble)
        )
      case None => NodeInput.DEFAULT_GEO_POSITION
    }
  }
}
