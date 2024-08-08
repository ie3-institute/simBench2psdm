package edu.ie3.simbench.main

import edu.ie3.simbench.config.{ConfigValidator, SimbenchConfig}
import edu.ie3.simbench.convert.GridConverter
import edu.ie3.simbench.io.SimbenchReader

import scala.jdk.CollectionConverters._

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
      val gridPath = getGridPath(simbenchCode, simbenchConfig.io.input)

      logger.info(s"$simbenchCode - Reading in the SimBench data set")
      val simbenchReader = SimbenchReader(
        simbenchCode,
        gridPath,
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
}
