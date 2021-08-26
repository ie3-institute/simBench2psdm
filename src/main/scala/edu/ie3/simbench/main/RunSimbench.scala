package edu.ie3.simbench.main

import akka.actor.typed.ActorSystem

import edu.ie3.simbench.actor.Coordinator
import edu.ie3.simbench.actor.Coordinator.{CoordinatorMessage, Start}
import edu.ie3.simbench.config.{ConfigValidator, SimbenchConfig}

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

    /* Start the actor system */
    val actorSystem = ActorSystem[CoordinatorMessage](
      Coordinator(),
      "Coordinator"
    )
    actorSystem ! Start(simbenchConfig)

//    simbenchConfig.io.simbenchCodes.foreach { simbenchCode =>
//      logger.info(s"$simbenchCode - Downloading data set from SimBench website")
//      val downloader =
//        Downloader(
//          simbenchConfig.io.input.download.folder,
//          simbenchConfig.io.input.download.baseUrl,
//          simbenchConfig.io.input.download.failOnExistingFiles
//        )
//      val downloadedFile =
//        downloader.download(
//          SimbenchCode(simbenchCode).getOrElse(
//            throw CodeValidationException(
//              s"'$simbenchCode' is no valid SimBench code."
//            )
//          )
//        )
//      val dataFolder =
//        Zipper.unzip(
//          downloadedFile,
//          downloader.downloadFolder,
//          simbenchConfig.io.input.download.failOnExistingFiles,
//          flattenDirectories = true
//        )
//
//      logger.info(s"$simbenchCode - Reading in the SimBench data set")
//      val simbenchReader = SimbenchReader(
//        simbenchCode,
//        dataFolder,
//        simbenchConfig.io.input.csv.separator,
//        simbenchConfig.io.input.csv.fileEnding,
//        simbenchConfig.io.input.csv.fileEncoding
//      )
//      val simbenchModel = simbenchReader.readGrid()
//
//      logger.info(s"$simbenchCode - Converting to PowerSystemDataModel")
//      val (
//        jointGridContainer,
//        timeSeries,
//        timeSeriesMapping,
//        powerFlowResults
//      ) =
//        GridConverter.convert(
//          simbenchCode,
//          simbenchModel,
//          simbenchConfig.conversion.removeSwitches
//        )
//
//      logger.info(s"$simbenchCode - Writing converted data set to files")
//
//      csvSink.persistJointGrid(jointGridContainer)
//      timeSeries.foreach(csvSink.persistTimeSeries(_))
//      csvSink.persistAllIgnoreNested(timeSeriesMapping.asJava)
//      csvSink.persistAll(powerFlowResults.asJava)
//    }
  }
}
