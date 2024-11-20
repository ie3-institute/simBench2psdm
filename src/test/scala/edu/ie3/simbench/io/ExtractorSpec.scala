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
    failOnExistingFiles = false,
    folder = "testData/download" // Valid download location
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
    targetFolder = "convertedData"
  )
  val io: Io = Io(input = input, output = output, simbenchCodes = List.empty)
  val simbenchConfig: SimbenchConfig = SimbenchConfig(
    conversion = SimbenchConfig.Conversion(removeSwitches = false),
    io = io
  )

  val extractor = new Extractor(simbenchConfig)
  val uuidPattern: Regex =
    "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}".r

  // Utility to create test files
  private def createTestFile(
      folder: String,
      fileName: String,
      content: String
  ): File = {
    val dir = new File(folder)
    if (!dir.exists()) dir.mkdirs()
    val file = new File(s"$folder/$fileName")
    val writer = new PrintWriter(file)
    writer.write(content)
    writer.close()
    file
  }

  // Utility to clean up test files
  private def deleteFolder(folder: String): Unit = {
    val dir = new File(folder)
    if (dir.exists()) dir.listFiles().foreach(_.delete())
    dir.delete()
  }

  "The extraction" should {
    "download should save the file to the specified location" in {
      extractor.download()
      val downloadedFile = new File(
        s"${simbenchConfig.io.input.download.folder}/simbench_datalinks.csv"
      )
      downloadedFile.exists() shouldBe true
    }

    "extractUUIDMap should return a correct map of codes to UUIDs" in {
      extractor.download()
      val uuidMap = extractor.extractUUIDMap()
      uuidMap should contain key "1-LV-urban6--0-sw"
      uuidMap should contain key "1-LV-rural1--2-no_sw"
      uuidMap("1-LV-urban6--0-sw") should fullyMatch regex uuidPattern
      uuidMap("1-LV-rural1--2-no_sw") should fullyMatch regex uuidPattern
    }

    "extractUUIDMap should throw an exception if required columns are missing" in {
      val invalidFolder = "testData/invalidStructure"
      createTestFile(
        invalidFolder,
        "simbench_datalinks.csv",
        "Invalid,Content\nOnly,OneColumn"
      )
      val invalidConfig = simbenchConfig.copy(
        io = simbenchConfig.io.copy(
          input = simbenchConfig.io.input.copy(
            download = simbenchConfig.io.input.download.copy(
              folder = invalidFolder
            )
          )
        )
      )
      val invalidExtractor = new Extractor(invalidConfig)
      an[IllegalArgumentException] should be thrownBy invalidExtractor
        .extractUUIDMap()
      deleteFolder(invalidFolder)
    }

    "extractUUIDMap should return an empty map if no valid UUIDs are found" in {
      val emptyFolder = "testData/emptyData"
      createTestFile(emptyFolder, "simbench_datalinks.csv", "Code,UUID\n")
      val emptyConfig = simbenchConfig.copy(
        io = simbenchConfig.io.copy(
          input = simbenchConfig.io.input.copy(
            download = simbenchConfig.io.input.download.copy(
              folder = emptyFolder
            )
          )
        )
      )
      val emptyExtractor = new Extractor(emptyConfig)
      emptyExtractor.download()
      val uuidMap = emptyExtractor.extractUUIDMap()
      uuidMap shouldBe empty
      deleteFolder(emptyFolder)
    }
  }
}
