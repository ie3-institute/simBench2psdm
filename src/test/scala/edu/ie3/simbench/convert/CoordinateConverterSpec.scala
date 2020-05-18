package edu.ie3.simbench.convert

import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.simbench.model.datamodel.Coordinate
import edu.ie3.test.common.UnitSpec
import org.locationtech.jts.geom.{GeometryFactory, Coordinate => JTSCoordinate}

class CoordinateConverterSpec extends UnitSpec {
  "The coordinate converter" should {
    val geometryFactory = new GeometryFactory()

    "convert an existing Coordinate correctly" in {
      val input = Some(
        Coordinate(
          "random coordinate",
          BigDecimal("7.412262"),
          BigDecimal("51.492689"),
          "subnet_1",
          7
        )
      )
      val expected =
        geometryFactory.createPoint(new JTSCoordinate(7.412262, 51.492689))
      expected.setSRID(4326)
      val actual = CoordinateConverter.convert(input)
      actual shouldBe expected
    }

    "build correct dummy geo position, if no Coordinate is apparent" in {
      val expected = NodeInput.DEFAULT_GEO_POSITION
      val actual = CoordinateConverter.convert(None)
      actual shouldBe expected
    }
  }
}
