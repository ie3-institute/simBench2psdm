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
import edu.ie3.datamodel.models.input.system.FixedFeedInInput
import edu.ie3.datamodel.models.result.NodeResult
import edu.ie3.simbench.exception.CodeValidationException
import edu.ie3.simbench.io.{Downloader, SimbenchReader, Zipper}
import edu.ie3.simbench.model.SimbenchCode
import edu.ie3.simbench.model.datamodel.{GridModel, Node}

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
      ctx.log.debug(s"$simBenchCode - Start conversion of grid structure")
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

    case (ctx, ResConverted(converted)) =>
      ctx.log.info(
        s"${stateData.simBenchCode} - All RES are converted."
      )
      val updatedAwaitedResults = awaitedResults.copy(res = Some(converted))

      // TODO: Move the termination of the mutator to another place, when all entities are converted
      stateData.mutator ! Mutator.Terminate

      converting(stateData, simBenchModel, gridConverter, updatedAwaitedResults)

    case (ctx, MutatorTerminated) =>
      ctx.log.debug(s"${stateData.simBenchCode} - Mutator has terminated.")
      ctx.log.info(
        s"${stateData.simBenchCode} - Shut down converter."
      )
      stateData.coordinator ! Coordinator.Converted(stateData.simBenchCode)
      Behaviors.stopped
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
      res: Option[Map[FixedFeedInInput, UUID]],
      powerPlants: Option[Map[FixedFeedInInput, UUID]]
  )
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
      replyTo: ActorRef[ResConverter.ResConverterMessage]
  ) extends ConverterMessage

  final case class ResConverted(converted: Map[FixedFeedInInput, UUID])
      extends ConverterMessage
}
