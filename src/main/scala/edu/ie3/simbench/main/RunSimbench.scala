/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.simbench.main

import edu.ie3.datamodel.io.FileNamingStrategy
import edu.ie3.datamodel.io.sink.CsvFileSink
import edu.ie3.simbench.config.SimbenchConfig
import edu.ie3.simbench.convert.GridConverter
import edu.ie3.simbench.exception.CodeValidationException
import edu.ie3.simbench.io.{Downloader, IoUtils, SimbenchReader}
import edu.ie3.simbench.model.SimbenchCode

import scala.jdk.CollectionConverters._

/**
  * This is not meant to be final production code. It is more a place for "testing" the full method stack.
  */
object RunSimbench extends SimbenchHelper {
  def main(args: Array[String]): Unit = {
    logger.info("Parsing the config")
    val (_, config) = prepareConfig(args)
    val simbenchConfig = SimbenchConfig(config)

    simbenchConfig.io.simbenchCodes.foreach { simbenchCode =>
      logger.info(s"Downloading data set '$simbenchCode' from SimBench website")
      val downloader =
        Downloader(simbenchConfig.io.downloadFolder, simbenchConfig.io.baseUrl)
      val downloadedFile =
        Downloader.download(
          downloader,
          SimbenchCode(simbenchCode).getOrElse(
            throw CodeValidationException(
              s"'$simbenchCode' is no valid SimBench code."
            )
          )
        )
      val dataFolder =
        Downloader.unzip(downloader, downloadedFile, flattenDirectories = true)

      logger.info(s"Reading in the SimBench data set '$simbenchCode'")
      val simbenchReader = SimbenchReader(
        simbenchCode,
        dataFolder,
        simbenchConfig.io.input.csv.separator,
        simbenchConfig.io.input.csv.fileEnding,
        simbenchConfig.io.input.csv.fileEncoding
      )
      val simbenchModel = simbenchReader.readGrid()

      logger.info(s"Converting '$simbenchCode' to PowerSystemDataModel")
      val (jointGridContainer, timeSeries, powerFlowResults) =
        GridConverter.convert(simbenchCode, simbenchModel)

      logger.info(s"Writing converted data set '$simbenchCode' to files")
      val targetFolderPath = "[/\\\\]$|(?<![/\\\\])$".r.replaceAllIn(
        IoUtils.harmonizeFileSeparator(simbenchConfig.io.output.targetFolder),
        "/" + simbenchCode
      )

      val csvSink = new CsvFileSink(
        targetFolderPath,
        new FileNamingStrategy(),
        false,
        simbenchConfig.io.output.csv.separator
      )
      csvSink.persistJointGrid(jointGridContainer)
      timeSeries.foreach(csvSink.persistTimeSeries(_))
      csvSink.persistAll(powerFlowResults.asJava)
    }
  }
}
