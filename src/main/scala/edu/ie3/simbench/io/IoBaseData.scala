package edu.ie3.simbench.io

import scala.util.matching.Regex

trait IoBaseData {

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
