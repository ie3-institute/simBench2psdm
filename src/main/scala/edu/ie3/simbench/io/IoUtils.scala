package edu.ie3.simbench.io

import java.nio.file.{Files, Path, Paths}

import edu.ie3.simbench.exception.io.IoException

import scala.util.matching.Regex

trait IoUtils {

  /**
    * Regex to detect the file separators
    */
  protected val fileSeparatorRegex: Regex = "[/\\\\]+".r

  /**
    * Regex to detect everything between, in front of or after a file separator
    */
  protected val everythingExceptSeparatorRegex: Regex = "[^/\\\\\\s]+".r

  /**
    * A regex marking a single string without folder separator or dot
    */
  protected val singleStringRegex: Regex = "^(?<![^.\\\\/\\s])\\w+$".r

  /**
    * Find "\." or "/." in a string
    */
  protected val currentDirDotPattern: Regex = "[/\\\\]\\.".r

  /**
    * Get the working directory string and remove the "current directory marker"
    */
  val pwd: String = currentDirDotPattern.replaceAllIn(
    Paths.get(".").toAbsolutePath.toString,
    ""
  )

  /**
    * Harmonize all contained file separators to "/"
    */
  val harmonizeFileSeparator: String => String = (pathString: String) =>
    pathString.replaceAll(fileSeparatorRegex.toString(), "/")

  /**
    * Harmonizes the file separators and ensures, that the last character is a file separator
    */
  val ensureHarmonizedAndTerminatingFileSeparator: String => String =
    (path: String) => {
      val harmonizedInput = harmonizeFileSeparator(path)
      if (harmonizedInput.endsWith("/"))
        harmonizedInput
      else
        harmonizedInput + "/"
    }

  /**
    * Removes the dot from a file provided file ending string
    */
  val getFileExtensionWithoutDot: String => String =
    (fullyQualifiedPath: String) =>
      (singleStringRegex.pattern.toString + "|" + "(?<=\\.)\\w+$").r
        .findFirstIn(fullyQualifiedPath) match {
        case Some(endingWithoutDot) => endingWithoutDot
        case None =>
          throw IoException(
            "The provided path string (" + fullyQualifiedPath + ") does not match the pattern .*\\.\\w+$, why no ending can be extracted."
          )
      }

  /**
    * This regex matches the string between the last file separator and the file extension e.g "zip".
    * The dot is added automatically
    */
  val fileNameRegex: String => Regex = (fileEnding: String) =>
    (singleStringRegex.pattern.toString + "|" + everythingExceptSeparatorRegex.pattern.toString + "(?=\\." + getFileExtensionWithoutDot(
      fileEnding
    ) + "$)").r

  /**
    * This regex matches the string after the last file separator (including the file extension).
    * The dot is added automatically
    */
  val fileNameRegexWithEnding: String => Regex = (fileEnding: String) =>
    (singleStringRegex.pattern.toString + "|" + everythingExceptSeparatorRegex.pattern.toString + "\\." + getFileExtensionWithoutDot(
      fileEnding
    ) + "$").r

  /**
    * Regex to filter all file names without any ending
    */
  val fileNameRegexWithoutAnyEnding: Regex =
    (singleStringRegex.pattern.toString + "|" + everythingExceptSeparatorRegex.pattern.toString + "(?=\\.\\w+$)").r

  /**
    * Regex to filter all file names with ending, including any extension
    */
  val fileNameRegexWithAnyEnding: Regex =
    (singleStringRegex.pattern.toString + "|" + everythingExceptSeparatorRegex.pattern.toString + "\\.\\w+$").r

  val folderNameRegex: Regex = "[\\w@.-]+".r

  /**
    * Regex to detect a fully qualified path consisting of a folder path followed by a file name with extension
    */
  val fullyQualifiedPathRegex: Regex =
    ("^(?:\\w\\:" + fileSeparatorRegex.pattern.toString + "+|" + fileSeparatorRegex.pattern.toString + ")(?:" + folderNameRegex.pattern.toString + fileSeparatorRegex.pattern.toString + ")*(?:\\w+\\.\\w+)$").r

