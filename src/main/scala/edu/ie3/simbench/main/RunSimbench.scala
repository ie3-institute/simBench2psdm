package edu.ie3.simbench.main

import edu.ie3.datamodel.io.FileNamingStrategy
import edu.ie3.datamodel.io.sink.CsvFileSink
import edu.ie3.simbench.config.SimbenchConfig
import edu.ie3.simbench.convert.GridConverter
import edu.ie3.simbench.exception.CodeValidationException
import edu.ie3.simbench.io.{Downloader, IoUtils, SimbenchReader, Zipper}
import edu.ie3.simbench.model.SimbenchCode

import scala.jdk.CollectionConverters._

/**
  * This is not meant to be final production code. It is more a place for "testing" the full method stack.
  */
object RunSimbench extends SimbenchHelper {
  def main(args: Array[String]): Unit = {
    printOpener()

    logger.info("Parsing the config")
    val (_, config) = prepareConfig(args)
    val simbenchConfig = SimbenchConfig(config)

    simbenchConfig.io.simbenchCodes.foreach { simbenchCode =>
      logger.info(s"Downloading data set '$simbenchCode' from SimBench website")
      val downloader =
        Downloader(
          simbenchConfig.io.input.download.folder,
          simbenchConfig.io.input.download.baseUrl,
          simbenchConfig.io.input.download.failOnExistingFiles
        )
      val downloadedFile =
        downloader.download(
          SimbenchCode(simbenchCode).getOrElse(
            throw CodeValidationException(
              s"'$simbenchCode' is no valid SimBench code."
            )
          )
        )
      val dataFolder =
        Zipper.unzip(
          downloadedFile,
          downloader.downloadFolder,
          simbenchConfig.io.input.download.failOnExistingFiles,
          flattenDirectories = true
        )

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
      val targetFolderPath =
        IoUtils.ensureHarmonizedAndTerminatingFileSeparator(
          simbenchConfig.io.output.targetFolder
        ) + simbenchCode

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
