package edu.ie3.simbench.actor

import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import edu.ie3.datamodel.models.input.{MeasurementUnitInput, NodeInput}
import edu.ie3.datamodel.models.input.connector.{
  LineInput,
  SwitchInput,
  Transformer2WInput,
  Transformer3WInput
}
import edu.ie3.datamodel.models.input.system.{FixedFeedInInput, LoadInput}
import edu.ie3.datamodel.models.result.NodeResult
import edu.ie3.simbench.exception.CodeValidationException
import edu.ie3.simbench.io.{Downloader, SimbenchReader, Zipper}
import edu.ie3.simbench.model.SimbenchCode
import edu.ie3.simbench.model.datamodel.{GridModel, Node}
import org.slf4j.Logger

import java.nio.file.Path
import java.util.UUID

object Converter {
  def apply(): Behaviors.Receive[ConverterMessage] = uninitialized

  /**
    * Receive information to set up myself accordingly
    *
    * @return Behavior to do so
    */
  def uninitialized: Behaviors.Receive[ConverterMessage] = Behaviors.receive {
    case (
        ctx,
        Init(
          simBenchCode,
          baseUrl,
          downloadTargetDirectory,
          failDownloadOnExistingFiles,
          inputFileEnding,
          inputFileEncoding,
          inputFileColumnSeparator,
          removeSwitches,
          amountOfWorkers,
          useDirectoryHierarchy,
          targetDirectory,
          csvColumnSeparator,
          compress,
          converter
        )
        ) =>
      ctx.log.info(s"$simBenchCode - Converter started.")

      /* Initialize a mutator for this conversion and wait for it's reply */
      spawnMutator(
        simBenchCode,
        useDirectoryHierarchy,
        targetDirectory,
        csvColumnSeparator,
        compress,
        ctx
      )
      initializing(
        simBenchCode,
        baseUrl,
        downloadTargetDirectory,
        failDownloadOnExistingFiles,
        inputFileEnding,
        inputFileEncoding,
        inputFileColumnSeparator,
        removeSwitches,
        amountOfWorkers,
        converter
      )
  }

  /**
    * Receive needed information while being in the process of initialization
    *
    * @param simBenchCode                 SimBench code to handle later
    * @param downloadTargetDirectory      Target directory for downloads
    * @param baseUrl                      Base url of the SimBench website
    * @param failDownloadOnExistingFiles  Whether or not to fail, if target files already exist
    * @param inputFileEnding              Ending of input files
    * @param inputFileEncoding            Encoding of the input files
    * @param inputFileColumnSeparator     Column separator of the input data
    * @param removeSwitches               Whether or not to remove switches in final model
    * @param amountOfWorkers              Amount of workers to convert participants
    * @param coordinator                  Reference to the coordinator
    * @return Behavior to do so
    */
  def initializing(
      simBenchCode: String,
      baseUrl: String,
      downloadTargetDirectory: String,
      failDownloadOnExistingFiles: Boolean,
      inputFileEnding: String,
      inputFileEncoding: String,
      inputFileColumnSeparator: String,
      removeSwitches: Boolean,
      amountOfWorkers: Int,
      coordinator: ActorRef[Coordinator.CoordinatorMessage]
  ): Behaviors.Receive[ConverterMessage] = Behaviors.receive {
    case (ctx, MutatorInitialized(mutator)) =>
      ctx.log.debug(s"$simBenchCode - Mutator is ready.")
      coordinator ! Coordinator.ConverterInitialized(simBenchCode, ctx.self)
      idle(
        StateData(
          simBenchCode,
          downloadTargetDirectory,
          baseUrl,
          failDownloadOnExistingFiles,
          inputFileEnding,
          inputFileEncoding,
          inputFileColumnSeparator,
          removeSwitches,
          amountOfWorkers,
          mutator,
          coordinator
        )
      )
  }

