package edu.ie3.simbench.io

import java.io.File
import java.nio.file.Paths
import edu.ie3.simbench.exception.CodeValidationException
import edu.ie3.simbench.exception.io.DownloaderException
import edu.ie3.simbench.model.SimbenchCode
import edu.ie3.test.common.UnitSpec
import edu.ie3.util.io.FileIOUtils
import org.scalatest.Ignore

import scala.language.postfixOps
import scala.util.{Failure, Success, Try}

@Ignore
class DownloaderSpec extends UnitSpec with IoUtils {
  val uuidMap: Map[String, String] = Map(
    "1-LV-urban6--0-sw" -> "a5a1d286-99a8-431c-9b2b-943f86467f22"
  )
  val downloader: Downloader = Downloader(
    "testData/download/",
    "https://daks.uni-kassel.de/bitstreams",
    uuidMap,
    failOnExistingFiles = false
  )

  "The download" should {
    "provide a file in the correct location" in {
      val targetSimbenchCode = SimbenchCode("1-LV-urban6--0-sw").getOrElse(
        throw CodeValidationException(
          "'1-LV-urban6--0-sw' is no valid SimBench code."
        )
      )
      val downloadFolderPath = new File("testData/download")
      val expectedPath = Paths.get(
        s"${downloadFolderPath.getAbsolutePath}/${targetSimbenchCode.code}.zip"
      )

      val actualFile = downloader.download(targetSimbenchCode)
      actualFile shouldBe expectedPath

      FileIOUtils.deleteRecursively(actualFile)
    }

    "fail downloading an already existing archive, when asked to do so" in {
      val downloader: Downloader = Downloader(
        "testData/download/",
        "https://daks.uni-kassel.de/bitstreams",
        uuidMap,
        failOnExistingFiles = true
      )

      val targetSimbenchCode = SimbenchCode("1-LV-urban6--0-sw").getOrElse(
        throw CodeValidationException(
          "'1-LV-urban6--0-sw' is no valid SimBench code."
        )
      )
      val downloadFolderPath = new File("testData/download")
      val expectedPath = Paths.get(
        s"${downloadFolderPath.getAbsolutePath}/${targetSimbenchCode.code}.zip"
      )

      val actualFile = downloader.download(targetSimbenchCode)
      actualFile shouldBe expectedPath

      Try(
        intercept[DownloaderException] {
          downloader.download(targetSimbenchCode)
        }.getMessage shouldBe "Cannot download to file '1-LV-urban6--0-sw.zip', as it already exists"
      ) match {
        case Success(assertion) =>
          FileIOUtils.deleteRecursively(actualFile)
          assertion
        case Failure(exception) =>
          FileIOUtils.deleteRecursively(expectedPath)
          throw exception
      }
    }

    "override an already existing archive, when asked to do so" in {
      val targetSimbenchCode = SimbenchCode("1-LV-urban6--0-sw").getOrElse(
        throw CodeValidationException(
          "'1-LV-urban6--0-sw' is no valid SimBench code."
        )
      )
      val downloadFolderPath = new File("testData/download")
      val expectedPath = Paths.get(
        s"${downloadFolderPath.getAbsolutePath}/${targetSimbenchCode.code}.zip"
      )

      val actualFile = downloader.download(targetSimbenchCode)
      actualFile shouldBe expectedPath

      Try {
        val actualFile = downloader.download(targetSimbenchCode)
        actualFile shouldBe expectedPath
      } match {
        case Success(assertion) =>
          FileIOUtils.deleteRecursively(actualFile)
          assertion
        case Failure(exception) =>
          FileIOUtils.deleteRecursively(expectedPath)
          throw exception
      }
    }
  }

  /* Unzipping operations are tested in "UnzippingSpec" */
}
