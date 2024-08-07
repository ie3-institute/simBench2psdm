package edu.ie3.simbench.main

import edu.ie3.datamodel.io.naming.{
  DefaultDirectoryHierarchy,
  EntityPersistenceNamingStrategy,
  FileNamingStrategy
}
import edu.ie3.datamodel.io.sink.CsvFileSink
import edu.ie3.simbench.config.{ConfigValidator, SimbenchConfig}
import edu.ie3.simbench.convert.GridConverter
import edu.ie3.simbench.exception.CodeValidationException
import edu.ie3.simbench.io.{Downloader, IoUtils, SimbenchReader, Zipper}
import edu.ie3.simbench.model.SimbenchCode
import edu.ie3.simbench.model.datamodel.GridModel
import edu.ie3.util.io.FileIOUtils
import org.apache.commons.io.FilenameUtils

import java.nio.file.{Path, Paths}
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.jdk.CollectionConverters._
import scala.jdk.FutureConverters.CompletionStageOps
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
      val simbenchModel = readGridModel(simbenchCode, simbenchConfig.io.input)

      logger.info(s"$simbenchCode - Converting to PowerSystemDataModel")
      val (
        jointGridContainer,
        timeSeries,
        timeSeriesMapping,
        powerFlowResults
      ) =
        GridConverter.convert(
          simbenchCode,
          simbenchModel,
          simbenchConfig.conversion.removeSwitches
        )

      logger.info(s"$simbenchCode - Writing converted data set to files")

      val (csvSink, baseTargetDirectory) =
        createCsvSink(simbenchCode, simbenchConfig)
      csvSink.persistJointGrid(jointGridContainer)
      timeSeries.foreach(csvSink.persistTimeSeries(_))
      csvSink.persistAllIgnoreNested(timeSeriesMapping.asJava)
      csvSink.persistAll(powerFlowResults.asJava)

      if (simbenchConfig.io.output.compress) {
        compressCsv(simbenchCode, baseTargetDirectory)
      }
    }
  }

  /** Method for reading in or downloading the specified simbench grid model.
    * @param simbenchCode
    *   of the grid
    * @param cfg
    *   config that specifies the folder and the download optione
    * @return
    *   the read [[GridModel]]
    */
  private def readGridModel(
      simbenchCode: String,
      cfg: SimbenchConfig.Io.Input
  ): GridModel = {
    val dataFolder = if (cfg.useLocalFiles) {
      Path.of(cfg.folder, simbenchCode)
    } else {
      logger.info(s"$simbenchCode - Downloading data set from SimBench website")

      val downloader =
        Downloader(
          cfg.folder,
          cfg.download.baseUrl,
          cfg.download.failOnExistingFiles
        )
      val downloadedFile =
        downloader.download(
          SimbenchCode(simbenchCode).getOrElse(
            throw CodeValidationException(
              s"'$simbenchCode' is no valid SimBench code."
            )
          )
        )

      Zipper.unzip(
        downloadedFile,
        downloader.downloadFolder,
        cfg.download.failOnExistingFiles,
        flattenDirectories = true
      )
    }

    logger.info(s"$simbenchCode - Reading in the SimBench data set")
    val simbenchReader = SimbenchReader(
      simbenchCode,
      dataFolder,
      cfg.csv.separator,
      cfg.csv.fileEnding,
      cfg.csv.fileEncoding
    )

    simbenchReader.readGrid()
  }

  /** Method for creating a [[CsvFileSink]]
    * @param simbenchCode
    *   for the folder name
    * @param simbenchConfig
    *   config
    * @return
    *   a file sink and the baseTargetDirectory
    */
  private def createCsvSink(
      simbenchCode: String,
      simbenchConfig: SimbenchConfig
  ): (CsvFileSink, String) = {
    /* Check, if a directory hierarchy is needed or not */
    val baseTargetDirectory =
      IoUtils.ensureHarmonizedAndTerminatingFileSeparator(
        simbenchConfig.io.output.targetFolder
      )

    val csvSink = if (simbenchConfig.io.output.csv.directoryHierarchy) {
      new CsvFileSink(
        Path.of(baseTargetDirectory),
        new FileNamingStrategy(
          new EntityPersistenceNamingStrategy(),
          new DefaultDirectoryHierarchy(
            Path.of(baseTargetDirectory),
            simbenchCode
          )
        ),
        simbenchConfig.io.output.csv.separator
      )
    } else {
      new CsvFileSink(
        Path.of(baseTargetDirectory + simbenchCode),
        new FileNamingStrategy(),
        simbenchConfig.io.output.csv.separator
      )
    }

    (csvSink, baseTargetDirectory)
  }

  /** Method to compress a given PSDM grid.
    * @param simbenchCode
    *   of the grid
    * @param baseTargetDirectory
    *   of the grid
    */
  private def compressCsv(
      simbenchCode: String,
      baseTargetDirectory: String
  ): Unit = {
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
