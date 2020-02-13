package edu.ie3.simbench.io

import java.nio.file.Path

import edu.ie3.simbench.exception.io.IoException
import edu.ie3.simbench.model.datamodel.{
  Coordinate,
  GridModel,
  Node,
  SimbenchModel,
  Substation
}

import scala.util.{Failure, Success}

/**
  * Reading all simbench models from a given data set
  *
  * @param folderPath   Path to the folder, where the de-compressed files do lay
  * @param separator    Separator used in the files
  * @param fileEnding   Ending of the files
  * @param fileEncoding Encoding of the files
  */
final case class SimbenchReader(folderPath: Path,
                                separator: String,
                                fileEnding: String,
                                fileEncoding: String) {

  /**
    * Read all models and compose them
    *
    * @return //TODO: Specify
    */
  def read(): GridModel = {
    /* Create empty grid model */
    val gridModel = GridModel.apply()

    val coordinates = generateCoordinates(gridModel)

    val gridModelWithSubstations = generateSubstations(gridModel)
    val gridModelWithNodes = generateNodes(gridModel, coordinates)

    gridModel
  }

  /**
    * Reading all models of the specified class and returning them
    *
    * @param modelClass     Class of the models to read
    * @param desiredFields  The desired fields to get from file
    * @return               A Vector of maps with fields to values
    */
  def read(modelClass: Class[_ <: SimbenchModel],
           desiredFields: Array[String]): Vector[Map[String, String]] = {
    /* Determine the matching file name */
    SimbenchFileNamingStrategy.getFileName(modelClass) match {
      case Failure(exception) =>
        throw IoException(
          s"Cannot read the data set, as the file name for $modelClass can not cannot be determined.",
          exception)
      case Success(filename) =>
        val csvReader =
          CsvReader(
            folderPath.toAbsolutePath.toString + "/" + filename + fileEnding,
            separator,
            fileEnding,
            fileEncoding)

        /* De-serialise the files */
        csvReader.read(desiredFields)
    }
  }

  /**
    * Read information from file and generate coordinates
    *
    * @param gridModel Container with models based on the current state
    * @return A vector of [[Coordinate]]s
    */
  private def generateCoordinates(gridModel: GridModel): Vector[Coordinate] = {
    val coordinateValues = read(classOf[Coordinate], Coordinate.getFields)
    coordinateValues.map(valueMap => Coordinate(valueMap))
  }

  /**
    * Generate the substation models and add them to the grid model
    *
    * @param gridModel    Container with models based on the current state
    * @return             An updated grid model with added substations
    */
  private def generateSubstations(gridModel: GridModel): GridModel = {
    val substationValues = read(classOf[Substation], Substation.getFields)
    val substations =
      substationValues.map(valueMap => Substation(valueMap, gridModel))
    gridModel.copy(substations = substations)
  }

  /**
    * Generate the nodes and add them to the grid model
    *
    * @param gridModel  Container with models based on the current state
    * @return           An updated grid model with added substations
    */
  private def generateNodes(gridModel: GridModel,
                            coordinates: Vector[Coordinate]): GridModel = {
    val nodeValues = read(classOf[Node], Node.getFields)
    val nodes =
      nodeValues.map(valueMap => Node(valueMap, gridModel, coordinates))
    gridModel.copy(nodes = nodes)
  }
}
