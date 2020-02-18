package edu.ie3.simbench.io

import java.nio.file.Paths

import edu.ie3.simbench.exception.io.IoException
import edu.ie3.simbench.model.datamodel.types.LineType
import edu.ie3.simbench.model.datamodel.{
  Coordinate,
  ExternalNet,
  Load,
  Node,
  RES,
  SimbenchModel,
  Transformer2W,
}
import edu.ie3.test.common.UnitSpec

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}

class SimbenchReaderSpec extends UnitSpec {
  implicit val ec: ExecutionContextExecutor = ExecutionContext.global

  val classLoader: ClassLoader = this.getClass.getClassLoader
  val reader: SimbenchReader = SimbenchReader(
    Paths.get(classLoader.getResource("io/csv/simpleDataset").getPath))
  val readModelClassMethod: PrivateMethod[
    Future[(Class[_ <: SimbenchModel], Vector[Map[String, String]])]] =
    PrivateMethod[Future[(Class[_ <: SimbenchModel],
                          Vector[Map[String, String]])]](Symbol("read"))

  "The SimBench data set reader" should {
    "throw an exception on the wrong model class to read" in {
      /* Simple mocking class implementing nothing at all */
      case class SimbenchMock() extends SimbenchModel {

        /**
          * Identifier
          */
        override val id: String = "No id"
      }

      reader invokePrivate readModelClassMethod(
        classOf[SimbenchMock],
        Array.empty[String]) onComplete {
        case Success(_) =>
          fail(
            "The future terminated successfully, although it was expected to fail")
        case Failure(exception: IoException) =>
          exception.getMessage shouldBe "Cannot read the data set, as the file " +
            "name for class edu.ie3.simbench.io.SimbenchReaderSpec$SimbenchMock$1 can not be determined"
        case Failure(wrongException) =>
          fail("The future failed with the wrong Exception", wrongException)
      }
    }

    "read the coordinates correctly" in {
      val expected = classOf[Coordinate] -> Vector(
        Map(
          "id" -> "coord_0",
          "x" -> "11.411",
          "y" -> "53.6407",
          "subnet" -> "LV1.101",
          "voltLvl" -> "7"
        ),
        Map(
          "id" -> "coord_3",
          "x" -> "11.4097",
          "y" -> "53.6413",
          "subnet" -> "LV1.101",
          "voltLvl" -> "7"
        ),
        Map(
          "id" -> "coord_14",
          "x" -> "11.4097",
          "y" -> "53.6413",
          "subnet" -> "MV1.101_LV1.101_Feeder1",
          "voltLvl" -> "5"
        )
      )

      reader invokePrivate readModelClassMethod(
        classOf[Coordinate],
        Coordinate.getFields) onComplete {
        case Success(classToCoordinates) => classToCoordinates shouldBe expected
        case Failure(exception) =>
          fail(
            "Future was not meant to fail. Failed due to the following exception.",
            exception)
      }
    }

    "get the field to value maps correctly" in {
      val fieldToValuesMethod = PrivateMethod[Map[Class[_ <: SimbenchModel],
                                                  Vector[Map[String, String]]]](
        Symbol("getFieldToValueMaps"))
      val fieldToValuesMap = reader invokePrivate fieldToValuesMethod()

      fieldToValuesMap.keySet.size shouldBe 7

      fieldToValuesMap
        .getOrElse(classOf[Coordinate],
                   fail(s"No entry available for class ${classOf[Coordinate]}"))
        .length shouldBe 3
      fieldToValuesMap
        .getOrElse(
          classOf[ExternalNet],
          fail(s"No entry available for class ${classOf[ExternalNet]}"))
        .length shouldBe 1
      fieldToValuesMap
        .getOrElse(classOf[LineType],
                   fail(s"No entry available for class ${classOf[LineType]}"))
        .length shouldBe 21
      fieldToValuesMap
        .getOrElse(classOf[Load],
                   fail(s"No entry available for class ${classOf[Load]}"))
        .length shouldBe 1
      fieldToValuesMap
        .getOrElse(classOf[Node],
                   fail(s"No entry available for class ${classOf[Node]}"))
        .length shouldBe 3
      fieldToValuesMap
        .getOrElse(classOf[RES],
                   fail(s"No entry available for class ${classOf[RES]}"))
        .length shouldBe 1
      fieldToValuesMap
        .getOrElse(
          classOf[Transformer2W],
          fail(s"No entry available for class ${classOf[Transformer2W]}"))
        .length shouldBe 1
    }
  }
}