  /**
    * Identifying a path pointing to a folder and not to a specific file. It consists of three non-capturing groups (?:):
    * 1) Start of a valid folder string ("C:", "/" or a simple word)
    * 2) Any allowed intermediate string (including "@", "." and "-" to allow urls in the name) followed by a file
    *    separator
    * 3) A simple word at the end (including "@", "." and "-" to allow urls in the name)
    */
  val folderPathWithoutLastSeparator: Regex =
    ("^(?:\\w\\:" + fileSeparatorRegex.pattern.toString + "+|" + fileSeparatorRegex.pattern.toString + ")(?:" + folderNameRegex.pattern.toString + fileSeparatorRegex.pattern.toString + ")*(?:" + folderNameRegex.pattern.toString + ")$").r

  /**
    * Removes a leading file separator at a fully qualified windows file path (e.g. "/C:" to "C:")
    */
  val trimFirstSeparatorInWindowsPath: String => String = (path: String) =>
    ("^" + fileSeparatorRegex.pattern.toString + "(?=\\w+:)").r
      .replaceAllIn(path, "")

  /**
    * Removes a file separator at the very end of the path string
    */
  val trimLastSeparator: String => String = (path: String) =>
    (fileSeparatorRegex.pattern.toString + "$").r.replaceAllIn(path, "")

  /**
    * Removes the last file separator, harmonizes all remaining file separators and checks the conformity of the string
    * with a valid folder path pattern
    */
  val prepareFolderPath: String => String = (folderPath: String) => {
    val checkedFolderPath = harmonizeFileSeparator(
      trimFirstSeparatorInWindowsPath(trimLastSeparator(folderPath))
    )
    if (!folderPathWithoutLastSeparator.matches(checkedFolderPath))
      throw IoException(
        s"Cannot determine correct fully qualified folder path from $folderPath"
      )
    checkedFolderPath
  }
}

object IoUtils extends IoUtils {

  /**
    * Composes a fully qualified path consisting of folder path, file name and extension
    *
    * @param folderPath Path to the folder, where the file is located
    * @param fileName   Name of the file
    * @param extension  Extension of the file
    * @return A fully qualified path
    */
  def composeFullyQualifiedPath(
      folderPath: String,
      fileName: String,
      extension: String
  ): String = {
    /* Remove last separator from file path if any */
    val checkedFolderPath = prepareFolderPath(folderPath)

    val checkedFileName =
      fileNameRegexWithoutAnyEnding.findFirstIn(fileName) match {
        case Some(value) => value
        case None =>
          throw IoException(
            s"Cannot determine the file name without extension in $fileName"
          )
      }
    val checkedExtension = getFileExtensionWithoutDot(extension)

    val fullyQualifiedPath = checkedFolderPath + "/" + checkedFileName + "." + checkedExtension
    if (!fullyQualifiedPathRegex.matches(fullyQualifiedPath))
      throw IoException(
        s"The composed fully qualified path ($fullyQualifiedPath) is not an actual qualified path..."
      )
    fullyQualifiedPath
  }

  /**
    * Checks, whether object at the provided path exists and is a file of proper ending
    *
    * @param path   String path to the object to check
    * @param ending Desired file ending
    */
  def checkFileExists(path: String, ending: String): Unit = {
    val filePath = Paths.get(path)
    checkFileExists(filePath, ending)
  }

  /**
    * Checks, whether object at the provided path exists and is a file of proper ending
    *
    * @param filePath Path to the object to check
    * @param ending   Desired file ending
    */
  def checkFileExists(filePath: Path, ending: String): Unit = {
    val checkedEnding = "." + getFileExtensionWithoutDot(ending)

    if (!Files.exists(filePath)) {
      throw IoException(s"The file $filePath does not exist.")
    }
    if (Files.isDirectory(filePath)) {
      throw IoException(s"The file $filePath is a directory.")
    }
    if (!filePath.toAbsolutePath.toString.endsWith(checkedEnding)) {
      throw IoException(
        s"The file $filePath is of wrong file type. Only $checkedEnding is supported (case sensitive)."
      )
    }
  }
}
