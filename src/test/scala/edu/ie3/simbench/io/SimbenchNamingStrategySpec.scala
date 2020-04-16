package edu.ie3.simbench.io

import edu.ie3.simbench.exception.io.SimbenchFileNamingException
import edu.ie3.simbench.model.datamodel.ExternalNet.{Simple, Ward, WardExtended}
import edu.ie3.simbench.model.datamodel.Line.{ACLine, DCLine}
import edu.ie3.simbench.model.datamodel.profiles.{
  LoadProfile,
  PowerPlantProfile,
  ResProfile
}
import edu.ie3.simbench.model.datamodel.types.LineType.{ACLineType, DCLineType}
import edu.ie3.simbench.model.datamodel.types.{
  Transformer2WType,
  Transformer3WType
}
import edu.ie3.simbench.model.datamodel._
import edu.ie3.test.common.UnitSpec
import org.scalatest.prop.TableDrivenPropertyChecks._
import org.scalatest.Inside._

import scala.util.{Failure, Success}

class SimbenchNamingStrategySpec extends UnitSpec {
  "The simbench naming strategy" should {
    val validNamings = Table(
      ("clazz", "filename"),
      (classOf[Coordinate], "Coordinates"),
      (classOf[Simple], "ExternalNet"),
      (classOf[Ward], "ExternalNet"),
      (classOf[WardExtended], "ExternalNet"),
      (classOf[ACLine], "Line"),
      (classOf[DCLine], "Line"),
      (classOf[ACLineType], "LineType"),
      (classOf[DCLineType], "LineType"),
      (classOf[Load], "Load"),
      (classOf[Node], "Node"),
      (classOf[RES], "RES"),
      (classOf[StudyCase], "StudyCases"),
      (classOf[Transformer2W], "Transformer"),
      (classOf[Transformer2WType], "TransformerType"),
      (classOf[Transformer3W], "Transformer3W"),
      (classOf[Transformer3WType], "Transformer3WType"),
      (classOf[LoadProfile], "LoadProfile"),
      (classOf[ResProfile], "RESProfile"),
      (classOf[PowerPlantProfile], "PowerPlantProfile")
    )
    // TODO: Test naming strategy for NodePFResult when implemented

    "provide correct file namings" in {
      forAll(validNamings)(
        (clazz: Class[_], expectedFileName: String) =>
          SimbenchFileNamingStrategy
            .getFileName(clazz) shouldBe Success(expectedFileName)
      )
    }

    "report a failure, if the class is not covered by the naming strategy" in {
      inside(SimbenchFileNamingStrategy.getFileName(classOf[String])) {
        case Failure(exception: SimbenchFileNamingException) =>
          exception.getMessage shouldBe "Cannot determine the filename for class String"
        case _ => fail()
      }
    }
  }
}
