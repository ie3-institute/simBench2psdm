package edu.ie3.simbench.io

import java.nio.file.Path

import com.typesafe.scalalogging.LazyLogging
import edu.ie3.simbench.exception.io.{IoException, SimbenchDataModelException}
import edu.ie3.simbench.model.RawModelData
import edu.ie3.simbench.model.datamodel.SimbenchModel.SimbenchCompanionObject
import edu.ie3.simbench.model.datamodel.*
import edu.ie3.simbench.model.datamodel.profiles.{
  LoadProfile,
  PowerPlantProfile,
  ResProfile,
  StorageProfile
}
import edu.ie3.simbench.model.datamodel.types.LineType.{ACLineType, DCLineType}
import edu.ie3.simbench.model.datamodel.types.{LineType, Transformer2WType}

import scala.concurrent.duration.Duration
import scala.concurrent.{
  Await,
  ExecutionContext,
  ExecutionContextExecutor,
  Future
}
import scala.reflect.ClassTag
import scala.util.{Failure, Success}

/** Reading all simbench models from a given data set
  *
  * @param simbenchCode
  *   Identifier of the SimBench model to read in
  * @param folderPath
  *   Path to the folder, where the de-compressed files do lay
  * @param separator
  *   Separator used in the files
  * @param fileExtension
  *   Extension of the files
  * @param fileEncoding
  *   Encoding of the files
  */
