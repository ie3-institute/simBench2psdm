package edu.ie3.simbench.actor

import akka.actor.typed.{ActorRef, DispatcherSelector, SupervisorStrategy}
import akka.actor.typed.scaladsl.{Behaviors, Routers}
import edu.ie3.datamodel.io.naming.{
  DefaultDirectoryHierarchy,
  EntityPersistenceNamingStrategy,
  FileNamingStrategy
}
import edu.ie3.datamodel.io.sink.CsvFileSink
import edu.ie3.datamodel.io.source.TimeSeriesMappingSource.MappingEntry
import edu.ie3.datamodel.models.input.{MeasurementUnitInput, NodeInput}
import edu.ie3.datamodel.models.input.connector.{
  LineInput,
  SwitchInput,
  Transformer2WInput,
  Transformer3WInput
}
import edu.ie3.datamodel.models.input.container.{
  GraphicElements,
  JointGridContainer,
  RawGridElements,
  SystemParticipants
}
import edu.ie3.datamodel.models.input.system.{
  BmInput,
  ChpInput,
  EvInput,
  EvcsInput,
  FixedFeedInInput,
  HpInput,
  LoadInput,
  PvInput,
  StorageInput,
  WecInput
}
import edu.ie3.datamodel.models.result.NodeResult
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries
import edu.ie3.simbench.actor.Mutator.Worker.MutatorWorkerMessage
import edu.ie3.simbench.io.IoUtils
import edu.ie3.util.io.FileIOUtils
import org.apache.commons.io.FilenameUtils

import java.nio.file.Paths
import java.util.UUID
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.jdk.FutureConverters.CompletionStageOps
import scala.util.{Failure, Success}
import scala.jdk.CollectionConverters._

object Mutator {
  def apply(): Behaviors.Receive[MutatorMessage] = uninitialized

  def uninitialized: Behaviors.Receive[MutatorMessage] = Behaviors.receive {
    case (
        ctx,
        Init(
          simBenchCode,
          useDirectoryHierarchy,
          targetDirectory,
          csvColumnSeparator,
          compress,
          amountOfWorkers,
          replyTo
        )
        ) =>
      ctx.log.debug(
        s"Initializing a mutator for SimBench code '$simBenchCode'."
      )

      val baseTargetDirectory =
        IoUtils.ensureHarmonizedAndTerminatingFileSeparator(targetDirectory)

      /* Starting up the worker pool */
      val workerPool = Routers
        .pool(poolSize = amountOfWorkers) {
          Behaviors
            .supervise(Mutator.Worker())
            .onFailure(SupervisorStrategy.restart)
        }
        /* Allow broadcast messages to init all workers */
        .withBroadcastPredicate {
          case _: Worker.Init      => true
          case _: Worker.Terminate => true
          case _                   => false
        }
        .withRoundRobinRouting()
      val workerPoolProxy =
        ctx.spawn(
          workerPool,
          s"MutatorWorkerPool_$simBenchCode",
          DispatcherSelector.sameAsParent()
        )
      /* Broadcasting init information to all workers */
      workerPoolProxy ! Worker.Init(
        simBenchCode,
        baseTargetDirectory,
        useDirectoryHierarchy,
        csvColumnSeparator,
        ctx.self
      )

      initializing(
        simBenchCode,
        baseTargetDirectory,
        compress,
        amountOfWorkers,
        amountOfWorkers,
        workerPoolProxy,
        replyTo
      )
  }

  def initializing(
      simBenchCode: String,
      targetDirectory: String,
      compress: Boolean,
      amountOfWorkers: Int,
      initializingWorkers: Int,
      workerPool: ActorRef[MutatorWorkerMessage],
      replyTo: ActorRef[Converter.ConverterMessage]
  ): Behaviors.Receive[MutatorMessage] = Behaviors.receive {
    case (ctx, MutatorWorkerInitialized) if initializingWorkers - 1 == 0 =>
      ctx.log.debug(s"$simBenchCode - All mutator workers ready.")
      replyTo ! Converter.MutatorInitialized(ctx.self)
      idle(simBenchCode, targetDirectory, amountOfWorkers, compress, workerPool)
    case (ctx, MutatorWorkerInitialized) =>
      val remainingWorkers = initializingWorkers - 1
      ctx.log.debug(
        s"$simBenchCode - Waiting for $remainingWorkers mutator workers to be initialized."
      )
      initializing(
        simBenchCode,
        targetDirectory,
        compress,
        amountOfWorkers,
        remainingWorkers,
        workerPool,
        replyTo
      )
  }

