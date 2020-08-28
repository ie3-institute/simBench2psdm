/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.simbench.concurrent

import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.stream.scaladsl.Source
import edu.ie3.simbench.config.SimbenchConfig
import edu.ie3.simbench.convert.GridConverter
import edu.ie3.simbench.exception.CodeValidationException
import edu.ie3.simbench.io.{Downloader, SimbenchReader}
import edu.ie3.simbench.main.SimbenchHelper
import edu.ie3.simbench.model.SimbenchCode

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.language.postfixOps

/**
  * Class to convert SimBench models in a standalone, but concurrent fashion. The Akka Stream API is used, because it
  * provides backpressure functionalities directly.
  */
object RunSimbenchConcurrent extends App with SimbenchHelper {
  /* Setting up the actor system */
  implicit val system: ActorSystem = ActorSystem("simbench-streaming")
  implicit val ec: ExecutionContextExecutor = system.dispatcher

  /* Parsing the configuration based on the given location by args */
  logger.info("Parsing the config")
  val (_, config) = prepareConfig(args)
  val simbenchConfig = SimbenchConfig(config)

  /* Produce a source, that emits the SimBench codes meant to be converted */
  val source: Source[String, NotUsed] = Source(simbenchConfig.io.simbenchCodes)

  /* Actually run the Stream and do the processions. Only 10 elements are treated at the same time */
  val done: Future[Done] =
    source
    /* Buffer, that only some elements are in the buffer, put backpressure, if consumer is not fast enough */
//      .buffer(10, backpressure)
    /* Download files */
      .mapAsyncUnordered(10) { simbenchCode =>
        Future {
          logger.info(
            s"$simbenchCode - Downloading data set from SimBench website"
          )
          val downloader =
            Downloader(
              simbenchConfig.io.downloadFolder,
              simbenchConfig.io.baseUrl
            )
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
            Downloader.unzip(
              downloader,
              downloadedFile,
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
          GridConverter.convert(simbenchCode, simbenchModel)
        }
      }
      .runForeach {
        case (jointGridContainer, timeSeries, powerFlowResults) =>
          println(
            s"Converted data set with ${jointGridContainer.getRawGrid.getNodes.size} nodes, ${timeSeries.size} individual time series and ${powerFlowResults.size}"
          )
      }

  /* Terminate the actor system on ending the stream */
  done onComplete (_ => system.terminate())
}
