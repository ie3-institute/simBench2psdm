package edu.ie3.simbench.main

import java.nio.file.Paths

import edu.ie3.datamodel.io.TarballUtils
import edu.ie3.datamodel.io.csv.{
  DefaultInputHierarchy,
  FileNamingStrategy,
  HierarchicFileNamingStrategy
}
import edu.ie3.datamodel.io.sink.CsvFileSink
import edu.ie3.simbench.config.SimbenchConfig
import edu.ie3.simbench.convert.GridConverter
import edu.ie3.simbench.exception.CodeValidationException
import edu.ie3.simbench.io.{Downloader, IoUtils, SimbenchReader, Zipper}
import edu.ie3.simbench.model.SimbenchCode
import edu.ie3.util.io.FileIOUtils
import org.apache.commons.io.FilenameUtils

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
      logger.info(s"$simbenchCode - Downloading data set from SimBench website")
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

      logger.info(s"$simbenchCode - Reading in the SimBench data set")
      val simbenchReader = SimbenchReader(
        simbenchCode,
        dataFolder,
        simbenchConfig.io.input.csv.separator,
        simbenchConfig.io.input.csv.fileEnding,
        simbenchConfig.io.input.csv.fileEncoding
      )
      val simbenchModel = simbenchReader.readGrid()

      logger.info(s"$simbenchCode - Converting to PowerSystemDataModel")
      val (jointGridContainer, timeSeries, powerFlowResults) =
        GridConverter.convert(simbenchCode, simbenchModel)

      logger.info(s"$simbenchCode - Writing converted data set to files")
      /* Check, if a directory hierarchy is needed or not */
      val baseTargetDirectory =
        IoUtils.ensureHarmonizedAndTerminatingFileSeparator(
          simbenchConfig.io.output.targetFolder
        )
      val csvSink = if (simbenchConfig.io.output.csv.directoryHierarchy) {
        new CsvFileSink(
          baseTargetDirectory,
          new HierarchicFileNamingStrategy(
            new DefaultInputHierarchy(baseTargetDirectory, simbenchCode)
          ),
          false,
          simbenchConfig.io.output.csv.separator
        )
      } else {
        new CsvFileSink(
          baseTargetDirectory + simbenchCode,
          new FileNamingStrategy(),
          false,
          simbenchConfig.io.output.csv.separator
        )
      }

      csvSink.persistJointGrid(jointGridContainer)
      timeSeries.foreach(csvSink.persistTimeSeries(_))
      csvSink.persistAll(powerFlowResults.asJava)

      if (simbenchConfig.io.output.compress) {
        logger.info(s"$simbenchCode - Adding files to compressed archive")
        val rawOutputPath = Paths.get(baseTargetDirectory + simbenchCode)
        val archivePath = Paths.get(
          FilenameUtils.concat(baseTargetDirectory, simbenchCode + ".tar.gz")
        )
        TarballUtils.compress(rawOutputPath, archivePath)

        FileIOUtils.deleteRecursively(rawOutputPath)
      }
    }
  }
}
