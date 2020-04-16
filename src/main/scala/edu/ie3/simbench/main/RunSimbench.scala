package edu.ie3.simbench.main

import edu.ie3.simbench.config.SimbenchConfig
import edu.ie3.simbench.io.{Downloader, SimbenchReader}

object RunSimbench extends SimbenchHelper {
  def main(args: Array[String]): Unit = {
    printOpener()

    val (_, config) = prepareConfig(args)
    val simbenchConfig = SimbenchConfig(config)

    val downloader =
      Downloader(simbenchConfig.io.downloadFolder, simbenchConfig.io.baseUrl)
    val downloadedFile =
      Downloader.download(downloader, "1-EHVHVMVLV-mixed-all-0-sw")
    val dataFolder =
      Downloader.unzip(downloader, downloadedFile, flattenDirectories = true)
    val simbenchReader = SimbenchReader(
      dataFolder,
      simbenchConfig.io.csv.separator,
      simbenchConfig.io.csv.fileEnding,
      simbenchConfig.io.csv.fileEncoding
    )
    val model = simbenchReader.readGrid()
  }
}