  def idle(
      stateData: StateData
  ): Behaviors.Receive[ConverterMessage] = Behaviors.receive {
    case (ctx, Convert(simBenchCode)) =>
      ctx.log.info(
        s"$simBenchCode - Downloading data set from SimBench website"
      )
      val downloadDirectory = downloadModel(
        simBenchCode,
        stateData.downloadDirectory,
        stateData.baseUrl,
        stateData.failOnExistingFiles
      )
      ctx.log.debug(s"$simBenchCode - Reading in the SimBench data set")
      val simBenchModel = readModel(
        simBenchCode,
        downloadDirectory,
        stateData.inputFileColumnSeparator,
        stateData.inputFileEnding,
        stateData.inputFileEncoding
      )

      /* Spawning a grid converter and ask it to do some first conversions */
      ctx.log.info(s"$simBenchCode - Start conversion of grid structure")
      val gridConverter =
        ctx.spawn(GridConverter(), s"gridConverter_${stateData.simBenchCode}")
      gridConverter ! GridConverter.ConvertGridStructure(
        simBenchCode,
        simBenchModel.nodes,
        simBenchModel.nodePFResults,
        simBenchModel.externalNets,
        simBenchModel.powerPlants,
        simBenchModel.res,
        simBenchModel.transformers2w,
        simBenchModel.transformers3w,
        simBenchModel.lines,
        simBenchModel.switches,
        simBenchModel.measurements,
        stateData.removeSwitches,
        ctx.self
      )
      converting(stateData, simBenchModel, gridConverter)
  }

  def converting(
      stateData: StateData,
      simBenchModel: GridModel,
      gridConverter: ActorRef[GridConverter.GridConverterMessage],
      awaitedResults: AwaitedResults = AwaitedResults.empty
  ): Behaviors.Receive[ConverterMessage] = Behaviors.receive {
    case (
        ctx,
        GridStructureConverted(
          nodeConversion,
          nodeResults,
          lines,
          transformers2w,
          transformers3w,
          switches,
          measurements
        )
        ) =>
      ctx.log.debug(
        s"${stateData.simBenchCode} - Grid structure has been converted."
      )

      ctx.log.info(
        s"${stateData.simBenchCode} - Starting conversion of participant models"
      )
      val loadConverter =
        ctx.spawn(LoadConverter(), s"loadConverter_${stateData.simBenchCode}")
      loadConverter ! LoadConverter.Init(
        stateData.simBenchCode,
        stateData.amountOfWorkers,
        simBenchModel.loadProfiles,
        stateData.mutator,
        ctx.self
      )
      val resConverter =
        ctx.spawn(ResConverter(), s"resConverter_${stateData.simBenchCode}")
      resConverter ! ResConverter.Init(
        stateData.simBenchCode,
        stateData.amountOfWorkers,
        simBenchModel.resProfiles,
        stateData.mutator,
        ctx.self
      )

      // TODO: Issue conversion of participants that then also respect for islanded nodes
      converting(
        stateData,
        simBenchModel,
        gridConverter,
        awaitedResults.copy(
          nodeConversion = Some(nodeConversion),
          nodeResults = Some(nodeResults),
          lines = Some(lines),
          transformers2w = Some(transformers2w),
          transformers3w = Some(transformers3w),
          switches = Some(switches),
          measurements = Some(measurements)
        )
      )

    case (ctx, LoadConverterReady(loadConverter)) =>
      ctx.log.debug(
        s"${stateData.simBenchCode} - LoadConverter is ready. Request conversion."
      )
      loadConverter ! LoadConverter.Convert(
        stateData.simBenchCode,
        simBenchModel.loads,
        awaitedResults.nodeConversion.getOrElse(Map.empty),
        ctx.self
      )
      Behaviors.same

    case (ctx, ResConverterReady(resConverter)) =>
      ctx.log.debug(
        s"${stateData.simBenchCode} - ResConverter is ready. Request conversion."
      )
      resConverter ! ResConverter.Convert(
        stateData.simBenchCode,
        simBenchModel.res,
        awaitedResults.nodeConversion.getOrElse(Map.empty),
        ctx.self
      )
      Behaviors.same

    case (ctx, LoadsConverted(converted)) =>
      ctx.log.info(
        s"${stateData.simBenchCode} - All loads are converted."
      )
      val updatedAwaitedResults = awaitedResults.copy(loads = Some(converted))

      if (updatedAwaitedResults.isReady)
        finalizeConversion(
          stateData.simBenchCode,
          updatedAwaitedResults,
          stateData.mutator,
          stateData.coordinator,
          ctx.self,
          ctx.log
        )
      else
        converting(
          stateData,
          simBenchModel,
          gridConverter,
          updatedAwaitedResults
        )

    case (ctx, ResConverted(converted)) =>
      ctx.log.info(
        s"${stateData.simBenchCode} - All RES are converted."
      )
      val updatedAwaitedResults = awaitedResults.copy(res = Some(converted))

      if (updatedAwaitedResults.isReady)
        finalizeConversion(
          stateData.simBenchCode,
          updatedAwaitedResults,
          stateData.mutator,
          stateData.coordinator,
          ctx.self,
          ctx.log
        )
      else
        converting(
          stateData,
          simBenchModel,
          gridConverter,
          updatedAwaitedResults
        )
  }

