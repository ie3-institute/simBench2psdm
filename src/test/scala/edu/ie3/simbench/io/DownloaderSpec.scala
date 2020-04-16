package edu.ie3.simbench.io

import java.io.File
import java.nio.file.{Files, Paths}

import edu.ie3.simbench.exception.io.{DownloaderException, IoException}
import edu.ie3.test.common.UnitSpec
import edu.ie3.util.io.FileIOUtils

import scala.jdk.StreamConverters._
import scala.language.postfixOps

class DownloaderSpec extends UnitSpec with IoUtils {
  val downloader: Downloader = Downloader(
    "testData/download/",
    "http://141.51.193.167/simbench/gui/usecase/download"
  )

  "The download" should {
    "provide a file in the correct location" in {
      val targetSimbenchCode = "1-LV-urban6--0-sw"
      val downloadFolderPath = new File("testData/download")
      val expectedPath = Paths.get(
        s"${downloadFolderPath.getAbsolutePath}/$targetSimbenchCode.zip"
      )

      val actualFile = Downloader.download(downloader, targetSimbenchCode)
      actualFile shouldBe expectedPath

      FileIOUtils.deleteRecursively(actualFile)
    }
  }

  "The unzipping" should {
    "throw an exception, if the questioned zip archive is not apparent" in {
      val path = Paths.get("totally/random/non/exsisting/file.zip")
      val thrown = intercept[IoException] {
        Downloader.unzip(downloader, path)
      }

      thrown.getMessage == "The file totally/random/non/exsisting/file.zip cannot be unzipped, as it does not exist."
    }

    "throw an exception, if the unzip routine is pointed to a directory" in {
      val path = Paths.get("inputData/download")
      val thrown = intercept[IoException] {
        Downloader.unzip(downloader, path)
      }

      thrown.getMessage == "The file inputData/download cannot be unzipped, as it is a directory."
    }

    "throw an exception, if the file is not a zip archive" in {
      Files.createDirectories(Paths.get(pwd, s"${downloader.downloadFolder}"))
      val filePath = Files.createFile(
        Paths.get(pwd, s"${downloader.downloadFolder}/test.txt")
      )

      intercept[IoException] {
        Downloader.unzip(downloader, filePath)
      }
      /* Message cannot be tested, as the path is dependent of the location of the code. */

      Files.deleteIfExists(filePath)
    }

    "throw an exception, if the target folder exists and is a file" in {
      val filePath =
        Paths.get(
          s"${downloader.downloadFolder}targetFolderExistsAndIsFile.zip"
        )
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
        "TransformerType.csv"
      )
      val files = Files.list(secondLevelPath)
      val actualContent = files
        .toScala(LazyList)
        .map(
          entry =>
            fileNameRegexWithAnyEnding.findFirstIn(entry.toString).getOrElse("")
        )
        .sorted
        .toVector
      files.close()

      actualContent shouldBe expectedContent

      /* Tidy up all the temporary files */
      FileIOUtils.deleteRecursively(extractionBasePath)
    }

    "unzip the data and flatten the directory structure correctly" in {
      val archiveFilePath =
        Paths.get(pwd, s"${downloader.downloadFolder}1-LV-rural1--0-no_sw.zip")
      val extractionBasePath =
        Paths.get(pwd, s"${downloader.downloadFolder}1-LV-rural1--0-no_sw")

      val actualBasePath =
        Downloader.unzip(downloader, archiveFilePath, flattenDirectories = true)
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
        "TransformerType.csv"
      )
      val files = Files.list(extractionBasePath)
      val actualContent = files
        .toScala(LazyList)
        .map(
          entry =>
            fileNameRegexWithAnyEnding.findFirstIn(entry.toString).getOrElse("")
        )
        .sorted
        .toVector
      files.close()

      actualContent shouldBe expectedContent

      /* Tidy up all the temporary files */
      FileIOUtils.deleteRecursively(extractionBasePath)
    }
  }
}
