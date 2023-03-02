package edu.ie3.simbench.io

import com.typesafe.scalalogging.LazyLogging
import edu.ie3.simbench.config.SimbenchConfig
import edu.ie3.simbench.exception.CodeValidationException
import edu.ie3.simbench.model.SimbenchCode
import edu.ie3.simbench.model.datamodel.GridModel

object SimbenchModelRetriever extends LazyLogging {

  def retrieve(
      simbenchCode: String,
      simbenchConfig: SimbenchConfig
  ): GridModel = {
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

    simbenchReader.readGrid()
  }

}
