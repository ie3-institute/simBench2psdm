package edu.ie3.simbench.io

import edu.ie3.simbench.config.SimbenchConfig
import edu.ie3.simbench.config.SimbenchConfig.Io
import edu.ie3.simbench.config.SimbenchConfig.Io.{Input, Output}
import edu.ie3.simbench.config.SimbenchConfig.Io.Input.Download
import edu.ie3.test.common.UnitSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}

import java.nio.file.{Files, Path}
import scala.util.Try
import scala.util.matching.Regex

class ExtractorSpec
  extends UnitSpec
    with IoUtils
    with Matchers
    with BeforeAndAfterAll
    with BeforeAndAfterEach {

  private var tempDir: Path = _

  private var extractor: Extractor = _
  private var uuidPattern: Regex = _

  override def beforeAll(): Unit = {
    super.beforeAll()

    tempDir = Files.createTempDirectory("extractorTest")

    val download: Download = Download(
      baseUrl = "https://daks.uni-kassel.de/bitstreams",
      failOnExistingFiles = false,
      directory = tempDir.toString
    )

    val input: Input = Input(
      csv = SimbenchConfig.CsvConfig(
        directoryHierarchy = false,
        fileEncoding = "UTF-8",
        fileEnding = ".csv",
        separator = ";"
      ),
      download = download
    )

    val output: Output = Output(
      compress = true,
      csv = SimbenchConfig.CsvConfig(
        directoryHierarchy = false,
        fileEncoding = "UTF-8",
        fileEnding = ".csv",
        separator = ";"
      ),
      targetDir = "convertedData"
    )

    val io: Io = Io(input = input, output = output, simbenchCodes = List.empty)

    val simbenchConfig: SimbenchConfig = SimbenchConfig(
      conversion = SimbenchConfig.Conversion(removeSwitches = false),
      io = io
    )

    extractor = new Extractor(simbenchConfig)

    uuidPattern = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}".r
  }

  override def beforeEach(): Unit = {
    super.beforeEach()
  }

  override def afterEach(): Unit = {
    super.afterEach()
  }

  override def afterAll(): Unit = {
    Try(deleteRecursively(tempDir))
    super.afterAll()
  }

  private def deleteRecursively(path: Path): Unit = {
    val file = path.toFile
    if (file.exists()) {
      if (file.isDirectory) {
        file.listFiles().foreach(child => deleteRecursively(child.toPath))
      }
      file.delete()
    }
  }

  private def createTestFile(fileName: String, content: String): Path = {
    val filePath = tempDir.resolve(fileName)
    Files.write(filePath, content.getBytes("UTF-8"))
    filePath
  }

  /* ------------------------------------------------------------------
   * Testcases
   * ------------------------------------------------------------------ */

  "The extractor" should {

    "ensure that the download correctly downloads and saves the expected data file" in {
      extractor.download()
      val downloadedFile = tempDir.resolve("simbench_datalinks.csv").toFile
      downloadedFile.exists() shouldBe true
    }

    "return a correct map of codes to UUIDs" in {
      extractor.download()
      val uuidMap = extractor.extractUUIDMap()
      uuidMap("1-LV-urban6--0-sw") should fullyMatch regex uuidPattern
      uuidMap should contain key "1-LV-rural1--2-no_sw"
      uuidMap("1-LV-rural1--2-no_sw") should fullyMatch regex uuidPattern
    }

    "throw an IllegalArgumentException when CSV does not contain the required columns" in {
      createTestFile("simbench_datalinks.csv", "Invalid,Content\nOnly,OneColumn")

      an[IllegalArgumentException] should be thrownBy {
        extractor.extractUUIDMap()
      }
    }

    "handle cases with no valid UUIDs" in {
      createTestFile(
        "simbench_datalinks.csv",
        "code,csv\n1-LV-urban6--0-sw,invalid_uuid\n1-LV-rural1--2-no_sw,another_invalid_uuid"
      )

      val uuidMap = extractor.extractUUIDMap()
      uuidMap shouldBe empty
    }
  }
}
