package edu.ie3.simbench.actor

import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import edu.ie3.simbench.exception.CodeValidationException
import edu.ie3.simbench.io.{Downloader, SimbenchReader, Zipper}
import edu.ie3.simbench.model.SimbenchCode
import edu.ie3.simbench.model.datamodel.GridModel

import java.nio.file.Path

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
          useDirectoryHierarchy,
          targetDirectory,
          csvColumnSeparator,
          compress,
          converter
        )
        ) =>
      ctx.log.info(s"Converter for '$simBenchCode' started.")

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
      coordinator: ActorRef[Coordinator.CoordinatorMessage]
  ): Behaviors.Receive[ConverterMessage] = Behaviors.receive {
    case (ctx, MutatorInitialized(mutator)) =>
      ctx.log.debug(s"Mutator for model '$simBenchCode' is ready.")
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
          mutator
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
      ctx.log.info(s"$simBenchCode - Reading in the SimBench data set")
      val simBenchModel = readModel(
        simBenchCode,
        downloadDirectory,
        stateData.inputFileColumnSeparator,
        stateData.inputFileEnding,
        stateData.inputFileEncoding
      )
      /*
       * TODO
       *  3) Issue conversion
       */
      Behaviors.same
  }

  // TODO: Terminate mutator and await it's termination [[MutatorTerminated]] when terminating this actor

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
    val simbenchReader = SimbenchReader(
      simBenchCode,
      downloadDirectory,
      csvColumnSeparator,
      fileEnding,
      fileEncoding
    )
    simbenchReader.readGrid()
  }

  final case class StateData(
      simBenchCode: String,
      downloadDirectory: String,
      baseUrl: String,
      failOnExistingFiles: Boolean,
      inputFileEnding: String,
      inputFileEncoding: String,
      inputFileColumnSeparator: String,
      mutator: ActorRef[Mutator.MutatorMessage]
  )

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
}
