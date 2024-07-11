package edu.ie3.simbench.convert.profiles

import edu.ie3.simbench.exception.ConversionException
import edu.ie3.simbench.model.datamodel.profiles.ResProfileType
import edu.ie3.util.geo.GeoUtils
import org.locationtech.jts.geom.Coordinate

object ResProfileConverter {
  // default coordinates for two locations
  private val hanoverCoordinate: Coordinate =
    GeoUtils.buildCoordinate(52.366667, 9.733333)
  private val luebeckCoordinate: Coordinate =
    GeoUtils.buildCoordinate(53.866667, 10.683333)

  /** Determines the [[Coordinate]] of a
    * [[edu.ie3.simbench.model.datamodel.RES]] based on its [[ResProfileType]]
    * @param profileType
    *   that can be used to determine the position
    * @return
    *   the coordinate
    */
  def getCoordinate(profileType: ResProfileType): Coordinate =
    profileType match {
      case ResProfileType.PV1 => hanoverCoordinate // hanover (first place)
      case ResProfileType.PV2 => luebeckCoordinate // lübeck (first place)
      case ResProfileType.PV3 => hanoverCoordinate // hanover (first place)
      case ResProfileType.PV4 => hanoverCoordinate // hanover (second place)
      case ResProfileType.PV5 => luebeckCoordinate // lübeck (second place)
      case ResProfileType.PV6 => luebeckCoordinate // lübeck (third place)
      case ResProfileType.PV7 => hanoverCoordinate // hanover (second place)
      case ResProfileType.PV8 => hanoverCoordinate // hanover (second place)
      case other =>
        throw ConversionException(
          s"There are no coordinates for the profile type $other."
        )
    }

  /** Determines the [[CompassDirection]] of a
    * [[edu.ie3.simbench.model.datamodel.RES]] based on its [[ResProfileType]]
    * @param profileType
    *   that can be used to determine the orientation
    * @return
    *   the orientation
    */
  def getCompassDirection(profileType: ResProfileType): CompassDirection.Value =
    profileType match {
      case ResProfileType.PV1 | ResProfileType.PV2 => CompassDirection.East
      case ResProfileType.PV3 | ResProfileType.PV4 | ResProfileType.PV5 |
          ResProfileType.PV6 =>
        CompassDirection.South
      case ResProfileType.PV7 | ResProfileType.PV8 => CompassDirection.West
      case other =>
        throw ConversionException(
          s"There are no coordinates for the profile type $other."
        )
    }

  /** Orientation that can be north, east, south and west.
    */
  final case object CompassDirection extends Enumeration {
    val North: Value = Value("north")
    val East: Value = Value("east")
    val South: Value = Value("south")
    val West: Value = Value("west")
  }
}
