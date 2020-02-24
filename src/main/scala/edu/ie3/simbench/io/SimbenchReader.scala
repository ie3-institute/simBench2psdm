package edu.ie3.simbench.io

import java.nio.file.Path

import com.typesafe.scalalogging.LazyLogging
import edu.ie3.simbench.exception.io.{IoException, SimbenchDataModelException}
import edu.ie3.simbench.model.RawModelData
import edu.ie3.simbench.model.datamodel.profiles.{
  LoadProfile,
  PowerPlantProfile,
  ResProfile,
  StorageProfile
}
import edu.ie3.simbench.model.datamodel.types.{LineType, Transformer2WType}
import edu.ie3.simbench.model.datamodel.{
  Coordinate,
  ExternalNet,
  GridModel,
  Line,
  Load,
  Measurement,
  Node,
  PowerPlant,
  RES,
  Shunt,
  SimbenchModel,
  Storage,
  StudyCase,
  Substation,
  Switch,
  Transformer2W,
  Transformer3W
}

import scala.concurrent.duration.Duration
import scala.concurrent.{
  Await,
  ExecutionContext,
  ExecutionContextExecutor,
  Future
}
import scala.util.{Failure, Success}

/**
  * Reading all simbench models from a given data set
  *
  * @param folderPath     Path to the folder, where the de-compressed files do lay
  * @param separator      Separator used in the files
  * @param fileExtension  Extension of the files
  * @param fileEncoding   Encoding of the files
  */
