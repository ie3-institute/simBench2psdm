package edu.ie3.simbench.io

import java.io.FileOutputStream
import java.nio.file.{Files, Path, Paths}

import com.typesafe.scalalogging.LazyLogging
import edu.ie3.simbench.exception.io.ZipperException
import edu.ie3.util.io.FileIOUtils
import org.apache.commons.compress.archivers.zip.{ZipArchiveEntry, ZipFile}

case object Zipper extends IoUtils with LazyLogging {

  /**
    * Unzips a given zip archive and returns the path to the folder, where the decompressed files are stored
    *
    * @param zipArchive           Path to archive file
    * @param targetFolder         Folder, where to place the extracted files
    * @param failOnExistingFiles  true, if already existing files in the target directory may provoke an Exception or not
    * @param flattenDirectories   true, if the directory tree in the archive should be flattened
    * @return [[Path]] to the folder, where the decompressed files are stored
    */
  @throws(classOf[ZipperException])
  def unzip(
      zipArchive: Path,
      targetFolder: String,
      failOnExistingFiles: Boolean,
      flattenDirectories: Boolean = false
  ): Path = {
    /* Pre-unzip safety checks */
    IoUtils.checkFileExists(zipArchive, ".zip")

    /* Create and check the target folder */
    val archiveName = fileNameRegex("zip")
      .findFirstIn(zipArchive.toAbsolutePath.toString)
      .getOrElse(
        throw ZipperException(
          s"Unable to determine the target filename from the provided file $zipArchive"
        )
      )
    val targetFolderPath = Paths.get(s"$targetFolder$archiveName/")
    prepareFolder(targetFolderPath, failOnExistingFiles)

    val zipFile = new ZipFile(zipArchive.toFile)
    val entries = zipFile.getEntries()
    while (entries.hasMoreElements) {
      handleZipEntry(
        entries.nextElement(),
        zipFile,
        targetFolderPath,
        flattenDirectories
      )
    }
    zipFile.close()

    targetFolderPath
  }

  /**
    * Prepares the folder by creating it, if it not exists. If it is existent and no directory, throw an exception. If
    * it is not empty, either delete the content or throw an exception, depending on what is requested.
    *
    * @param folderPath           Path to the folder, that is meant to be prepared
    * @param failOnNonEmptyFolder Throws an Exception, when true and the folder already has files in it
    */
  private def prepareFolder(
      folderPath: Path,
      failOnNonEmptyFolder: Boolean
  ): Unit = {
    if (!Files.exists(folderPath)) {
      Files.createDirectories(folderPath)
    } else if (!Files.isDirectory(folderPath)) {
      throw ZipperException(
        s"'$folderPath' already exists, but is not a directory"
      )
    } else {
      /* Check if the folder is not empty */
      val folderStream = Files.newDirectoryStream(folderPath.toAbsolutePath)
      val folderEntryIterator = folderStream.iterator()
      if (folderEntryIterator.hasNext) {
        if (failOnNonEmptyFolder)
          throw ZipperException(
            s"Directory '$folderPath' is not empty"
          )
        else {
          logger.warn(
            s"The target directory $folderPath is not empty! Deleting the files."
          )
          folderEntryIterator.forEachRemaining(FileIOUtils.deleteRecursively(_))
        }
      }
    }
  }

  /**
    * Extracting the single entries from zip file. Enclosed directories are flattened, if requested to do so.
    *
    * @param entry              Actual entry to treat
    * @param zipFile            Archive to extract information from
    * @param targetFolder       Target folder path
    * @param flattenDirectories true, when enclosed directories may be flattened
    */
  private def handleZipEntry(
      entry: ZipArchiveEntry,
      zipFile: ZipFile,
      targetFolder: Path,
      flattenDirectories: Boolean
  ): Unit = {
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
              throw ZipperException(
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
}