  def finalizing(
      simBenchCode: String,
      mutator: ActorRef[Mutator.MutatorMessage],
      coordinator: ActorRef[Coordinator.CoordinatorMessage],
      gridStructurePersisted: Boolean = false,
      nodeResultsPersisted: Boolean = false,
      timeSeriesMappingPersisted: Boolean = false
  ): Behaviors.Receive[ConverterMessage] = Behaviors.receive {
    case (ctx, MutatorTerminated) =>
      ctx.log.debug(s"$simBenchCode - Mutator has terminated.")
      ctx.log.info(
        s"$simBenchCode - Shut down converter."
      )
      coordinator ! Coordinator.Converted(simBenchCode)
      Behaviors.stopped

    case (ctx, message) =>
      val (
        updatedGridStructurePersisted,
        updatedNodeResultsPersisted,
        updatedTimeSeriesMappingPersisted
      ) = message match {
        case GridStructurePersisted =>
          ctx.log.debug(s"$simBenchCode - Grid structure is persisted")
          (
            true,
            nodeResultsPersisted,
            timeSeriesMappingPersisted
          )
        case NodalResultsPersisted =>
          ctx.log.debug(s"$simBenchCode - Node results are persisted")
          (
            gridStructurePersisted,
            true,
            timeSeriesMappingPersisted
          )
        case TimeSeriesMappingPersisted =>
          ctx.log.debug(s"$simBenchCode - Time series mapping is persisted")
          (
            gridStructurePersisted,
            nodeResultsPersisted,
            true
          )
        case unexpected =>
          ctx.log.warn(
            s"$simBenchCode - Received unexpected message '$unexpected'."
          )
          (
            gridStructurePersisted,
            nodeResultsPersisted,
            timeSeriesMappingPersisted
          )
      }
      if (updatedGridStructurePersisted && updatedNodeResultsPersisted && updatedTimeSeriesMappingPersisted) {
        ctx.log.debug(
          s"$simBenchCode - All models are persisted. Shut down mutator."
        )
        mutator ! Mutator.Terminate
      }
      finalizing(
        simBenchCode,
        mutator,
        coordinator,
        updatedGridStructurePersisted,
        updatedNodeResultsPersisted,
        updatedTimeSeriesMappingPersisted
      )
  }

  private def spawnMutator(
      simBenchCode: String,
      useDirectoryHierarchy: Boolean,
      targetDirectory: String,
      csvColumnSeparator: String,
      compress: Boolean,
      ctx: ActorContext[ConverterMessage]
  ): Unit = {
    val mutator = ctx.spawn(Mutator(), s"mutator_$simBenchCode")
    mutator ! Mutator.Init(
      simBenchCode,
      useDirectoryHierarchy,
      targetDirectory,
      csvColumnSeparator,
      compress,
      ctx.self
    )
    ctx.watchWith(mutator, MutatorTerminated)
  }

  /**
    * Downloads the specified model and hands back the path, where the extracted files are located
    *
    * @param simBenchCode         Code, that denotes the target SimBench model
    * @param downloadDirectory    Target directory for downloads
    * @param baseUrl              Base url of the SimBench website
    * @param failOnExistingFiles  Whether or not to fail, if target files already exist
    * @return The path to the extracted data
    */
  private def downloadModel(
      simBenchCode: String,
      downloadDirectory: String,
      baseUrl: String,
      failOnExistingFiles: Boolean
  ): Path = {
    val downloader =
      Downloader(
        downloadDirectory,
        baseUrl,
        failOnExistingFiles
      )
    val downloadedFile =
      downloader.download(
        SimbenchCode(simBenchCode).getOrElse(
          throw CodeValidationException(
            s"'$simBenchCode' is no valid SimBench code."
          )
        )
      )
    Zipper.unzip(
      downloadedFile,
      downloader.downloadFolder,
      failOnExistingFiles,
      flattenDirectories = true
    )
  }

