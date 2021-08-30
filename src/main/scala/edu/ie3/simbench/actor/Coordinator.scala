package edu.ie3.simbench.actor

import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import edu.ie3.simbench.config.SimbenchConfig

object Coordinator {
  def apply(): Behaviors.Receive[CoordinatorMessage] = uninitialized

  def uninitialized: Behaviors.Receive[CoordinatorMessage] = Behaviors.receive {
    case (ctx, Start(config)) =>
      ctx.log.info("Starting conversion with given config.")
      config.io.simbenchCodes.headOption match {
        case Some(firstSimBenchCode) =>
          val remainingCodes =
            config.io.simbenchCodes.filter(_ != firstSimBenchCode)

          /* Start a converter and issue the conversion */
          spawnConverter(
            ctx,
            firstSimBenchCode,
            config.io.input.download.baseUrl,
            config.io.input.download.folder,
            config.io.input.download.failOnExistingFiles,
            config.io.input.csv.fileEnding,
            config.io.input.csv.fileEncoding,
            config.io.input.csv.separator,
            config.conversion.removeSwitches,
            config.conversion.participantWorkersPerType,
            config.io.output.csv.directoryHierarchy,
            config.io.output.targetFolder,
            config.io.output.csv.separator,
            config.io.output.compress,
            config.io.output.workers
          )
          val initializingConverters = List(firstSimBenchCode)

          val stateData = StateData(
            remainingCodes,
            initializingConverters,
            List.empty[String],
            config.io.input.download.baseUrl,
            config.io.input.download.folder,
            config.io.input.download.failOnExistingFiles,
            config.io.input.csv.fileEnding,
            config.io.input.csv.fileEncoding,
            config.io.input.csv.separator,
            config.conversion.removeSwitches,
            config.conversion.participantWorkersPerType,
            config.io.output.csv.directoryHierarchy,
            config.io.output.targetFolder,
            config.io.output.csv.separator,
            config.io.output.compress,
            config.io.output.workers
          )

          idle(stateData)
        case None =>
          ctx.log.error("No models to convert. Shut down.")
          Behaviors.stopped
      }
  }

  def idle(
      stateData: StateData
  ): Behaviors.Receive[CoordinatorMessage] = Behaviors.receive {
    case (ctx, ConverterInitialized(simBenchCode, replyTo)) =>
      ctx.log.debug(s"Reading to convert SimBench mode '$simBenchCode'.")
      replyTo ! Converter.Convert(simBenchCode)

      val stillInitializingConverters =
        stateData.initializingConverters.filterNot(_ == simBenchCode)
      val yetActiveConverters = stateData.activeConverters :+ simBenchCode
      idle(
        stateData.copy(
          initializingConverters = stillInitializingConverters,
          activeConverters = yetActiveConverters
        )
      )
    case (ctx, Converted(simBenchCode)) =>
      /* A converter has completed. Report that and start a new converter. */
      ctx.log.info(
        s"$simBenchCode - Completely converted."
      )
      val stillActiveConverters =
        stateData.activeConverters.filterNot(_ == simBenchCode)
      stateData.simBenchCodes.headOption match {
        case Some(nextSimBenchCode) =>
          /* Spawn a converter and wait until it is ready */
          val remainingCodes =
            stateData.simBenchCodes.filter(_ != nextSimBenchCode)
          spawnConverter(
            ctx,
            nextSimBenchCode,
            stateData.baseUrl,
            stateData.downloadTargetDirectory,
            stateData.failDownloadOnExistingFiles,
            stateData.inputFileEnding,
            stateData.inputFileEncoding,
            stateData.inputFileColumnSeparator,
            stateData.removeSwitches,
            stateData.amountOfWorkers,
            stateData.useDirectoryHierarchy,
            stateData.targetDirectory,
            stateData.csvColumnSeparator,
            stateData.compressConverted,
            stateData.amountOfMutatorWorkers
          )
          val yetInitializingConverters = stateData.initializingConverters :+ nextSimBenchCode
          idle(
            stateData.copy(
              simBenchCodes = remainingCodes,
              initializingConverters = yetInitializingConverters
            )
          )
        case None if stillActiveConverters.nonEmpty =>
          ctx.log.debug(
            s"Waiting for last ${stillActiveConverters.size} active converter(s)."
          )
          Behaviors.same
        case None =>
          ctx.log.info("All models have been converted. Shut down.")
          Behaviors.stopped
      }
  }

  def spawnConverter(
      ctx: ActorContext[CoordinatorMessage],
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
      amountOfMutatorWorkers: Int
  ): Unit = {
    val converter = ctx.spawn(Converter(), s"converter_$simBenchCode")
    converter ! Converter.Init(
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
      compressConverted,
      amountOfMutatorWorkers,
      ctx.self
    )
  }

  final case class StateData(
      simBenchCodes: List[String],
      initializingConverters: List[String],
      activeConverters: List[String],
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
      amountOfMutatorWorkers: Int
  )

  /** Messages, a coordinator will understand */
  sealed trait CoordinatorMessage

  /**
    * Request to start conversion
    *
    * @param config The config to use for conversion
    */
  final case class Start(config: SimbenchConfig) extends CoordinatorMessage

  /**
    * Reporting, that a certain converter is ready for action
    *
    * @param simBenchCode Code of the SimBench model it will handle
    * @param replyTo      Reference to where to reach the converter
    */
  final case class ConverterInitialized(
      simBenchCode: String,
      replyTo: ActorRef[Converter.ConverterMessage]
  ) extends CoordinatorMessage

  /**
    * Back-reporting, that a given SimBench-code has been handled
    *
    * @param simBenchCode Handle SimBench code
    */
  final case class Converted(simBenchCode: String) extends CoordinatorMessage
}
