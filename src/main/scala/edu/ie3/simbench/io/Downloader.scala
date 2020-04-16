package edu.ie3.simbench.io

import scala.language.postfixOps
import sys.process._
import java.net.URL
import java.io.{File, FileOutputStream}
import java.nio.file.{Files, Path, Paths}

import com.typesafe.scalalogging.LazyLogging
import edu.ie3.simbench.exception.io.DownloaderException
import org.apache.commons.compress.archivers.zip.ZipFile

case class Downloader(downloadFolder: String, baseUrl: String)

case object Downloader extends IoUtils with LazyLogging {

  /**
    * Download the data set for the specified simbenchCode. The code has to be a valid code! It is
    *  not possible to check, if the URL is correct, because the SimBench website does redirect.
    * @param simbenchCode A valid SimBench code
    */
  def download(downloader: Downloader, simbenchCode: String): Path = {
    val downloadFolderPath = new File(s"${downloader.downloadFolder}/")
    val downloadPath =
      Paths.get(s"${downloadFolderPath.getAbsolutePath}/$simbenchCode.zip")
    val downloadFile = downloadPath.toFile
    if (downloadFolderPath.mkdirs()) {
      logger.debug("Created all non existing folders")
    }
    if (downloadFile.createNewFile()) {
      logger.debug(s"Created new empty file ${downloadFile.getName}")
    } else {
      logger.debug(s"Overwrite existing file ${downloadFile.getName}")
    }

    val url = new URL(s"${downloader.baseUrl}/?Simbench_Code=$simbenchCode")
    url #> downloadFile !!

    downloadPath
  }

  /**
    * Unzips a given zip archive and returns the path to the folder, where the decompressed files are stored
    *
    * @param downloader Downloader to use
    * @param zipArchive Path to archive file
    * @param flattenDirectories true, if the directory tree in the archive should be flattened
    * @return [[Path]] to the folder, where the decompressed files are stored
    */
  @throws(classOf[DownloaderException])
  def unzip(
      downloader: Downloader,
      zipArchive: Path,
      flattenDirectories: Boolean = false
  ): Path = {
    /* Pre-unzip safety checks */
    IoUtils.checkFileExists(zipArchive, ".zip")

    /* Create and check the target folder */
    val archiveName = fileNameRegex("zip")
      .findFirstIn(zipArchive.toAbsolutePath.toString)
      .getOrElse(
        throw DownloaderException(
          s"Unable to determine the target filename from the provided file $zipArchive"
        )
      )
    val targetFolder = Paths.get(s"${downloader.downloadFolder}$archiveName/")
    if (!Files.exists(targetFolder)) {
      Files.createDirectories(targetFolder)
    } else if (!Files.isDirectory(targetFolder)) {
      throw DownloaderException(
        s"The target directory to unzip $zipArchive already exists, but is not a " +
          s"directory"
      )
    } else {
      /* Check if the folder is not empty */
      val folderStream =
        Files.newDirectoryStream(targetFolder.toAbsolutePath)
      if (folderStream.iterator().hasNext) {
        logger.warn(s"The target directory $targetFolder is not empty!")
      }
    }

    val zipFile = new ZipFile(zipArchive.toFile)
    val entries = zipFile.getEntries()
    while (entries.hasMoreElements) {
      val entry = entries.nextElement()
      logger.debug(s"Unzipping ${entry.getName} (${entry.getSize} bytes)")
      (entry.isDirectory, flattenDirectories) match {
        case (true, true) =>
          /* Flatten everything, that is in this directory */
          logger.debug(
            s"I will flatten all entries, that are in the directory ${entry.getName}"
          )
        case (true, false) =>
          /* Create the subfolder */
          val subfolder = Paths.get(
            s"${targetFolder.toAbsolutePath.toString}/${entry.getName}"
          )
          Files.createDirectories(subfolder)
        case (false, _) =>
          val entryName = if (flattenDirectories) {
            fileNameRegexWithAnyEnding
              .findFirstIn(entry.getName)
              .getOrElse(
                throw DownloaderException(
                  s"Cannot extract the flattened file name of ${entry.getName}"
                )
              )
          } else {
            entry.getName
          }
          val forseenTagetFilePath =
            Paths.get(s"${targetFolder.toAbsolutePath.toString}/$entryName")
          if (Files.exists(forseenTagetFilePath)) {
            logger.warn(s"Target file $forseenTagetFilePath already exists")
          }
          val targetFilePath = Files.createFile(forseenTagetFilePath)

          val entryInputStream = zipFile.getInputStream(entry)
          val outputStream = new FileOutputStream(targetFilePath.toFile, false)
          val outputBuffer = new Array[Byte](1024)
          LazyList
            .continually(entryInputStream.read(outputBuffer))
            .takeWhile(_ != -1)
            .foreach(outputStream.write(outputBuffer, 0, _))
          entryInputStream.close()
          outputStream.close()
      }
    }
    zipFile.close()

    targetFolder
  }
}
