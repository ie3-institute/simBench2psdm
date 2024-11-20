package edu.ie3.simbench.io

import java.net.URL
import java.io.File
import java.nio.file.{Files, Paths, StandardCopyOption}
import scala.io.Source
import scala.util.matching.Regex

final case class ExtractorConfig(downloadLink: String, outputPath: String)

class Extractor(config: ExtractorConfig) {

  def download(): Unit = {
    val url = new URL(
      "https://daks.uni-kassel.de/bitstreams/b1fb0ccf-94a1-4d5e-921c-5f9fa44e5371/download"
    )
    val inputStream = url.openStream()

    try {
      val path = Paths.get(config.outputPath)
      Files.createDirectories(path.getParent)
      Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING)
      println(s"File downloaded successfully to ${config.outputPath}")
    } finally {
      inputStream.close()
    }
  }

  /** Extracts a map of simbench-code to UUID from the downloaded file.
    */
  def extractUUIDMap(): Map[String, String] = {
    val uuidPattern: Regex =
      """[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}""".r

    val source = Source.fromFile(config.outputPath)
    val lines = source.getLines().toList

    val header = lines.head.split(",").map(_.trim)
    val data = lines.tail

    val codeIndex = header.indexOf("code")
    val csvIndex = header.indexOf("csv")

    if (codeIndex == -1 || csvIndex == -1) {
      throw new IllegalArgumentException(
        "The required columns ('code', 'csv') are missing."
      )
    }

    // Extract the map
    val dataMap: Map[String, String] = data.flatMap { line =>
      val columns = line.split(",").map(_.trim)
      if (columns.length > csvIndex) {
        val code = columns(codeIndex)
        val csv = columns(csvIndex)
        val uuidOpt = uuidPattern.findFirstIn(csv)
        uuidOpt.map(uuid =>
          code -> uuid
        ) // Create key-value pair only if UUID exists
      } else None
    }.toMap

    source.close()
    dataMap
  }
}