  /**
    * Read in the raw SimBench grid model
    *
    * @param simBenchCode       Code of model to convert
    * @param downloadDirectory  Directory, where the files are downloaded to
    * @param csvColumnSeparator Column separator of input files
    * @param fileEnding         Ending of input files
    * @param fileEncoding       Encoding of the file
    * @return The raw SimBench model
    */
  private def readModel(
      simBenchCode: String,
      downloadDirectory: Path,
      csvColumnSeparator: String,
      fileEnding: String,
      fileEncoding: String
  ): GridModel = {
    val simBenchReader = SimbenchReader(
      simBenchCode,
      downloadDirectory,
      csvColumnSeparator,
      fileEnding,
      fileEncoding
    )
    simBenchReader.readGrid()
  }

  private def finalizeConversion(
      simBenchCode: String,
      awaitedResults: AwaitedResults,
      mutator: ActorRef[Mutator.MutatorMessage],
      coordinator: ActorRef[Coordinator.CoordinatorMessage],
      self: ActorRef[ConverterMessage],
      logger: Logger
  ): Behaviors.Receive[ConverterMessage] = {
    logger.info(
      s"$simBenchCode - Persisting grid structure and associated particicpants."
    )
    /* Bring together all results and send them to the mutator */
    mutator ! Mutator.PersistGridStructure(
      simBenchCode,
      awaitedResults.nodeConversion.map(_.values.toSet).getOrElse(Set.empty),
      awaitedResults.lines.map(_.toSet).getOrElse {
        logger.warn("Model does not contain lines.")
        Set.empty
      },
      awaitedResults.transformers2w.map(_.toSet).getOrElse {
        logger.warn("Model does not contain two winding transformers.")
        Set.empty
      },
      awaitedResults.transformers3w.map(_.toSet).getOrElse {
        logger.debug("Model does not contain three winding transformers.")
        Set.empty
      },
      awaitedResults.switches.map(_.toSet).getOrElse {
        logger.debug("Model does not contain switches.")
        Set.empty
      },
      awaitedResults.measurements.map(_.toSet).getOrElse {
        logger.debug("Model does not contain measurements.")
        Set.empty
      },
      awaitedResults.loads.map(_.keys.toSet).getOrElse {
        logger.debug("Model does not contain loads.")
        Set.empty
      },
      (awaitedResults.res.map(_.keys).getOrElse {
        logger.debug("Model does not contain renewable energy sources.")
        Seq.empty[FixedFeedInInput]
      } ++ awaitedResults.powerPlants.map(_.keys).getOrElse {
        logger.debug("Model does not contain power plants.")
        Seq.empty[FixedFeedInInput]
      }).toSet,
      self
    )

    /* Build the time series mapping and send it to the mutator */
    val timeSeriesMapping: Map[UUID, UUID] = awaitedResults.loads
      .map(
        modelToTimeSeries =>
          modelToTimeSeries.map {
            case (model, uuid) => model.getUuid -> uuid
          }
      )
      .getOrElse {
        logger.warn("The model does not contain time series for loads.")
        Map.empty[UUID, UUID]
      } ++ awaitedResults.res
      .map(
        modelToTimeSeries =>
          modelToTimeSeries.map {
            case (model, uuid) => model.getUuid -> uuid
          }
      )
      .getOrElse {
        logger.warn(
          "The model does not contain time series for renewable energy sources."
        )
        Map.empty[UUID, UUID]
      } ++ awaitedResults.powerPlants
      .map(
        modelToTimeSeries =>
          modelToTimeSeries.map {
            case (model, uuid) => model.getUuid -> uuid
          }
      )
      .getOrElse {
        logger.warn("The model does not contain time series for power plants.")
        Map.empty[UUID, UUID]
      }

    mutator ! Mutator.PersistTimeSeriesMapping(timeSeriesMapping, self)

    /* Persist the nodal results */
    mutator ! Mutator.PersistNodalResults(awaitedResults.nodeResults.getOrElse {
      logger.warn("The model does not contain nodal results.")
      Set.empty[NodeResult]
    }.toSet, self)

    finalizing(simBenchCode, mutator, coordinator)
  }

