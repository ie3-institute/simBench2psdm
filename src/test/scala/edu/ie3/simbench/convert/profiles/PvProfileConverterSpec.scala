package edu.ie3.simbench.convert.profiles

import edu.ie3.datamodel.models.timeseries.individual.{
  IndividualTimeSeries,
  TimeBasedValue
}
import edu.ie3.datamodel.models.value.PValue
import edu.ie3.simbench.model.datamodel.profiles.ResProfileType
import edu.ie3.test.common.UnitSpec
import edu.ie3.util.quantities.QuantityUtils._
import org.scalatest.prop.TableDrivenPropertyChecks

import java.time.ZonedDateTime
import scala.jdk.CollectionConverters._
import scala.jdk.OptionConverters.RichOptional

class PvProfileConverterSpec extends UnitSpec with TableDrivenPropertyChecks {

  "The pv profile converter" should {

    "calculate the power before the converter correctly" in {
      val time = ZonedDateTime.now()

      val timeSeries = new IndividualTimeSeries(
        Set(
          new TimeBasedValue(time, new PValue((-1).asKiloWatt)),
          new TimeBasedValue(time.plusHours(1), new PValue((-3).asKiloWatt)),
          new TimeBasedValue(time.plusHours(2), new PValue((-2).asKiloWatt))
        ).asJava
      )

      val efficiency = 97.asPercent
      val kG: Double = 0.8999999761581421
      val kT: Double = 1.0

      val result = PvProfileConverter.calculatePowerBeforeConverter(
        timeSeries,
        efficiency,
        kG,
        kT
      )

      val entries = result.getEntries.asScala.toList.map(value =>
        value.getValue.getP.toScala
          .getOrElse(fail("This test should not fail!"))
      )

      entries(0).getValue.doubleValue() shouldBe -1.1454754026242313
      entries(1).getValue.doubleValue() shouldBe -3.436426207872694
      entries(2).getValue.doubleValue() shouldBe -2.2909508052484626
    }

    "return the correct azimuth for a given pv profile type" in {
      val cases = Table(
        ("profileType", "expectedAzimuth"),
        (ResProfileType.PV1, (-90).asDegreeGeom),
        (ResProfileType.PV2, (-90).asDegreeGeom),
        (ResProfileType.PV3, 0.asDegreeGeom),
        (ResProfileType.PV4, 0.asDegreeGeom),
        (ResProfileType.PV5, 0.asDegreeGeom),
        (ResProfileType.PV6, 0.asDegreeGeom),
        (ResProfileType.PV7, 90.asDegreeGeom),
        (ResProfileType.PV8, 90.asDegreeGeom)
      )

      forAll(cases) { (profileType, expectedAzimuth) =>
        PvProfileConverter.getAzimuth(profileType) shouldBe expectedAzimuth
      }
    }

    "calculate the elevation angle correctly" in {
      val result = PvProfileConverter.calculateElevationAngle(
        None,
        5.asKiloVoltAmpere,
        ResProfileType.PV3,
        0.asDegreeGeom
      )

      result shouldBe 0.652171369646312.asDegreeGeom
    }

  }
}