  def idle(
      simBenchCode: String,
      targetDirectory: String,
      amountOfWorkers: Int,
      compress: Boolean,
      workerPool: ActorRef[MutatorWorkerMessage]
  ): Behaviors.Receive[MutatorMessage] =
    Behaviors.receive {
      case (ctx, msg @ PersistTimeSeries(timeSeries, _)) =>
        ctx.log.debug(
          s"$simBenchCode - Forwarding request to persist time series '${timeSeries.getUuid}'."
        )
        workerPool ! msg.toWorkerMessage
        Behaviors.same

      case (
          ctx,
          msg: PersistGridStructure
          ) =>
        ctx.log.debug(
          s"$simBenchCode - Forwarding request to persist grid structure."
        )
        workerPool ! msg.toWorkerMessage
        Behaviors.same

      case (ctx, msg: PersistNodalResults) =>
        ctx.log.debug(
          s"$simBenchCode - Forwarding request to persist nodal results."
        )
        workerPool ! msg.toWorkerMessage
        Behaviors.same

      case (ctx, msg: PersistTimeSeriesMapping) =>
        ctx.log.debug(
          s"$simBenchCode - Forwarding request to persist time series mapping"
        )
        workerPool ! msg.toWorkerMessage
        Behaviors.same

      case (ctx, Terminate) =>
        ctx.log.debug(
          s"$simBenchCode - Got termination request. Shutting down mutator workers."
        )
        workerPool ! Worker.Terminate(ctx.self)
        shuttingDown(simBenchCode, targetDirectory, compress, amountOfWorkers)
    }

  def shuttingDown(
      simBenchCode: String,
      targetDirectory: String,
      compress: Boolean,
      shuttingDownWorkers: Int
  ): Behaviors.Receive[MutatorMessage] = Behaviors.receive {
    case (ctx, MutatorWorkerTerminated) if shuttingDownWorkers - 1 == 0 =>
      ctx.log.debug(
        s"$simBenchCode - All mutator workers have terminated.${if (compress) " Compress output files"}"
      )

      if (compress) {
        ctx.log.debug("Compressing output")
        val rawOutputPath = Paths.get(targetDirectory + simBenchCode)
        val archivePath = Paths.get(
          FilenameUtils.concat(targetDirectory, simBenchCode + ".tar.gz")
        )
        val compressFuture =
          FileIOUtils.compressDir(rawOutputPath, archivePath).asScala
        compressFuture.onComplete {
          case Success(_) =>
            FileIOUtils.deleteRecursively(rawOutputPath)
          case Failure(exception) =>
            ctx.log.error(
              s"Compression of output files to '$archivePath' has failed. Keep raw data.",
              exception
            )
        }

        Await.ready(compressFuture, Duration("180s"))
      }

      Behaviors.stopped
    case (ctx, MutatorWorkerTerminated) =>
      val remainingWorkers = shuttingDownWorkers - 1
      ctx.log.debug(
        s"$simBenchCode - Await shut down of $remainingWorkers mutator workers."
      )
      shuttingDown(simBenchCode, targetDirectory, compress, remainingWorkers)
  }

  /** Messages, a mutator will understand */
  sealed trait MutatorMessage
  final case class Init(
      simBenchCode: String,
      useDirectoryHierarchy: Boolean,
      targetDirectory: String,
      csvColumnSeparator: String,
      compress: Boolean,
      amountOfWorkers: Int,
      replyTo: ActorRef[Converter.ConverterMessage]
  ) extends MutatorMessage

  object MutatorWorkerInitialized extends MutatorMessage

  final case class PersistGridStructure(
      simBenchCode: String,
      nodes: Set[NodeInput],
      lines: Set[LineInput],
      transformers2w: Set[Transformer2WInput],
      transformers3w: Set[Transformer3WInput],
      switches: Set[SwitchInput],
      measurements: Set[MeasurementUnitInput],
      loads: Set[LoadInput],
      fixedFeedIns: Set[FixedFeedInInput],
      replyTo: ActorRef[Converter.ConverterMessage]
  ) extends MutatorMessage {
    def toWorkerMessage: Worker.PersistGridStructure =
      Worker.PersistGridStructure(
        simBenchCode,
        nodes,
        lines,
        transformers2w,
        transformers3w,
        switches,
        measurements,
        loads,
        fixedFeedIns,
        replyTo
      )
  }

  final case class PersistTimeSeriesMapping(
      mapping: Map[UUID, UUID],
      replyTo: ActorRef[Converter.ConverterMessage]
  ) extends MutatorMessage {
    def toWorkerMessage: Worker.PersistTimeSeriesMapping =
      Worker.PersistTimeSeriesMapping(mapping, replyTo)
  }

  final case class PersistNodalResults(
      results: Set[NodeResult],
      replyTo: ActorRef[Converter.ConverterMessage]
  ) extends MutatorMessage {
    def toWorkerMessage: Worker.PersistNodalResults =
      Worker.PersistNodalResults(results, replyTo)
  }

  final case class PersistTimeSeries(
      timeSeries: IndividualTimeSeries[_],
      replyTo: ActorRef[WorkerMessage]
  ) extends MutatorMessage {
    def toWorkerMessage: Worker.PersistTimeSeries =
      Worker.PersistTimeSeries(timeSeries, replyTo)
  }

  object Terminate extends MutatorMessage

  object MutatorWorkerTerminated extends MutatorMessage

  object Worker {
    def apply(): Behaviors.Receive[MutatorWorkerMessage] = uninitialized

