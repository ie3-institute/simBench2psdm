package edu.ie3.simbench.main

import edu.ie3.datamodel.io.naming.{
  DefaultDirectoryHierarchy,
  EntityPersistenceNamingStrategy,
  FileNamingStrategy
}
import edu.ie3.datamodel.io.sink.CsvFileSink
import edu.ie3.simbench.config.{ConfigValidator, SimbenchConfig}
import edu.ie3.simbench.convert.GridConverter
import edu.ie3.simbench.io.{IoUtils, SimbenchModelRetriever}
import edu.ie3.util.io.FileIOUtils
import org.apache.commons.io.FilenameUtils

import java.nio.file.Paths
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.jdk.CollectionConverters._
import scala.jdk.FutureConverters.CompletionStageOps
import scala.reflect.io.Directory
import scala.util.{Failure, Success}

/**
  * This is not meant to be final production code. It is more a place for "testing" the full method stack.
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
      val simbenchModel = SimbenchModelRetriever.retrieve(
        simbenchCode,
        simbenchConfig
      )

      logger.info(s"$simbenchCode - Converting to PowerSystemDataModel")
      val (
        jointGridContainer,
        timeSeries,
        timeSeriesMapping,
        powerFlowResults
      ) =
        GridConverter.convert(
          simbenchCode,
          simbenchConfig,
          simbenchModel,
          simbenchConfig.conversion.removeSwitches
        )

      logger.info(s"$simbenchCode - Writing converted data set to files")
      /* Check, if a directory hierarchy is needed or not */
      val baseTargetDirectory =
        IoUtils.ensureHarmonizedAndTerminatingFileSeparator(
          simbenchConfig.io.output.targetFolder
        )

      val targetDirectory =
        Paths.get(baseTargetDirectory, simbenchCode, "input")
      val directory = new Directory(targetDirectory.toFile)
      directory.deleteRecursively()

      val csvSink = if (simbenchConfig.io.output.csv.directoryHierarchy) {
        new CsvFileSink(
          baseTargetDirectory,
          new FileNamingStrategy(
            new EntityPersistenceNamingStrategy(),
            new DefaultDirectoryHierarchy(baseTargetDirectory, simbenchCode)
          ),
          false,
          simbenchConfig.io.output.csv.separator
        )
      } else {
        new CsvFileSink(
          targetDirectory.toString,
          new FileNamingStrategy(),
          false,
          simbenchConfig.io.output.csv.separator
        )
      }

      csvSink.persistJointGrid(jointGridContainer)
      timeSeries.foreach(csvSink.persistTimeSeries(_))
      csvSink.persistAllIgnoreNested(timeSeriesMapping.asJava)
      csvSink.persistAll(powerFlowResults.asJava)

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
