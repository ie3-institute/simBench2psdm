package edu.ie3.simbench.io

import java.io.File
import java.net.URL
import java.nio.file.{Path, Paths}

import com.typesafe.scalalogging.LazyLogging
import edu.ie3.simbench.exception.io.DownloaderException
import edu.ie3.simbench.model.SimbenchCode

import scala.language.postfixOps
import scala.sys.process._

final case class Downloader(
    downloadFolder: String,
    baseUrl: String,
    failOnExistingFiles: Boolean = true
) extends LazyLogging {

  /** Download the data set for the specified simbenchCode. The code has to be a
    * valid code! It is not possible to check, if the URL is correct, because
    * the SimBench website does redirect.
    * @param simbenchCode
    *   A valid SimBench code
    */
  def download(simbenchCode: SimbenchCode): Path = {
    val downloadFolderPath = new File(s"$downloadFolder/")
    val downloadPath =
      Paths.get(
        s"${downloadFolderPath.getAbsolutePath}/${simbenchCode.code}.zip"
      )
    val downloadFile = downloadPath.toFile
    if (downloadFolderPath.mkdirs()) {
      logger.debug("Created all non existing folders")
    }

    if (failOnExistingFiles && downloadFile.exists())
      throw DownloaderException(
        s"Cannot download to file '${downloadFile.getName}', as it already exists"
      )
    else if (downloadFile.createNewFile()) {
      logger.debug(s"Created new empty file ${downloadFile.getName}")
    } else {
      logger.debug(s"Overwrite existing file ${downloadFile.getName}")
    }

    val url = new URL(
      s"$baseUrl/?Simbench_Code=${simbenchCode.code}"
    )
    url #> downloadFile !!

    downloadPath
  }
}
