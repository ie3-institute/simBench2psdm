package edu.ie3.simbench.convert.profiles

import edu.ie3.datamodel.models.timeseries.individual.{
  IndividualTimeSeries,
  TimeBasedValue
}
import edu.ie3.datamodel.models.value.PValue
import edu.ie3.simbench.exception.ConversionException
import edu.ie3.simbench.model.datamodel.profiles.ResProfileType
import edu.ie3.util.geo.GeoUtils
import org.locationtech.jts.geom.Coordinate

import edu.ie3.util.quantities.QuantityUtils._

import scala.jdk.CollectionConverters.SetHasAsScala
import scala.jdk.OptionConverters.RichOptional

object ResProfileConverter {
  // default coordinates for two locations
  val hanoverCoordinate: Coordinate =
    GeoUtils.buildCoordinate(52.366667, 9.733333)
  val luebeckCoordinate: Coordinate =
    GeoUtils.buildCoordinate(53.866667, 10.683333)

  /** Determines the maximum feed in of the time series.
    * @param timeSeries
    *   given time series
    * @return
    *   time based value with the maximum feed in
    */
  def findMaxFeedIn(
      timeSeries: IndividualTimeSeries[PValue]
  ): Option[TimeBasedValue[PValue]] =
    timeSeries.getEntries.asScala
      .filter { value =>
        value.getValue.getP.toScala
          .getOrElse(1.asKiloWatt)
          .getValue
          .doubleValue() < 0
      }
      .minByOption { value =>
        value.getValue.getP.toScala
          .getOrElse(0.asKiloWatt)
          .getValue
          .doubleValue()
      }

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
      case ResProfileType.PV2 => luebeckCoordinate // luebeck (first place)
      case ResProfileType.PV3 => hanoverCoordinate // hanover (first place)
      case ResProfileType.PV4 => hanoverCoordinate // hanover (second place)
      case ResProfileType.PV5 => luebeckCoordinate // luebeck (second place)
      case ResProfileType.PV6 => luebeckCoordinate // luebeck (third place)
      case ResProfileType.PV7 => hanoverCoordinate // hanover (second place)
      case ResProfileType.PV8 => hanoverCoordinate // hanover (second place)
      case other =>
        throw ConversionException(
          s"There is no coordinate for the profile type $other."
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
          s"There is no compass direction for the profile type $other."
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
