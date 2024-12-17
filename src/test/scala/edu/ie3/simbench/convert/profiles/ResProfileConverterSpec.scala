package edu.ie3.simbench.convert.profiles

import edu.ie3.datamodel.models.timeseries.individual.{
  IndividualTimeSeries,
  TimeBasedValue
}
import edu.ie3.datamodel.models.value.PValue
import edu.ie3.simbench.convert.profiles.ResProfileConverter.{
  CompassDirection,
  findMaxFeedIn,
  hanoverCoordinate,
  luebeckCoordinate
}
import edu.ie3.simbench.model.datamodel.profiles.ResProfileType
import edu.ie3.test.common.UnitSpec
import edu.ie3.util.quantities.QuantityUtils._
import org.scalatest.prop.TableDrivenPropertyChecks

import java.time.ZonedDateTime
import scala.jdk.CollectionConverters._
import scala.util.{Failure, Success, Try}

class ResProfileConverterSpec extends UnitSpec with TableDrivenPropertyChecks {

  "The res profile converter" should {
    "find the maximum feed in of a time series" in {
      val time = ZonedDateTime.now()

      val value0 = new TimeBasedValue(time, new PValue(1.asKiloWatt))
      val value1 =
        new TimeBasedValue(time.plusHours(1), new PValue((-1).asKiloWatt))
      val value2 = new TimeBasedValue(time.plusHours(2), new PValue(null))
      val value3 =
        new TimeBasedValue(time.plusHours(3), new PValue((-3).asKiloWatt))
      val value4 =
        new TimeBasedValue(time.plusHours(4), new PValue((-2).asKiloWatt))

      val cases = Table(
        ("timeSeries", "expectedValue"),
        (new IndividualTimeSeries(Set(value0).asJava), None),
        (new IndividualTimeSeries(Set(value0, value2).asJava), None),
        (
          new IndividualTimeSeries(Set(value0, value1, value2).asJava),
          Some(value1)
        ),
        (
          new IndividualTimeSeries(Set(value1, value2, value4).asJava),
          Some(value4)
        ),
        (
          new IndividualTimeSeries(Set(value1, value2, value3, value4).asJava),
          Some(value3)
        )
      )

      forAll(cases) { (timeSeries, expectedValue) =>
        val maxFeedIn = findMaxFeedIn(timeSeries)
        maxFeedIn shouldBe expectedValue
      }

    }

    "return the correct coordinate" in {
      val cases = Table(
        ("profileType", "expectedCoordinate"),
        (ResProfileType.PV1, hanoverCoordinate),
        (ResProfileType.PV2, luebeckCoordinate),
        (ResProfileType.PV3, hanoverCoordinate),
        (ResProfileType.PV4, hanoverCoordinate),
        (ResProfileType.PV5, luebeckCoordinate),
        (ResProfileType.PV6, luebeckCoordinate),
        (ResProfileType.PV7, hanoverCoordinate),
        (ResProfileType.PV8, hanoverCoordinate)
      )

      forAll(cases) { (profileType, expectedCoordinate) =>
        ResProfileConverter.getCoordinate(
          profileType
        ) shouldBe expectedCoordinate
      }
    }

    "throw an exception if unknown profile type is given for coordinate" in {
      Try(ResProfileConverter.getCoordinate(ResProfileType.WP1)) match {
        case Success(_) => fail("This test should not pass!")
        case Failure(exception) =>
          exception.getMessage shouldBe s"There is no coordinate for the profile type WP1."
      }
    }

    "return the correct compass direction" in {
      val cases = Table(
        ("profileType", "expectedDirection"),
        (ResProfileType.PV1, CompassDirection.East),
        (ResProfileType.PV2, CompassDirection.East),
        (ResProfileType.PV3, CompassDirection.South),
        (ResProfileType.PV4, CompassDirection.South),
        (ResProfileType.PV5, CompassDirection.South),
        (ResProfileType.PV6, CompassDirection.South),
        (ResProfileType.PV7, CompassDirection.West),
        (ResProfileType.PV8, CompassDirection.West)
      )

      forAll(cases) { (profileType, expectedDirection) =>
        ResProfileConverter.getCompassDirection(
          profileType
        ) shouldBe expectedDirection
      }
    }

    "throw an exception if unknown profile type is given for compass direction" in {
      Try(ResProfileConverter.getCompassDirection(ResProfileType.WP1)) match {
        case Success(_) => fail("This test should not pass!")
        case Failure(exception) =>
          exception.getMessage shouldBe s"There is no compass direction for the profile type WP1."
      }
    }

  }
}