    def uninitialized: Behaviors.Receive[MutatorWorkerMessage] = {
      Behaviors.receive {
        case (
            _,
            Init(
              simBenchCode,
              baseTargetDirectory,
              useDirectoryHierarchy,
              csvColumnSeparator,
              replyTo
            )
            ) =>
          /* Starting up a csv file sink */
          val csvSink = if (useDirectoryHierarchy) {
            new CsvFileSink(
              baseTargetDirectory,
              new FileNamingStrategy(
                new EntityPersistenceNamingStrategy(),
                new DefaultDirectoryHierarchy(baseTargetDirectory, simBenchCode)
              ),
              false,
              csvColumnSeparator
            )
          } else {
            new CsvFileSink(
              baseTargetDirectory + simBenchCode,
              new FileNamingStrategy(),
              false,
              csvColumnSeparator
            )
          }

          replyTo ! MutatorWorkerInitialized
          initialized(csvSink)
      }
    }

    def initialized(
        csvFileSink: CsvFileSink
    ): Behaviors.Receive[MutatorWorkerMessage] =
      Behaviors.receive {
        case (ctx, PersistTimeSeries(timeSeries, replyTo)) =>
          ctx.log.debug(
            s"Got request to persist time series '${timeSeries.getUuid}'."
          )
          csvFileSink.persistTimeSeries(timeSeries)
          replyTo ! WorkerMessage.TimeSeriesPersisted(timeSeries.getUuid)
          Behaviors.same

        case (
            ctx,
            PersistGridStructure(
              simBenchCode,
              nodes,
              lines,
              transformers2w,
              transformers3w,
              switches,
              measurements,
              loads,
              fixedFeedIns,
              replyTo
            )
            ) =>
          ctx.log.debug("Got request to persist grid structure.")

          val container = new JointGridContainer(
            simBenchCode,
            new RawGridElements(
              nodes.asJava,
              lines.asJava,
              transformers2w.asJava,
              transformers3w.asJava,
              switches.asJava,
              measurements.asJava
            ),
            new SystemParticipants(
              Set.empty[BmInput].asJava,
              Set.empty[ChpInput].asJava,
              Set.empty[EvcsInput].asJava,
              Set.empty[EvInput].asJava,
              fixedFeedIns.asJava,
              Set.empty[HpInput].asJava,
              loads.asJava,
              Set.empty[PvInput].asJava,
              Set.empty[StorageInput].asJava,
              Set.empty[WecInput].asJava
            ),
            new GraphicElements(
              Set.empty[GraphicElements].asJava
            )
          )

          csvFileSink.persistJointGrid(container)

          replyTo ! Converter.GridStructurePersisted
          Behaviors.same

        case (ctx, PersistNodalResults(results, replyTo)) =>
          ctx.log.debug("Got request to persist nodal results.")

          csvFileSink.persistAll(results.asJava)
          replyTo ! Converter.NodalResultsPersisted
          Behaviors.same

        case (ctx, PersistTimeSeriesMapping(mapping, replyTo)) =>
          ctx.log.debug("Got request to persist time series mapping")

          csvFileSink.persistAllIgnoreNested(
            mapping
              .map {
                case (modelUuid, timeSeriesUuid) =>
                  new MappingEntry(UUID.randomUUID(), modelUuid, timeSeriesUuid)
              }
              .toList
              .asJava
          )
          replyTo ! Converter.TimeSeriesMappingPersisted
          Behaviors.same

        case (ctx, Terminate(replyTo)) =>
          ctx.log.debug("Shutting down this mutator worker")
          replyTo ! MutatorWorkerTerminated
          Behaviors.stopped
      }

    sealed trait MutatorWorkerMessage
    final case class Init(
        simBenchCode: String,
        baseTargetDirectory: String,
        useDirectoryHierarchy: Boolean,
        csvColumnSeparator: String,
        replyTo: ActorRef[Mutator.MutatorMessage]
    ) extends MutatorWorkerMessage

    final case class PersistGridStructure(
        simBenchCode: String,
        nodes: Set[NodeInput],
        lines: Set[LineInput],
        transformers2w: Set[Transformer2WInput],
        transformers3w: Set[Transformer3WInput],
        switches: Set[SwitchInput],
        measurements: Set[MeasurementUnitInput],
        loads: Set[LoadInput],
        fixedFeedIns: Set[FixedFeedInInput],
        replyTo: ActorRef[Converter.ConverterMessage]
    ) extends MutatorWorkerMessage

    final case class PersistTimeSeriesMapping(
        mapping: Map[UUID, UUID],
        replyTo: ActorRef[Converter.ConverterMessage]
    ) extends MutatorWorkerMessage

    final case class PersistNodalResults(
        results: Set[NodeResult],
        replyTo: ActorRef[Converter.ConverterMessage]
    ) extends MutatorWorkerMessage

    final case class PersistTimeSeries(
        timeSeries: IndividualTimeSeries[_],
        replyTo: ActorRef[WorkerMessage]
    ) extends MutatorWorkerMessage

    final case class Terminate(replyTo: ActorRef[Mutator.MutatorMessage])
        extends MutatorWorkerMessage
  }
}
