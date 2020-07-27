package edu.ie3.simbench.main

import edu.ie3.simbench.config.SimbenchConfig
import edu.ie3.simbench.exception.CodeValidationException
import edu.ie3.simbench.io.{Downloader, SimbenchReader}
import edu.ie3.simbench.model.SimbenchCode

/**
  * This is not meant to be final production code. It is more a place for "testing" the full method stack.
  */
object RunSimbench extends SimbenchHelper {
  def main(args: Array[String]): Unit = {
    printOpener()

    val (_, config) = prepareConfig(args)
    val simbenchConfig = SimbenchConfig(config)

    val downloader =
      Downloader(simbenchConfig.io.downloadFolder, simbenchConfig.io.baseUrl)
    val downloadedFile =
      Downloader.download(
        downloader,
        SimbenchCode("1-EHVHVMVLV-mixed-all-0-sw").getOrElse(
          throw CodeValidationException(
            "'1-EHVHVMVLV-mixed-all-0-sw' is no valid SimBench code."
          )
        )
      )
    val dataFolder =
      Downloader.unzip(downloader, downloadedFile, flattenDirectories = true)
    val simbenchReader = SimbenchReader(
      dataFolder,
      simbenchConfig.io.input.csv.separator,
      simbenchConfig.io.input.csv.fileEnding,
      simbenchConfig.io.input.csv.fileEncoding
    )
    val model = simbenchReader.readGrid()
  }
}