final case class SimbenchReader(folderPath: Path,
                                separator: String = ";",
                                fileExtension: String = "csv",
                                fileEncoding: String = "UTF-8")
    extends LazyLogging {

  val checkedFolderPath: String =
    IoUtils.prepareFolderPath(folderPath.toAbsolutePath.toString)

  val checkedFileExtension: String =
    IoUtils.getFileExtensionWithoutDot(fileExtension)

  implicit val ec: ExecutionContextExecutor = ExecutionContext.global

  /* Define the classes to read */
  private val classesToRead = Vector(
    (classOf[LoadProfile], LoadProfile.getFields),
    (classOf[PowerPlantProfile], PowerPlantProfile.getFields),
    (classOf[ResProfile], ResProfile.getFields),
    (classOf[StudyCase], StudyCase.getFields),
    (classOf[Coordinate], Coordinate.getFields),
    (classOf[ExternalNet], ExternalNet.getFields),
    (classOf[LineType], LineType.getFields),
    (classOf[Line[_ <: LineType]], Line.getFields),
    (classOf[Load], Load.getFields),
    (classOf[Node], Node.getFields),
    (classOf[RES], RES.getFields),
    (classOf[Transformer2WType], Transformer2WType.getFields),
    (classOf[Transformer2W], Transformer2W.getFields)
  )

  /**
    * Read all models and compose them
    *
    * @return A [[GridModel]] containing all read information
    */
  def readGrid(): GridModel = {
    /* Reading the field to value maps for each of the specified classes
     *
     * Block is discouraged, but the following assembly of classes cannot be parallelized as well, therefore the
     * Await is okay here */
    val modelClassToRawData = getFieldToValueMaps

    /* Extracting all profiles */
    val loadProfiles = LoadProfile.buildModels(
      modelClassToRawData.getOrElse(
        classOf[LoadProfile],
        throw IoException(
          "Cannot build load profiles, as no raw data has been received.")))

    val powerPlantProfiles =
      modelClassToRawData.get(classOf[PowerPlantProfile]) match {
        case Some(rawDatas) => PowerPlantProfile.buildModels(rawDatas)
        case None =>
          logger.debug(
            s"No information available for ${classOf[PowerPlantProfile].getSimpleName}")
          Vector.empty
      }

    val resProfiles =
      modelClassToRawData.get(classOf[ResProfile]) match {
        case Some(rawDatas) => ResProfile.buildModels(rawDatas)
        case None =>
          logger.debug(
            s"No information available for ${classOf[ResProfile].getSimpleName}")
          Vector.empty
      }

    val storageProfiles =
      modelClassToRawData.get(classOf[StorageProfile]) match {
        case Some(_) =>
          throw SimbenchDataModelException(
            s"Found data set for ${classOf[StorageProfile]}, but no factory method defined")
        case None => Vector.empty[StorageProfile]
      }

    /* Creating study cases */
    val studyCases = modelClassToRawData.get(classOf[StudyCase]) match {
      case Some(rawDatas) => StudyCase.buildModels(rawDatas)
      case None =>
        logger.debug(
          s"No information available for ${classOf[StudyCase].getSimpleName}")
        Vector.empty
    }

    /* Extracting all types */
    val lineTypes = modelClassToRawData.get(classOf[LineType]) match {
      case Some(rawDatas) => getLineTypes(rawDatas)
      case None =>
        throw IoException(
          "Cannot build line types, as no raw data has been received.")
    }
    val transformer2WTypes =
      modelClassToRawData.get(classOf[Transformer2WType]) match {
        case Some(rawDatas) => getTransformer2WTypes(rawDatas)
        case None =>
          throw IoException(
            "Cannot build transformer types, as no raw data has been received.")
      }
    val coordinates = modelClassToRawData.get(classOf[Coordinate]) match {
      case Some(rawDatas) => getCoordinates(rawDatas)
      case None =>
        logger.debug(
          s"No information available for ${classOf[Coordinate].getSimpleName}")
        Map.empty[String, Coordinate]
    }
    val substations = modelClassToRawData.get(classOf[Substation]) match {
      case Some(rawDatas) => getSubstations(rawDatas)
      case None =>
        logger.debug(
          s"No information available for ${classOf[Substation].getSimpleName}")
        Map.empty[String, Substation]
    }

    /* Creating the actual models */
    val nodes = modelClassToRawData.get(classOf[Node]) match {
      case Some(rawDatas) =>
        Node
          .buildModels(rawDatas, coordinates, substations)
          .map(node => node.id -> node)
          .toMap
      case None =>
        throw IoException(
          "Cannot build nodes, as no raw data has been received.")
    }
    val lines = modelClassToRawData.get(classOf[Line[_]]) match {
      case Some(rawDatas) => Line.buildModels(rawDatas, nodes, lineTypes)
      case None =>
        throw IoException(
          "Cannot build lines, as no raw data has been received.")
    }
    val transformers2w = modelClassToRawData.get(classOf[Transformer2W]) match {
      case Some(rawDatas) =>
        Transformer2W.buildModels(rawDatas,
                                  nodes,
                                  transformer2WTypes,
                                  substations)
      case None =>
        throw IoException(
          "Cannot build two-winding transformers, as no raw data has been received.")
    }
    val switches = modelClassToRawData.get(classOf[Switch]) match {
      case Some(rawDatas) => Switch.buildModels(rawDatas, nodes, substations)
      case None =>
        logger.debug(
          s"No information available for ${classOf[Switch].getSimpleName}")
        Vector.empty[Switch]
    }
    val externalNets = modelClassToRawData.get(classOf[ExternalNet]) match {
      case Some(rawDatas) => ExternalNet.buildModels(rawDatas, nodes)
      case None =>
        throw IoException(
          "Cannot build external nets, as no raw data has been received.")
    }
    val loads = modelClassToRawData.get(classOf[Load]) match {
      case Some(rawDatas) => Load.buildModels(rawDatas, nodes)
      case None =>
        throw IoException(
          "Cannot build external nets, as no raw data has been received.")
    }
    val res = modelClassToRawData.get(classOf[RES]) match {
      case Some(rawDatas) => RES.buildModels(rawDatas, nodes)
      case None =>
        logger.debug(
          s"No information available for ${classOf[RES].getSimpleName}")
        Vector.empty[RES]
    }
    val measurements = modelClassToRawData.get(classOf[Measurement]) match {
      case Some(rawDatas) =>
        Measurement.buildModels(
          rawDatas,
          nodes,
          lines.map(line => line.id -> line).toMap,
          transformers2w
            .map(transformer => transformer.id -> transformer)
            .toMap)
      case None =>
        logger.debug(
          s"No information available for ${classOf[Measurement].getSimpleName}")
        Vector.empty[Measurement]
    }
    val powerPlants = modelClassToRawData.get(classOf[PowerPlant]) match {
      case Some(rawDatas) => PowerPlant.buildModels(rawDatas, nodes)
      case None =>
        logger.debug(
          s"No information available for ${classOf[PowerPlant].getSimpleName}")
        Vector.empty[PowerPlant]
    }

    /* Currently not known table scheme */
    val shunts = modelClassToRawData.get(classOf[Shunt]) match {
      case Some(_) =>
        throw SimbenchDataModelException(
          s"Found data set for ${classOf[Shunt]}, but no factory method defined")
      case None => Vector.empty[Shunt]
    }
    val storages = modelClassToRawData.get(classOf[Storage]) match {
      case Some(_) =>
        throw SimbenchDataModelException(
          s"Found data set for ${classOf[Storage]}, but no factory method defined")
      case None => Vector.empty[Storage]
    }
    val transformers3w = modelClassToRawData.get(classOf[Transformer3W]) match {
      case Some(_) =>
        throw SimbenchDataModelException(
          s"Found data set for ${classOf[Transformer3W]}, but no factory method defined")
      case None => Vector.empty[Transformer3W]
    }

    /* Create grid model */
    GridModel(
      externalNets,
      lines,
      loads,
      loadProfiles,
      measurements,
      nodes.values.toVector,
      powerPlants,
      powerPlantProfiles,
      res,
      resProfiles,
      shunts,
      storages,
      storageProfiles,
      studyCases,
      substations.values.toVector,
      switches,
      transformers2w,
      transformers3w
    )
  }

  /**
    * Reading all models of the specified class and returning them
    *
    * @param modelClass     Class of the models to read
    * @param desiredFields  The desired fields to get from file
    * @return               A Vector of maps with fields to values
    */
  private def read[T <: SimbenchModel](modelClass: Class[T],
                                       desiredFields: Array[HeadLineField])
    : Future[(Class[T], Vector[RawModelData])] =
    Future {
      /* Determine the matching file name */
      SimbenchFileNamingStrategy.getFileName(modelClass) match {
        case Failure(exception) =>
          throw IoException(
            s"Cannot read the data set, as the file name for $modelClass can not be determined",
            exception)
        case Success(filename) =>
          val csvReader =
            CsvReader(modelClass,
                      IoUtils.composeFullyQualifiedPath(checkedFolderPath,
                                                        filename,
                                                        checkedFileExtension),
                      separator,
                      fileExtension,
                      fileEncoding)

          /* De-serialise the files */
          modelClass -> csvReader.read(desiredFields)
      }
    }

  /**
    * Get the field to value maps for all of the specified classes to read.
    *
    * @return A map of model class to a vector of maps from field to value
    */
  private def getFieldToValueMaps: Map[Class[_], Vector[RawModelData]] = {
    Await
      .result(Future.sequence(
                for ((clazz, fields) <- classesToRead)
                  yield {

                    read(clazz, fields)
                  }),
              Duration("10 s"))
      .toMap
  }

  /**
    * Generate a mapping from line type id to the line type itself
    *
    * @param rawData Vector of field to value maps
    * @return A mapping from line type id to line type itself
    */
  private def getLineTypes(
      rawData: Vector[RawModelData]): Map[String, LineType] =
    LineType
      .buildModels(rawData)
      .map(lineType => lineType.id -> lineType)
      .toMap

  /**
    * Generate a mapping from transformer type id to the transformer type itself
    *
    * @param rawData Vector of field to value maps
    * @return A mapping from transformer type id to transformer type itself
    */
  private def getTransformer2WTypes(
      rawData: Vector[RawModelData]): Map[String, Transformer2WType] =
    Transformer2WType
      .buildModels(rawData)
      .map(transformerType => transformerType.id -> transformerType)
      .toMap

  /**
    * Generate a mapping from coordinate id to the coordinate itself
    *
    * @param rawData Vector of field to value maps
    * @return A mapping from coordinate id to coordinate itself
    */
  private def getCoordinates(
      rawData: Vector[RawModelData]): Map[String, Coordinate] =
    Coordinate
      .buildModels(rawData)
      .map(coordinate => coordinate.id -> coordinate)
      .toMap

  /**
    * Generate a mapping from substation id to the substation
    *
    * @param rawData Vector of field to value maps
    * @return A mapping from substation id to substation itself
    */
  private def getSubstations(
      rawData: Vector[RawModelData]): Map[String, Substation] =
    Substation
      .buildModels(rawData)
      .map(coordinate => coordinate.id -> coordinate)
      .toMap
}