  final case class StateData(
      simBenchCode: String,
      downloadDirectory: String,
      baseUrl: String,
      failOnExistingFiles: Boolean,
      inputFileEnding: String,
      inputFileEncoding: String,
      inputFileColumnSeparator: String,
      removeSwitches: Boolean,
      amountOfWorkers: Int,
      mutator: ActorRef[Mutator.MutatorMessage],
      coordinator: ActorRef[Coordinator.CoordinatorMessage]
  )

  final case class AwaitedResults(
      nodeConversion: Option[Map[Node, NodeInput]],
      nodeResults: Option[Vector[NodeResult]],
      lines: Option[Vector[LineInput]],
      transformers2w: Option[Vector[Transformer2WInput]],
      transformers3w: Option[Vector[Transformer3WInput]],
      switches: Option[Vector[SwitchInput]],
      measurements: Option[Vector[MeasurementUnitInput]],
      loads: Option[Map[LoadInput, UUID]],
      res: Option[Map[FixedFeedInInput, UUID]],
      powerPlants: Option[Map[FixedFeedInInput, UUID]]
  ) {

    /**
      * Check, if all awaited results are there
      *
      * @return true, if nothing is missing
      */
    def isReady: Boolean =
      Seq(
        nodeConversion,
        nodeResults,
        lines,
        transformers2w,
        transformers3w,
        switches,
        measurements,
        loads,
        res
        //powerPlants TODO
      ).forall(_.nonEmpty)
  }
  object AwaitedResults {
    def empty =
      new AwaitedResults(
        None,
        None,
        None,
        None,
        None,
        None,
        None,
        None,
        None,
        None
      )
  }

  /** Messages, a converter will understand */
  sealed trait ConverterMessage
  final case class Init(
      simBenchCode: String,
      baseUrl: String,
      downloadTargetDirectory: String,
      failDownloadOnExistingFiles: Boolean,
      inputFileEnding: String,
      inputFileEncoding: String,
      inputFileColumnSeparator: String,
      removeSwitches: Boolean,
      amountOfWorkers: Int,
      useDirectoryHierarchy: Boolean,
      targetDirectory: String,
      csvColumnSeparator: String,
      compressConverted: Boolean,
      replyTo: ActorRef[Coordinator.CoordinatorMessage]
  ) extends ConverterMessage

  /**
    * Report, that the attached [[Mutator]] is ready for action
    *
    * @param replyTo  Reference to the mutator
    */
  final case class MutatorInitialized(replyTo: ActorRef[Mutator.MutatorMessage])
      extends ConverterMessage

  /**
    * Report, that the [[Mutator]] has terminated
    */
  object MutatorTerminated extends ConverterMessage

  /**
    * Request to convert a certain SimBench model
    *
    * @param simBenchCode Code that denotes the model
    */
  final case class Convert(simBenchCode: String) extends ConverterMessage

  /**
    * Feedback, that a given set of nodes have been converted
    *
    * @param nodeConversion The conversion of nodes
    * @param nodeResults    Node results
    * @param lines          Lines
    * @param transformers2w Two winding transformer
    * @param transformers3w Three winding transformers
    * @param switches       Switches
    * @param measurements   Measurement devices
    */
  final case class GridStructureConverted(
      nodeConversion: Map[Node, NodeInput],
      nodeResults: Vector[NodeResult],
      lines: Vector[LineInput],
      transformers2w: Vector[Transformer2WInput],
      transformers3w: Vector[Transformer3WInput],
      switches: Vector[SwitchInput],
      measurements: Vector[MeasurementUnitInput]
  ) extends ConverterMessage

  final case class ResConverterReady(
      replyTo: ActorRef[ResConverter.ShuntConverterMessage]
  ) extends ConverterMessage
  final case class LoadConverterReady(
      replyTo: ActorRef[LoadConverter.ShuntConverterMessage]
  ) extends ConverterMessage

  final case class ResConverted(converted: Map[FixedFeedInInput, UUID])
      extends ConverterMessage

  final case class LoadsConverted(converted: Map[LoadInput, UUID])
      extends ConverterMessage

  object GridStructurePersisted extends ConverterMessage
  object TimeSeriesMappingPersisted extends ConverterMessage
  object NodalResultsPersisted extends ConverterMessage
}
