package edu.ie3.simbench.io

import edu.ie3.simbench.config.SimbenchConfig
import edu.ie3.simbench.config.SimbenchConfig.Io
import edu.ie3.simbench.config.SimbenchConfig.Io.{Input, Output}
import edu.ie3.simbench.config.SimbenchConfig.Io.Input.Download

import java.io.{File, PrintWriter}
import org.scalatest.matchers.should.Matchers
import edu.ie3.test.common.UnitSpec
import scala.util.matching.Regex

class ExtractorSpec extends UnitSpec with IoUtils with Matchers {
  // Define configurations for the extractor
  val download: Download = Download(
    baseUrl = "https://daks.uni-kassel.de/bitstreams",
    failOnExistingFiles = false
  )
  val input: Input = Input(
    directory = "testData/download", // Valid download location
    csv = SimbenchConfig.CsvConfig(
      directoryHierarchy = false,
      fileEncoding = "UTF-8",
      fileEnding = ".csv",
      separator = ";"
    ),
    download = Some(download),
    localFile = None
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

  val extractor = new Extractor(input)
  val uuidPattern: Regex =
    "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}".r

  // Utility to create test files
  private def createTestFile(
      directory: String,
      fileName: String,
      content: String
  ): File = {
    val dir = new File(directory)
    if (!dir.exists()) dir.mkdirs()
    val file = new File(s"$directory/$fileName")
    val writer = new PrintWriter(file)
    writer.write(content)
    writer.close()
    file
  }

  // Utility to clean up test files
  private def deleteDir(directory: String): Unit = {
    val dir = new File(directory)
    if (dir.exists()) dir.listFiles().foreach(_.delete())
    dir.delete()
  }

  "The extractor" should {
    "ensure that the download correctly downloads and saves the expected data file" in {
      extractor.download()
      val downloadedFile = new File(
        s"${simbenchConfig.io.input.directory}/simbench_datalinks.csv"
      )
      downloadedFile.exists() shouldBe true
    }

    "return a correct map of codes to UUIDs" in {
      extractor.download()
      val uuidMap = extractor.extractUUIDMap()
      uuidMap should contain key "1-LV-urban6--0-sw"
      uuidMap should contain key "1-LV-rural1--2-no_sw"
      uuidMap("1-LV-urban6--0-sw") should fullyMatch regex uuidPattern
      uuidMap("1-LV-rural1--2-no_sw") should fullyMatch regex uuidPattern
    }

    "check that an appropriate exception (IllegalArgumentException) is thrown when attempting to extract data that does not contain the required columns" in {
      val invalidDir = "testData/invalidStructure"
      createTestFile(
        invalidDir,
        "simbench_datalinks.csv",
        "Invalid,Content\nOnly,OneColumn"
      )
      val invalidConfig = simbenchConfig.copy(
        io = simbenchConfig.io.copy(
          input = simbenchConfig.io.input.copy(directory = invalidDir)
        )
      )
      val invalidExtractor = new Extractor(invalidConfig.io.input)
      an[IllegalArgumentException] should be thrownBy invalidExtractor
        .extractUUIDMap()
      deleteDir(invalidDir)
    }
    "handle cases with no valid UUIDs" in {
      val invalidUUIDPath = "testData/invalidUUIDs"
      createTestFile(
        invalidUUIDPath,
        "simbench_datalinks.csv",
        "code,csv\n1-LV-urban6--0-sw,invalid_uuid\n1-LV-rural1--2-no_sw,another_invalid_uuid"
      )
      val invalidUUIDConfig = simbenchConfig.copy(
        io = simbenchConfig.io.copy(
          input = simbenchConfig.io.input.copy(directory = invalidUUIDPath)
        )
      )
      val invalidUUIDExtractor = new Extractor(invalidUUIDConfig.io.input)
      val uuidMap = invalidUUIDExtractor.extractUUIDMap()
      uuidMap shouldBe empty
      deleteDir(invalidUUIDPath)
    }
  }
}