final case class SimbenchReader(
    simbenchCode: String,
    folderPath: Path,
    separator: String = ";",
    fileExtension: String = "csv",
    fileEncoding: String = "UTF-8"
) extends LazyLogging {

  val checkedFolderPath: String =
    IoUtils.prepareFolderPath(folderPath.toAbsolutePath.toString)

  val checkedFileExtension: String =
    IoUtils.getFileExtensionWithoutDot(fileExtension)

  given ec: ExecutionContextExecutor = ExecutionContext.global

  /* Define the classes to read */
  private val classesToRead = Vector(
    (classOf[LoadProfile], LoadProfile.getFields),
    (classOf[PowerPlantProfile], PowerPlantProfile.getFields),
    (classOf[ResProfile], ResProfile.getFields),
    (classOf[StudyCase], StudyCase.getFields),
    (classOf[Coordinate], Coordinate.getFields),
    (classOf[ExternalNet], ExternalNet.getFields),
    (classOf[ACLineType], ACLineType.getFields),
    (classOf[DCLineType], DCLineType.getFields),
    (classOf[Line[? <: LineType]], Line.getFields),
    (classOf[Load], Load.getFields),
    (classOf[Node], Node.getFields),
    (classOf[NodePFResult], NodePFResult.getFields),
    (classOf[RES], RES.getFields),
    (classOf[Transformer2WType], Transformer2WType.getFields),
    (classOf[Transformer2W], Transformer2W.getFields),
    (classOf[Switch], Switch.getFields)
  )

  /** Read all models and compose them
    *
    * @return
    *   A [[GridModel]] containing all read information
    */
  def readGrid(): GridModel = {
    /* Reading the field to value maps for each of the specified classes
     *
     * Block is discouraged, but the following assembly of classes cannot be parallelized as well, therefore the
     * Await is okay here */
    val modelClassToRawData = getFieldToValueMaps

    if (modelClassToRawData.forall(_._2.isEmpty)) {
      throw SimbenchDataModelException(
        s"Model is empty. Please check the provided data."
      )
    }

    /* Extracting all profiles */
    val loadProfiles = buildModels(modelClassToRawData, LoadProfile)
    val powerPlantProfiles = buildModels(modelClassToRawData, PowerPlantProfile)
    val resProfiles = buildModels(modelClassToRawData, ResProfile)

    val storageProfiles =
      modelClassToRawData.get(classOf[StorageProfile]) match {
        case Some(_) =>
          throw SimbenchDataModelException(
            s"Found data set for ${classOf[StorageProfile]}, but no factory method defined"
          )
        case None => Vector.empty[StorageProfile]
      }

    /* Creating study cases */
    val studyCases = buildModels(modelClassToRawData, StudyCase)

    /* Extracting all types */
    val lineTypes =
      (buildModels(modelClassToRawData, ACLineType, optional = false)
        .map(lineType => lineType.id -> lineType) ++ buildModels(
        modelClassToRawData,
        DCLineType
      ).map(lineType => lineType.id -> lineType)).toMap
    val transformer2WTypes =
      buildModels(modelClassToRawData, Transformer2WType, optional = false)
        .map(transformer2WType => transformer2WType.id -> transformer2WType)
        .toMap

    val coordinates = buildModels(modelClassToRawData, Coordinate)
      .map(coordinate => coordinate.id -> coordinate)
      .toMap
    val substations = buildModels(modelClassToRawData, Substation)
      .map(substation => substation.id -> substation)
      .toMap

    /* Creating the actual models */
    val nodes = modelClassToRawData.getOrElse(classOf[Node], None) match {
      case Some(rawData) =>
        Node
          .buildModels(rawData, coordinates, substations)
          .map(node => node.id -> node)
          .toMap
      case None =>
        throw IoException(
          "Cannot build nodes, as no raw data has been received."
        )
    }
    val lines = modelClassToRawData.getOrElse(classOf[Line[?]], None) match {
      case Some(rawData) => Line.buildModels(rawData, nodes, lineTypes)
      case None =>
        throw IoException(
          "Cannot build lines, as no raw data has been received."
        )
    }
    val transformers2w =
      modelClassToRawData.getOrElse(classOf[Transformer2W], None) match {
        case Some(rawData) =>
          Transformer2W.buildModels(
            rawData,
            nodes,
            transformer2WTypes,
            substations
          )
        case None =>
          throw IoException(
            "Cannot build two-winding transformers, as no raw data has been received."
          )
      }
    val switches = modelClassToRawData.getOrElse(classOf[Switch], None) match {
      case Some(rawData) => Switch.buildModels(rawData, nodes, substations)
      case None =>
        logger.debug(
          s"No information available for ${classOf[Switch].getSimpleName}"
        )
        Vector.empty[Switch]
    }
    val externalNets =
      modelClassToRawData.getOrElse(classOf[ExternalNet], None) match {
        case Some(rawData) => ExternalNet.buildModels(rawData, nodes)
        case None =>
          logger.debug(
            s"No information available for ${classOf[ExternalNet].getSimpleName}"
          )
          Vector.empty[ExternalNet]
      }
    val loads = modelClassToRawData.getOrElse(classOf[Load], None) match {
      case Some(rawData) => Load.buildModels(rawData, nodes)
      case None =>
        throw IoException(
          "Cannot build external nets, as no raw data has been received."
        )
    }
    val res = modelClassToRawData.getOrElse(classOf[RES], None) match {
      case Some(rawData) => RES.buildModels(rawData, nodes)
      case None =>
        logger.debug(
          s"No information available for ${classOf[RES].getSimpleName}"
        )
        Vector.empty[RES]
    }
    val measurements =
      modelClassToRawData.getOrElse(classOf[Measurement], None) match {
        case Some(rawData) =>
          Measurement.buildModels(
            rawData,
            nodes,
            lines.map(line => line.id -> line).toMap,
            transformers2w
              .map(transformer => transformer.id -> transformer)
              .toMap
          )
        case None =>
          logger.debug(
            s"No information available for ${classOf[Measurement].getSimpleName}"
          )
          Vector.empty[Measurement]
      }
    val powerPlants =
      modelClassToRawData.getOrElse(classOf[PowerPlant], None) match {
        case Some(rawData) => PowerPlant.buildModels(rawData, nodes)
        case None =>
          logger.debug(
            s"No information available for ${classOf[PowerPlant].getSimpleName}"
          )
          Vector.empty[PowerPlant]
      }

    /* Currently not known table scheme */
    val shunts = modelClassToRawData.getOrElse(classOf[Shunt], None) match {
      case Some(_) =>
        throw SimbenchDataModelException(
          s"Found data set for ${classOf[Shunt]}, but no factory method defined"
        )
      case None => Vector.empty[Shunt]
    }
    val storages = modelClassToRawData.getOrElse(classOf[Storage], None) match {
      case Some(_) =>
        throw SimbenchDataModelException(
          s"Found data set for ${classOf[Storage]}, but no factory method defined"
        )
      case None => Vector.empty[Storage]
    }
    val transformers3w =
      modelClassToRawData.getOrElse(classOf[Transformer3W], None) match {
        case Some(_) =>
          throw SimbenchDataModelException(
            s"Found data set for ${classOf[Transformer3W]}, but no factory method defined"
          )
        case None => Vector.empty[Transformer3W]
      }

    /* Create the nodal power flow results */
    val powerFlowResults =
      modelClassToRawData.getOrElse(classOf[NodePFResult], None) match {
        case Some(rawData) =>
          NodePFResult.buildModels(rawData, nodes, substations)
        case None =>
          logger.debug(
            s"No information available for ${classOf[NodePFResult].getSimpleName}"
          )
          Vector.empty[NodePFResult]
      }

    /* Create grid model */
    GridModel(
      simbenchCode,
      externalNets,
      lines,
      loads,
      loadProfiles,
      measurements,
      nodes.values.toVector,
      powerFlowResults,
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

  /** Reading all models of the specified class and returning them as an
    * [[Option]]. Some classes are not mandatory, therefore, an [[Option]] on
    * the result is handed back.
    *
    * @param modelClass
    *   Class of the models to read
    * @param desiredFields
    *   The desired fields to get from file
    * @return
    *   A tuple of modelClass to an Option on a [[Vector]] of raw model data for
    *   that class
    */
  private def read[T <: SimbenchModel](
      modelClass: Class[T],
      desiredFields: Array[HeadLineField]
  ): Future[(Class[T], Option[Vector[RawModelData]])] =
    Future {
      /* Determine the matching file name */
      SimbenchFileNamingStrategy.getFileName(modelClass) match {
        case Failure(exception) =>
          throw IoException(
            s"Cannot read the data set, as the file name for $modelClass can not be determined",
            exception
          )
        case Success(filename) =>
          modelClass -> CsvReader(
            modelClass,
            IoUtils.composeFullyQualifiedPath(
              checkedFolderPath,
              filename,
              checkedFileExtension
            ),
            separator,
            fileExtension,
            fileEncoding
          ).map(_.read(desiredFields))
      }
    }

  /** Get the field to value maps for all of the specified classes to read.
    *
    * @return
    *   A map of model class to a vector of maps from field to value
    */
  private def getFieldToValueMaps
      : Map[Class[?], Option[Vector[RawModelData]]] = {
    Await
      .result(
        Future.sequence(
          for ((clazz, fields) <- classesToRead)
            yield {
              read(clazz, fields)
            }
        ),
        Duration("30 s")
      )
      .toMap
  }

  /** Building models from implicitly given class tag. If the models may or not
    * be apparent, an empty Vector is returned. If they are mandatory, a
    * [[IoException]] ist thrown.
    *
    * @param modelClassToRawData
    *   Mapping from class to specific raw data
    * @param cls
    *   Companion object to use for model generation
    * @param tag
    *   Given class tag of the model class to build
    * @param optional
    *   true, if the models may or not be apparent (Default: true)
    * @tparam C
    *   Type of the model class
    * @return
    *   A [[Vector]] of models
    */
  private def buildModels[C <: SimbenchModel](
      modelClassToRawData: Map[Class[?], Option[Vector[RawModelData]]],
      cls: SimbenchCompanionObject[C],
      optional: Boolean = true
  )(using tag: ClassTag[C]): Vector[C] = {
    modelClassToRawData.getOrElse(tag.runtimeClass, None) match {
      case Some(rawData) => cls.buildModels(rawData)
      case None =>
        if optional then {
          logger.debug(
            s"No information available for ${tag.runtimeClass.getSimpleName}"
          )
          Vector.empty
        } else {
          throw IoException(
            s"Cannot build models of ${tag.runtimeClass.getSimpleName}, as no raw data has been received."
          )
        }
    }
  }
}
