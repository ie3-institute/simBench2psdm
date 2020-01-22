package edu.ie3.simbench.main

import edu.ie3.simbench.config.SimbenchConfig
import edu.ie3.simbench.io.Downloader

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object RunSimbench extends SimbenchHelper {
  def main(args: Array[String]): Unit = {
    printOpener()

    val (_, config) = prepareConfig(args)
    val simbenchConfig = SimbenchConfig(config)

    val downloader =
      Downloader(simbenchConfig.io.downloadFolder, simbenchConfig.io.baseUrl)
    val downloadedFile =
      Downloader.download(downloader, "1-EHVHVMVLV-mixed-all-0-sw")
    Downloader.unzip(downloader, downloadedFile, flattenDirectories = true)
  }
}
