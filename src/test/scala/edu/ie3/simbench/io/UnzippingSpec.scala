package edu.ie3.simbench.io

import java.nio.file.{Files, Path, Paths}

import edu.ie3.simbench.exception.io.{ZipperException, IoException}
import edu.ie3.test.common.UnitSpec
import edu.ie3.util.io.FileIOUtils
import org.scalatest.BeforeAndAfterEach

import scala.jdk.StreamConverters._
import scala.language.{existentials, postfixOps}
import scala.util.{Failure, Success, Try}

class UnzippingSpec extends UnitSpec with IoUtils with BeforeAndAfterEach {
  val uuidMap: Map[String, String] = Map(
    "1-LV-urban6--0-sw" -> "a5a1d286-99a8-431c-9b2b-943f86467f22"
  )
  val downloader: Downloader = Downloader(
    "testData/download/",
    "https://daks.uni-kassel.de/bitstreams",
    uuidMap,
    failOnExistingFiles = false
  )

  private val archiveFilePath: Path =
    Paths.get(pwd, s"${downloader.downloadDir}1-LV-rural1--0-no_sw.zip")
  private val extractionBasePath: Path =
    Paths.get(pwd, s"${downloader.downloadDir}1-LV-rural1--0-no_sw")
  private val secondLevelPath: Path =
    Paths.get(s"${extractionBasePath.toAbsolutePath}/1-LV-rural1--0-no_sw")

  "The unzipping" should {
    "throw an exception, if the questioned zip archive is not apparent" in {
      val path = Paths.get("totally/random/non/exsisting/file.zip")
      val thrown = intercept[IoException] {
        Zipper.unzip(
          path,
          downloader.downloadDir,
          downloader.failOnExistingFiles
        )
      }

      thrown.getMessage == "The file totally/random/non/exsisting/file.zip cannot be unzipped, as it does not exist."
    }

    "throw an exception, if the unzip routine is pointed to a directory" in {
      val path = Paths.get("inputData/download")
      val thrown = intercept[IoException] {
        Zipper.unzip(
          path,
          downloader.downloadDir,
          downloader.failOnExistingFiles
        )
      }

      thrown.getMessage == "The file inputData/download cannot be unzipped, as it is a directory."
    }

    "throw an exception, if the file is not a zip archive" in {
      Files.createDirectories(Paths.get(pwd, s"${downloader.downloadDir}"))
      val filePath = Files.createFile(
        Paths.get(pwd, s"${downloader.downloadDir}/test.txt")
      )

      intercept[IoException] {
        Zipper.unzip(
          filePath,
          downloader.downloadDir,
          downloader.failOnExistingFiles
        )
      }
      /* Message cannot be tested, as the path is dependent of the location of the code. */

      Files.deleteIfExists(filePath)
    }

    "throw an exception, if the target folder exists and is a file" in {
      val filePath =
        Paths.get(
          s"${downloader.downloadDir}targetFolderExistsAndIsFile.zip"
        )
      val thrown = intercept[ZipperException] {
        Zipper.unzip(
          filePath,
          downloader.downloadDir,
          downloader.failOnExistingFiles
        )
      }
      thrown.getMessage == "The target directory to unzip testData/download/targetFolderExistsAndIsFile.zip already exists, but is not a directory"
    }

    "unzip the data without flattening the directory structure correctly" in {
      val actualBasePath = Zipper.unzip(
        archiveFilePath,
        downloader.downloadDir,
        downloader.failOnExistingFiles
      )
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
        .map(entry =>
          fileNameRegexWithAnyEnding.findFirstIn(entry.toString).getOrElse("")
        )
        .sorted
        .toVector
      files.close()

      Try(actualContent shouldBe expectedContent) match {
        case Failure(exception) =>
          /* Tidy up all the temporary files, when an exception occurs */
          FileIOUtils.deleteRecursively(extractionBasePath)
          logger.debug("Successfully tidied up the extraction base path")
          throw exception
        case Success(_) =>
          /* Tidy up all the temporary files */
          FileIOUtils.deleteRecursively(extractionBasePath)
      }
    }

    "unzip the data and flatten the directory structure correctly" in {
      val actualBasePath =
        Zipper.unzip(
          archiveFilePath,
          downloader.downloadDir,
          downloader.failOnExistingFiles,
          flattenDirectories = true
        )
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
        .map(entry =>
          fileNameRegexWithAnyEnding.findFirstIn(entry.toString).getOrElse("")
        )
        .sorted
        .toVector
      files.close()

      try {
        actualContent shouldBe expectedContent
      } catch {
        case e: Throwable =>
          /* Tidy up all the temporary files, when an exception occurs */
          FileIOUtils.deleteRecursively(extractionBasePath)
          logger.debug("Successfully tidied up the extraction base path")
          throw e
      }

      /* Tidy up all the temporary files */
      FileIOUtils.deleteRecursively(extractionBasePath)
    }

    "throw an exception, if the target folder exists and contains files" in {
      val targetFolderPath =
        Zipper.unzip(
          archiveFilePath,
          downloader.downloadDir,
          failOnExistingFiles = false,
          flattenDirectories = true
        )
      Try(
        intercept[ZipperException] {
          Zipper.unzip(
            archiveFilePath,
            downloader.downloadDir,
            failOnExistingFiles = true,
            flattenDirectories = true
          )
        }.getMessage == "Cannot unzip '${zipArchive.getFileName}', as it's target folder '$targetFolder' is not empty"
      ) match {
        case Success(assertion) =>
          FileIOUtils.deleteRecursively(targetFolderPath)
          assertion
        case Failure(exception) =>
          FileIOUtils.deleteRecursively(targetFolderPath)
          throw exception
      }
    }

    "throw no exception, if the target folder exists and contains files" in {
      val targetFolderPath =
        Zipper.unzip(
          archiveFilePath,
          downloader.downloadDir,
          failOnExistingFiles = false,
          flattenDirectories = true
        )
      Try(
        noException shouldBe thrownBy {
          Zipper.unzip(
            archiveFilePath,
            downloader.downloadDir,
            failOnExistingFiles = false,
            flattenDirectories = true
          )
        }
      ) match {
        case Success(assertion) =>
          FileIOUtils.deleteRecursively(targetFolderPath)
          assertion
        case Failure(exception) =>
          FileIOUtils.deleteRecursively(targetFolderPath)
          throw exception
      }
    }
  }
}
