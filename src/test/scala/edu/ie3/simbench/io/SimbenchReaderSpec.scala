package edu.ie3.simbench.io

import java.nio.file.Paths

import edu.ie3.simbench.exception.io.IoException
import edu.ie3.simbench.model.RawModelData
import edu.ie3.simbench.model.datamodel._
import edu.ie3.simbench.model.datamodel.profiles.{
  LoadProfile,
  PowerPlantProfile,
  ResProfile
}
import edu.ie3.simbench.model.datamodel.types.LineType.{ACLineType, DCLineType}
import edu.ie3.simbench.model.datamodel.types.{LineType, Transformer2WType}
import edu.ie3.test.common.{SimbenchReaderTestData, UnitSpec}
import org.scalatest.Inside._

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}

class SimbenchReaderSpec extends UnitSpec with SimbenchReaderTestData {
  given ec: ExecutionContextExecutor = ExecutionContext.global

  val classLoader: ClassLoader = this.getClass.getClassLoader
  /* Replace leading file separator, if it is a Windows-Folderpath (/C: etc.) */
  val checkedFolderPath: String = IoUtils.trimFirstSeparatorInWindowsPath(
    classLoader.getResource("io/csv/simpleDataset").getPath
  )
  val reader: SimbenchReader =
    SimbenchReader("simpleDataset", Paths.get(checkedFolderPath))
  val readModelClassMethod: PrivateMethod[
    Future[(Class[_ <: SimbenchModel], Vector[Map[String, String]])]
  ] =
    PrivateMethod[Future[
      (Class[_ <: SimbenchModel], Vector[Map[String, String]])
    ]](Symbol("read"))

