package edu.ie3.simbench.main

import com.typesafe.config.{ConfigFactory, Config as TypesafeConfig}
import com.typesafe.scalalogging.LazyLogging
import edu.ie3.datamodel.io.naming.{
  DefaultDirectoryHierarchy,
  EntityPersistenceNamingStrategy,
  FileNamingStrategy
}
import edu.ie3.datamodel.io.sink.CsvFileSink
import edu.ie3.simbench.config.ArgsParser.Arguments
import edu.ie3.simbench.config.{ArgsParser, SimbenchConfig}
import edu.ie3.simbench.exception.CodeValidationException
import edu.ie3.simbench.exception.io.SimbenchConfigException
import edu.ie3.simbench.io.{Downloader, Extractor, IoUtils, Zipper}
import edu.ie3.simbench.model.SimbenchCode
import edu.ie3.util.io.FileIOUtils
import edu.ie3.util.quantities.QuantityAdjustments
import org.apache.commons.io.FilenameUtils

import java.nio.file.{Path, Paths}
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.jdk.FutureConverters.CompletionStageOps
import scala.util.{Failure, Success}

trait SimbenchHelper extends LazyLogging {

  /** Adjust the quantity library to be able to "understand" the Scala number
    * system
    */
  QuantityAdjustments.adjust()

  def prepareConfig(args: Array[String]): (Arguments, TypesafeConfig) = {

    val parsedArgs = ArgsParser.parse(args) match {
      case Some(pArgs) => pArgs
      case None =>
        System.exit(-1)
        throw new IllegalArgumentException(
          "Unable to parse provided Arguments."
        )
    }

    // ConfigConsistencyComparator.parseBeamTemplateConfFile(parsedArgs.configLocation.get) // todo implement

    // check if a config is provided
    val parsedArgsConfig = parsedArgs.config match {
      case None =>
        throw new SimbenchConfigException(
          "Please provide a valid config file via --config <path-to-config-file>."
        )
      case Some(parsedArgsConfig) => parsedArgsConfig
    }

    // set config file location as system property // todo do we need this? (same is valid for additional parsing below)
    System.setProperty(
      "configFileLocation",
      parsedArgs.configLocation.getOrElse("")
    )

    val configFromLocationPath =
      ConfigFactory.parseString(
        s"config=${parsedArgs.configLocation.getOrElse(throw new SimbenchConfigException("Cannot get config location from configuration!"))}"
      )

    // note: this overrides the default config values provided in the config file!
    // THE ORDER OF THE CALLS MATTERS -> the later the call, the more "fallback" -> first config is always the primary one!
    // hence if you add some more program arguments, you have to add them before(!) your default config!
    // see https://github.com/lightbend/config#merging-config-trees for details on merging configs
    val config = parsedArgsConfig
      .withFallback(configFromLocationPath)
      .resolve()

    (parsedArgs, config)
  }

  def printOpener(): Unit = {
    println("  ___                   ___  ")
    println(" (o o)                 (o o) ")
    println("(  V  ) SimBench2PDSM (  V  )")
    println("--m-m-------------------m-m--")
  }

  /** Method for returning the grid path. If downloading is used, the specified
    * simbench grid model will be automatically downloaded.
    * @param simbenchCode
    *   of the grid
    * @param cfg
    *   config that specifies the folder and the download option
    * @return
    *   the path to the grid
    */
  private[main] def getGridPath(
      simbenchCode: String,
      cfg: SimbenchConfig.Io.Input
  ): Path = {
    (cfg.localFile, cfg.download) match {
      case (Some(local), None) =>
        if local.isZipped then {
          Zipper.unzip(
            Path.of(cfg.directory, simbenchCode + ".zip"),
            cfg.directory,
            local.failOnExistingFiles,
            flattenDirectories = true
          )
        } else {
          Path.of(cfg.directory, simbenchCode)
        }
      case (None, Some(download)) =>
        logger.info(
          s"$simbenchCode - Downloading data set from SimBench website"
        )

        val extractor = new Extractor(cfg)
        extractor.download()
        val uuidMap = extractor.extractUUIDMap()

        val downloadCfg = cfg.download.getOrElse(
          throw new SimbenchConfigException(
            s"There was no download configuration provided!"
          )
        )

        val downloader =
          Downloader(
            cfg.directory,
            downloadCfg.baseUrl,
            uuidMap,
            downloadCfg.failOnExistingFiles
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
          downloader.downloadDir,
          download.failOnExistingFiles,
          flattenDirectories = true
        )
    }
  }

  /** Method for creating a [[CsvFileSink]]
    * @param simbenchCode
    *   for the folder name
    * @param simbenchConfig
    *   config that specifies the output path
    * @return
    *   a file sink and the baseTargetDirectory
    */
  private[main] def createCsvSink(
      simbenchCode: String,
      simbenchConfig: SimbenchConfig
  ): (CsvFileSink, String) = {
    /* Check, if a directory hierarchy is needed or not */
    val baseTargetDirectory =
      IoUtils.ensureHarmonizedAndTerminatingFileSeparator(
        simbenchConfig.io.output.targetDir
      )

    val csvSink = if simbenchConfig.io.output.csv.directoryHierarchy then {
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
  private[main] def compressCsv(
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
