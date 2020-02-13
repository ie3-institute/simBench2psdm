package edu.ie3.simbench.io

import java.nio.file.{Files, Path, Paths}

import edu.ie3.simbench.exception.io.IoException

import scala.util.matching.Regex

trait IoUtils {

  /** This regex matches the string between the last file separator and the file extension e.g "zip"
    * The dot is added automatically
    */
  val fileNameRegex: String => Regex = (fileEnding: String) => {
    val strippedEnding = fileEnding match {
      case _ if fileEnding.startsWith(".") => fileEnding.replaceFirst("\\.", "")
      case _                               => fileEnding
    }
    ("[^/\\\\\\s]+(?=\\." + strippedEnding + "$)").r
  }

  /** This regex matches the string between the last file separator and the file extension e.g "zip"
    * The dot is added automatically
    */
  val fileNameRegexWithEnding: String => Regex = (fileEnding: String) => {
    val strippedEnding = fileEnding match {
      case _ if fileEnding.startsWith(".") => fileEnding.replaceFirst("\\.", "")
      case _                               => fileEnding
    }

    ("[^/\\\\\\s]+\\." + strippedEnding + "$").r
  }

  /**
    * Regex to filter all file names with ending, including any extension
    */
  val fileNameRegexWithAnyEnding: Regex = fileNameRegexWithEnding("[\\w\\d]+")
}

object IoUtils {

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
    if (!Files.exists(filePath)) {
      throw IoException(s"The file $filePath does not exist.")
    }
    if (Files.isDirectory(filePath)) {
      throw IoException(s"The file $filePath is a directory.")
    }
    if (!filePath.toAbsolutePath.toString.endsWith(ending)) {
      throw IoException(
        s"The file $filePath is of wrong file type. Only $ending is supported (case sensitive).")
    }
  }
}