  "The SimBench data set reader" should {
    "throw an exception on the wrong model class to read" in {
      /* Simple mocking class implementing nothing at all */
      final case class SimbenchMock() extends SimbenchModel {

        /** Identifier
          */
        override val id: String = "No id"
      }

      reader invokePrivate readModelClassMethod(
        classOf[SimbenchMock],
        Array.empty[HeadLineField]
      ) onComplete {
        case Success(_) =>
          fail(
            "The future terminated successfully, although it was expected to fail"
          )
        case Failure(exception: IoException) =>
          exception.getMessage shouldBe "Cannot read the data set, as the file " +
            "name for class edu.ie3.simbench.io.SimbenchReaderSpec$SimbenchMock$1 can not be determined"
        case Failure(wrongException) =>
          fail("The future failed with the wrong Exception", wrongException)
      }
    }

    "read the coordinates correctly" in {
      val expected = classOf[Coordinate] -> Some(
        Vector(
          RawModelData(
            classOf[Coordinate],
            Map(
              "id" -> "coord_0",
              "x" -> "11.411",
              "y" -> "53.6407",
              "subnet" -> "LV1.101",
              "voltLvl" -> "7"
            )
          ),
          RawModelData(
            classOf[Coordinate],
            Map(
              "id" -> "coord_3",
              "x" -> "11.4097",
              "y" -> "53.6413",
              "subnet" -> "LV1.101",
              "voltLvl" -> "7"
            )
          ),
          RawModelData(
            classOf[Coordinate],
            Map(
              "id" -> "coord_14",
              "x" -> "11.4097",
              "y" -> "53.6413",
              "subnet" -> "MV1.101_LV1.101_Feeder1",
              "voltLvl" -> "5"
            )
          )
        )
      )

      reader invokePrivate readModelClassMethod(
        classOf[Coordinate],
        Coordinate.getFields
      ) onComplete {
        case Success(classToCoordinates) => classToCoordinates shouldBe expected
        case Failure(exception) =>
          fail(
            "Future was not meant to fail. Failed due to the following exception.",
            exception
          )
      }
    }

    "read the nodal power flow results correctly" in {
      val expected = classOf[NodePFResult] -> Some(
        Vector(
          RawModelData(
            classOf[NodePFResult],
            Map(
              "node" -> "LV1.101 Bus 1",
              "vm" -> "1.02203",
              "va" -> "1.17226",
              "substation" -> "NULL",
              "subnet" -> "LV1.101",
              "voltLvl" -> "7"
            )
          ),
          RawModelData(
            classOf[NodePFResult],
            Map(
              "node" -> "LV1.101 Bus 4",
              "vm" -> "1.02474",
              "va" -> "1.17148",
              "substation" -> "NULL",
              "subnet" -> "LV1.101",
              "voltLvl" -> "7"
            )
          ),
          RawModelData(
            classOf[NodePFResult],
            Map(
              "node" -> "MV1.101 Bus 4",
              "vm" -> "1.025",
              "va" -> "0",
              "substation" -> "NULL",
              "subnet" -> "MV1.101_LV1.101_Feeder1",
              "voltLvl" -> "5"
            )
          )
        )
      )

      reader invokePrivate readModelClassMethod(
        classOf[NodePFResult],
        NodePFResult.getFields
      ) onComplete {
        case Success(classToCoordinates) => classToCoordinates shouldBe expected
        case Failure(exception) =>
          fail(
            "Future was not meant to fail. Failed due to the following exception.",
            exception
          )
      }
    }

    "get the field to value maps correctly" in {
      val fieldToValuesMethod = PrivateMethod[
        Map[Class[?], Option[Vector[RawModelData]]]
      ](Symbol("getFieldToValueMaps"))
      val fieldToValuesMap = reader invokePrivate fieldToValuesMethod()

      fieldToValuesMap.keySet.size shouldBe 16

      /* profiles */
      fieldToValuesMap
        .getOrElse(
          classOf[LoadProfile],
          fail(s"No entry available for class ${classOf[LoadProfile]}")
        )
        .getOrElse(fail(s"Entry for class ${classOf[LoadProfile]} is empty."))
        .length shouldBe 2 /* Here, each time step is an entry */
      fieldToValuesMap
        .getOrElse(
          classOf[PowerPlantProfile],
          fail(s"No entry available for class ${classOf[PowerPlantProfile]}")
        )
        .getOrElse(
          fail(s"Entry for class ${classOf[PowerPlantProfile]} is empty.")
        )
        .length shouldBe 2 /* Here, each time step is an entry */
      fieldToValuesMap
        .getOrElse(
          classOf[ResProfile],
          fail(s"No entry available for class ${classOf[ResProfile]}")
        )
        .getOrElse(fail(s"Entry for class ${classOf[ResProfile]} is empty."))
        .length shouldBe 2 /* Here, each time step is an entry */

      fieldToValuesMap
        .getOrElse(
          classOf[StudyCase],
          fail(s"No entry available for class ${classOf[StudyCase]}")
        )
        .getOrElse(fail(s"Entry for class ${classOf[StudyCase]} is empty."))
        .length shouldBe 6
      fieldToValuesMap
        .getOrElse(
          classOf[Coordinate],
          fail(s"No entry available for class ${classOf[Coordinate]}")
        )
        .getOrElse(fail(s"Entry for class ${classOf[Coordinate]} is empty."))
        .length shouldBe 3
      fieldToValuesMap
        .getOrElse(
          classOf[ExternalNet],
          fail(s"No entry available for class ${classOf[ExternalNet]}")
        )
        .getOrElse(fail(s"Entry for class ${classOf[ExternalNet]} is empty."))
        .length shouldBe 1
      fieldToValuesMap
        .getOrElse(
          classOf[ACLineType],
          fail(s"No entry available for class ${classOf[ACLineType]}")
        )
        .getOrElse(fail(s"Entry for class ${classOf[ACLineType]} is empty."))
        .length shouldBe 21
      fieldToValuesMap
        .getOrElse(
          classOf[Line[? <: LineType]],
          fail(s"No entry available for class ${classOf[Line[? <: LineType]]}")
        )
        .getOrElse(
          fail(s"Entry for class ${classOf[Line[? <: LineType]]} is empty.")
        )
        .length shouldBe 1
      fieldToValuesMap
        .getOrElse(
          classOf[Load],
          fail(s"No entry available for class ${classOf[Load]}")
        )
        .getOrElse(fail(s"Entry for class ${classOf[Load]} is empty."))
        .length shouldBe 1
      fieldToValuesMap
        .getOrElse(
          classOf[Node],
          fail(s"No entry available for class ${classOf[Node]}")
        )
        .getOrElse(fail(s"Entry for class ${classOf[Node]} is empty."))
        .length shouldBe 3
      fieldToValuesMap
        .getOrElse(
          classOf[NodePFResult],
          fail(s"No entry available for class ${classOf[NodePFResult]}")
        )
        .getOrElse(fail(s"Entry for class ${classOf[NodePFResult]} is empty."))
        .length shouldBe 3
      fieldToValuesMap
        .getOrElse(
          classOf[RES],
          fail(s"No entry available for class ${classOf[RES]}")
        )
        .getOrElse(fail(s"Entry for class ${classOf[RES]} is empty."))
        .length shouldBe 1
      fieldToValuesMap
        .getOrElse(
          classOf[Transformer2WType],
          fail(s"No entry available for class ${classOf[Transformer2WType]}")
        )
        .getOrElse(
          fail(s"Entry for class ${classOf[Transformer2WType]} is empty.")
        )
        .length shouldBe 12
      fieldToValuesMap
        .getOrElse(
          classOf[Transformer2W],
          fail(s"No entry available for class ${classOf[Transformer2W]}")
        )
        .getOrElse(fail(s"Entry for class ${classOf[Transformer2W]} is empty."))
        .length shouldBe 1
    }

    "read the complete grid data set correctly" in {
      val actual = reader.readGrid()
      inside(actual) {
        case GridModel(
              simbenchCode,
              externalNets,
              lines,
              loads,
              loadProfiles,
              measurements,
              nodes,
              nodePFResults,
              powerPlants,
              powerPlantProfiles,
              res,
              resProfiles,
              shunts,
              storages,
              storageProfiles,
              studyCases,
              substations,
              switches,
              transofmers2w,
              transformers3w
            ) =>
          simbenchCode shouldBe expectedGridModel.simbenchCode
          externalNets shouldBe expectedGridModel.externalNets
          lines shouldBe expectedGridModel.lines
          loads shouldBe expectedGridModel.loads
          loadProfiles.toSet shouldBe expectedGridModel.loadProfiles.toSet
          measurements shouldBe expectedGridModel.measurements
          nodes shouldBe expectedGridModel.nodes
          nodePFResults shouldBe expectedGridModel.nodePFResults
          powerPlants shouldBe expectedGridModel.powerPlants
          powerPlantProfiles.toSet shouldBe expectedGridModel.powerPlantProfiles.toSet
          res shouldBe expectedGridModel.res
          resProfiles.toSet shouldBe expectedGridModel.resProfiles.toSet
          shunts shouldBe expectedGridModel.shunts
          storages shouldBe expectedGridModel.shunts
          storageProfiles.toSet shouldBe expectedGridModel.storageProfiles.toSet
          studyCases shouldBe expectedGridModel.studyCases
          substations shouldBe expectedGridModel.substations
          switches shouldBe expectedGridModel.switches
          transofmers2w shouldBe expectedGridModel.transformers2w
          transformers3w shouldBe expectedGridModel.transformers3w
        case wrongEntity =>
          fail(s"Somehow a wrong class got instantiated... $wrongEntity")
      }
    }
  }
}
