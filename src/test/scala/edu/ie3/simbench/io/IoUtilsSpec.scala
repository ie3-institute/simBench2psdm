/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.simbench.io

import java.nio.file.{Files, Paths}

import edu.ie3.simbench.exception.io.IoException
import edu.ie3.test.common.UnitSpec

class IoUtilsSpec extends UnitSpec {
  "The I/0 utils" should {
    "harmonize file separators correctly" in {
      IoUtils.harmonizeFileSeparator("bla/foo/test.ext") shouldBe "bla/foo/test.ext"
      IoUtils.harmonizeFileSeparator("bla\\foo\\test.ext") shouldBe "bla/foo/test.ext"
      IoUtils.harmonizeFileSeparator("bla\\foo/test.ext") shouldBe "bla/foo/test.ext"
    }

    "extract file endings correctly" in {
      IoUtils.getFileExtensionWithoutDot("test.ext") shouldBe "ext"
      IoUtils.getFileExtensionWithoutDot("bla/foo/test.ext") shouldBe "ext"
      IoUtils.getFileExtensionWithoutDot("ext") shouldBe "ext"
    }

    "throw an IoException, if the provided string is not a path description" in {
      val thrown = intercept[IoException](
        IoUtils.getFileExtensionWithoutDot(
          "totally random string without any meaning"
        )
      )
      thrown.getMessage shouldBe "The provided path string (totally random string without any meaning) does not " +
        "match the pattern .*\\.\\w+$, why no ending can be extracted."
    }

    "compose the correct regex for file names with specified extension, but not matching the extension" in {
      IoUtils
        .fileNameRegex("csv")
        .toString() shouldBe "^(?<![^.\\\\/\\s])\\w+$|[^/\\\\\\s]+(?=\\.csv$)"
      IoUtils
        .fileNameRegex(".csv")
        .toString() shouldBe "^(?<![^.\\\\/\\s])\\w+$|[^/\\\\\\s]+(?=\\.csv$)"
    }

    "compose the correct regex for file names with specified extension, matching the extension" in {
      IoUtils
        .fileNameRegexWithEnding("csv")
        .toString() shouldBe "^(?<![^.\\\\/\\s])\\w+$|[^/\\\\\\s]+\\.csv$"
      IoUtils
        .fileNameRegexWithEnding(".csv")
        .toString() shouldBe "^(?<![^.\\\\/\\s])\\w+$|[^/\\\\\\s]+\\.csv$"
    }

    "deliver the correct regex for file names with any extension" in {
      IoUtils.fileNameRegexWithAnyEnding
        .toString() shouldBe "^(?<![^.\\\\/\\s])\\w+$|[^/\\\\\\s]+\\.\\w+$"
    }

    "detect fully qualified paths correctly" in {
      IoUtils.fullyQualifiedPathRegex.matches("C:\\bla\\file.ext") shouldBe true
      IoUtils.fullyQualifiedPathRegex.matches("C:\\\\bla\\file.ext") shouldBe true
      IoUtils.fullyQualifiedPathRegex.matches("C:\\foo\\bar\\file.ext") shouldBe true
      IoUtils.fullyQualifiedPathRegex.matches("C:\\\\foo\\bar\\file.ext") shouldBe true
      IoUtils.fullyQualifiedPathRegex.matches("C:\\a@b.com\\foo\\bar\\file.ext") shouldBe true
      IoUtils.fullyQualifiedPathRegex.matches(
        "C:\\\\a@b.com\\foo\\bar\\file.ext"
      ) shouldBe true
      IoUtils.fullyQualifiedPathRegex.matches("/bla/file.ext") shouldBe true
      IoUtils.fullyQualifiedPathRegex.matches("/foo/bar/bla/file.ext") shouldBe true
      IoUtils.fullyQualifiedPathRegex.matches("/a@b.com/foo/bar/bla/file.ext") shouldBe true
    }

    "compose a fully qualified path correctly" in {
      IoUtils.composeFullyQualifiedPath("/home/user/test/path", "file", "ext") shouldBe "/home/user/test/path/file.ext"
    }

    "throw an error, if the folder path is not correct" in {
      val thrown = intercept[IoException](
        IoUtils.composeFullyQualifiedPath("home.user.test.path", "file", "ext")
      )
      thrown.getMessage shouldBe "Cannot determine correct fully qualified folder path from home.user.test.path"
    }
  }

  "Checking of files" should {
    "throw an exception, if the questioned file is not apparent" in {
      val path = Paths.get("totally/random/non/exsisting/file.zip")
      val thrown = intercept[IoException] {
        IoUtils.checkFileExists(path, "zip")
      }

      thrown.getMessage == "The file totally/random/non/exsisting/file.zip does not exist."
    }

    "throw an exception, if the file is a directory" in {
      val path = Paths.get("inputData/download")
      val thrown = intercept[IoException] {
        IoUtils.checkFileExists(path, "zip")
      }

      thrown.getMessage == "The file inputData/download is a directory."
    }

    "throw an exception, if the file is not a zip archive" in {
      val folderPath = Paths.get(IoUtils.pwd, "testData/download")
      val filePath = Paths.get(IoUtils.pwd, "testData/download/test.txt")
      Files.createDirectories(folderPath)
      val file = Files.createFile(filePath)

      intercept[IoException] {
        IoUtils.checkFileExists(file, "zip")
      }
      /* Message cannot be tested, as the path is dependent of the location of the code. */

      Files.deleteIfExists(file)
    }
  }
}
