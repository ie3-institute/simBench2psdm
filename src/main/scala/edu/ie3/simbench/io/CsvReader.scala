package edu.ie3.simbench.io

import edu.ie3.simbench.exception.io.IoException
import edu.ie3.simbench.model.RawModelData

import scala.io.BufferedSource

/**
  * Generic csv reader to de-serialise a csv file to a vector of maps from desired field names to their content
  *
  * @param modelClass   Model class to read from this file
  * @param filePath     Path to the csv file to read
  * @param separator    Separator to use for splitting fields
  * @param fileEnding   Desired file ending
  * @param fileEncoding Desired file encoding
  */
final case class CsvReader[T](modelClass: Class[T],
                              filePath: String,
                              separator: String,
                              fileEnding: String = ".csv",
                              fileEncoding: String = "UTF-8") {
  /* Check, if the object exists, is a file and has the correct file ending */
  IoUtils.checkFileExists(filePath, fileEnding)

  /**
    * Read the content of the file and generate a vector of it
    *
    * @param desiredFields  Array with strings of desired field names
    * @return               A vector with raw model data
    */
  def read(desiredFields: Array[String]): Vector[RawModelData] = {
    val bufferedSource: BufferedSource =
      io.Source.fromFile(filePath, fileEncoding)
    try {
      /* Build the file mapping */
      val headLine = bufferedSource
        .getLines()
        .find(_ => true)
        .getOrElse(throw IoException("The file does not contain any line."))
      val fieldMapping = mapFields(headLine, desiredFields)

      /* The buffered source continues with the second line, as the headline has already been read */
      (for (line <- bufferedSource.getLines()) yield {
        RawModelData(modelClass, readLine(line, fieldMapping))
      }).toVector
    } finally {
      bufferedSource.close()
    }
  }

  /**
    * Build the column mapping from the desired fields to their column position in the file. If one desired field is not
    * apparent, an exception is thrown. If more fields are apparent in the file, the rest is simply discarded.
    *
    * @param headLine       Head line of the file
    * @param desiredFields  Desired fields to be contained in the file
    * @return               A map from desired field to column position in the file
    */
  private def mapFields(headLine: String,
                        desiredFields: Array[String]): Map[String, Int] = {
    /* Split for the single apparent fields in the file and map it to their indices */
    val headLineFields = headLine.split(separator).map(_.trim).zipWithIndex

    /* Map all single desired fields to their column position in the file. If one desired field is not apparent, throw
     * an exception. */
    desiredFields
      .map(
        desiredField =>
          (desiredField,
           headLineFields
             .find(apparentField => apparentField._1 == desiredField)
             .getOrElse(throw IoException(
               s"The headline of the file $filePath does not contain the desired field $desiredField"))
             ._2))
      .toMap
  }

  /**
    * Reads the given line and splits it up according to the defined fieldMapping.
    *
    * @param line         Line in the file to read
    * @param fieldMapping Mapping from desired field to column position
    * @return             A map from desired field to content of the field
    */
  private def readLine(line: String,
                       fieldMapping: Map[String, Int]): Map[String, String] = {
    val fields = line.split(separator).map(_.trim)
    if (fields.length < fieldMapping.values.max + 1)
      throw IoException(
        s"The line of the file $filePath does not contain the correct amount of fields (apparent = ${fields.length}, " +
          s"needed = ${fieldMapping.values.max + 1}).\nLine affected: $line")
    fieldMapping.map(mappingEntry => (mappingEntry._1, fields(mappingEntry._2)))
  }
}
