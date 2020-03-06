package edu.ie3.simbench.io

import java.nio.file.{Files, Paths}

import edu.ie3.simbench.exception.io.DownloaderException
import edu.ie3.test.common.UnitSpec
import edu.ie3.util.io.FileHelper

import scala.jdk.StreamConverters._
import scala.language.postfixOps

class UnzippingSpec extends UnitSpec with IoUtils {
  val downloader: Downloader = Downloader(
    "testData/download/",
    "http://141.51.193.167/simbench/gui/usecase/download")

  "The unzipping" should {
    "throw an exception, if the target folder exists and is a file" in {
      val filePath =
        Paths.get(
          s"${downloader.downloadFolder}targetFolderExistsAndIsFile.zip")
      val thrown = intercept[DownloaderException] {
        Downloader.unzip(downloader, filePath)
      }
      thrown.getMessage == "The target directory to unzip testData/download/targetFolderExistsAndIsFile.zip already exists, but is not a directory"
    }

    "unzip the data without flattening the directory structure correctly" in {
      val archiveFilePath =
        Paths.get(pwd, s"${downloader.downloadFolder}1-LV-rural1--0-no_sw.zip")
      val extractionBasePath =
        Paths.get(pwd, s"${downloader.downloadFolder}1-LV-rural1--0-no_sw")
      val secondLevelPath =
        Paths.get(s"${extractionBasePath.toAbsolutePath}/1-LV-rural1--0-no_sw")

      val actualBasePath = Downloader.unzip(downloader, archiveFilePath)
      actualBasePath.toAbsolutePath shouldBe extractionBasePath.toAbsolutePath
      Files.exists(extractionBasePath) shouldBe true
      Files.isDirectory(extractionBasePath) shouldBe true
      Files.exists(secondLevelPath) shouldBe true
      Files.isDirectory(secondLevelPath) shouldBe true

      val expectedContent = Vector(
        "Coordinates.csv",
        "ExternalNet.csv",
        "Line.csv",
        "LineType.csv",
        "Load.csv",
        "LoadProfile.csv",
        "Node.csv",
        "NodePFResult.csv",
        "RES.csv",
        "RESProfile.csv",
        "StudyCases.csv",
        "Transformer.csv",
        "TransformerType.csv",
      )
      val files = Files.list(secondLevelPath)
      val actualContent = files
        .toScala(LazyList)
        .map(entry =>
          fileNameRegexWithAnyEnding.findFirstIn(entry.toString).getOrElse(""))
        .sorted
        .toVector
      files.close()

      actualContent shouldBe expectedContent

      /* Tidy up all the temporary files */
      FileHelper.deleteRecursively(extractionBasePath)
    }

    "unzip the data and flatten the directory structure correctly" in {
      val archiveFilePath =
        Paths.get(pwd, s"${downloader.downloadFolder}1-LV-rural1--0-no_sw.zip")
      val extractionBasePath =
        Paths.get(pwd, s"${downloader.downloadFolder}1-LV-rural1--0-no_sw")

      val actualBasePath = Downloader.unzip(downloader, archiveFilePath, true)
      actualBasePath.toAbsolutePath shouldBe extractionBasePath.toAbsolutePath
      Files.exists(extractionBasePath) shouldBe true
      Files.isDirectory(extractionBasePath) shouldBe true

      val expectedContent = Vector(
        "Coordinates.csv",
        "ExternalNet.csv",
        "Line.csv",
        "LineType.csv",
        "Load.csv",
        "LoadProfile.csv",
        "Node.csv",
        "NodePFResult.csv",
        "RES.csv",
        "RESProfile.csv",
        "StudyCases.csv",
        "Transformer.csv",
        "TransformerType.csv",
      )
      val files = Files.list(extractionBasePath)
      val actualContent = files
        .toScala(LazyList)
        .map(entry =>
          fileNameRegexWithAnyEnding.findFirstIn(entry.toString).getOrElse(""))
        .sorted
        .toVector
      files.close()

      actualContent shouldBe expectedContent

      /* Tidy up all the temporary files */
      FileHelper.deleteRecursively(extractionBasePath)
    }
  }
}
