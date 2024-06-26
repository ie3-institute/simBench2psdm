package edu.ie3.simbench.main

import java.nio.file.Paths
import edu.ie3.datamodel.io.naming.{
  DefaultDirectoryHierarchy,
  EntityPersistenceNamingStrategy,
  FileNamingStrategy
}
import edu.ie3.datamodel.io.sink.CsvFileSink
import edu.ie3.datamodel.models.timeseries.TimeSeries
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries
import edu.ie3.simbench.config.{ConfigValidator, SimbenchConfig}
import edu.ie3.simbench.convert.GridConverter
import edu.ie3.simbench.exception.CodeValidationException
import edu.ie3.simbench.exception.io.IoException
import edu.ie3.simbench.io.IoUtils.writeMapToCsv
import edu.ie3.simbench.io.{Downloader, IoUtils, SimbenchReader, Zipper}
import edu.ie3.simbench.model.SimbenchCode
import edu.ie3.util.io.FileIOUtils
import org.apache.commons.io.FilenameUtils

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.jdk.CollectionConverters._
import scala.jdk.FutureConverters.CompletionStageOps
import scala.jdk.OptionConverters.RichOptional
import scala.util.{Failure, Success}

/** This is not meant to be final production code. It is more a place for
  * "testing" the full method stack.
  */
object RunSimbench extends SimbenchHelper {
  def main(args: Array[String]): Unit = {
    printOpener()

    logger.info("Parsing the config")
    val (_, config) = prepareConfig(args)
    val simbenchConfig = SimbenchConfig(config)

    /* Validate the config */
    ConfigValidator.checkValidity(simbenchConfig)

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
      val (
        jointGridContainer,
        timeSeries,
        timeSeriesMapping,
        timeSeriesIdMapping,
        powerFlowResults
      ) =
        GridConverter.convert(
          simbenchCode,
          simbenchModel,
          simbenchConfig.conversion.removeSwitches
        )

      logger.info(s"$simbenchCode - Writing converted data set to files")

      /* Check, if a directory hierarchy is needed or not */
      val baseTargetDirectory =
        IoUtils.ensureHarmonizedAndTerminatingFileSeparator(
          simbenchConfig.io.output.targetFolder
        )

      val hierarchyAdjustedBaseDir = if (simbenchConfig.io.output.csv.directoryHierarchy) baseTargetDirectory else baseTargetDirectory + simbenchCode

      val fileNamingStrategy =
        if (simbenchConfig.io.output.csv.directoryHierarchy) {
          new FileNamingStrategy(
            new EntityPersistenceNamingStrategy(),
            new DefaultDirectoryHierarchy(baseTargetDirectory, simbenchCode)
          )
        } else {
          new FileNamingStrategy()
        }

      val csvSink = new CsvFileSink(
        hierarchyAdjustedBaseDir,
        fileNamingStrategy,
        false,
        simbenchConfig.io.output.csv.separator
      )

      csvSink.persistJointGrid(jointGridContainer)
      timeSeries.foreach(csvSink.persistTimeSeries(_))
      csvSink.persistAllIgnoreNested(timeSeriesMapping.asJava)
      csvSink.persistAll(powerFlowResults.asJava)

      val timeSeriesPath = fileNamingStrategy
          .getDirectoryPath(
            classOf[IndividualTimeSeries[_]]
          )
          .toScala match {
          case Some(timeSeriesDir) => Paths.get(hierarchyAdjustedBaseDir, timeSeriesDir)
          case None => Paths.get(hierarchyAdjustedBaseDir)
        }

      writeMapToCsv(timeSeriesIdMapping, timeSeriesPath)

      if (simbenchConfig.io.output.compress) {
        logger.info(s"$simbenchCode - Adding files to compressed archive")
        val rawOutputPath = Paths.get(baseTargetDirectory + simbenchCode)
        val archivePath = Paths.get(
          FilenameUtils.concat(baseTargetDirectory, simbenchCode + ".tar.gz")
        )
        val compressFuture =
          FileIOUtils.compressDir(rawOutputPath, archivePath).asScala
        compressFuture.onComplete {
          case Success(_) =>
            FileIOUtils.deleteRecursively(rawOutputPath)
          case Failure(exception) =>
            logger.error(
              s"Compression of output files to '$archivePath' has failed. Keep raw data.",
              exception
            )
        }

        Await.ready(compressFuture, Duration("180s"))
      }
    }
  }
}
