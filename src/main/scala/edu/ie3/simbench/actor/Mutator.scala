package edu.ie3.simbench.actor

import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.Behaviors
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
  SystemParticipantInput,
  WecInput
}
import edu.ie3.datamodel.models.result.NodeResult
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries
import edu.ie3.simbench.io.IoUtils
import edu.ie3.util.io.FileIOUtils
import org.apache.commons.io.FilenameUtils

import java.nio.file.Paths
import java.util
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
          replyTo
        )
        ) =>
      ctx.log.debug(
        s"Initializing a mutator for SimBench code '$simBenchCode'."
      )

      val baseTargetDirectory =
        IoUtils.ensureHarmonizedAndTerminatingFileSeparator(targetDirectory)

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

      replyTo ! Converter.MutatorInitialized(ctx.self)
      idle(simBenchCode, csvSink, baseTargetDirectory, compress)
  }

  def idle(
      simBenchCode: String,
      sink: CsvFileSink,
      targetDirectory: String,
      compress: Boolean
  ): Behaviors.Receive[MutatorMessage] =
    Behaviors.receive {
      case (ctx, PersistTimeSeries(timeSeries, replyTo)) =>
        ctx.log.debug(
          s"Got request to persist time series '${timeSeries.getUuid}'."
        )
        sink.persistTimeSeries(timeSeries)
        replyTo ! ResConverter.Worker.TimeSeriesPersisted(timeSeries.getUuid)
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

        sink.persistJointGrid(container)

        replyTo ! Converter.GridStructurePersisted
        Behaviors.same

      case (ctx, PersistNodalResults(results, replyTo)) =>
        ctx.log.debug("Got request to persist nodal results.")

        sink.persistAll(results.asJava)
        replyTo ! Converter.NodalResultsPersisted
        Behaviors.same

      case (ctx, PersistTimeSeriesMapping(mapping, replyTo)) =>
        ctx.log.debug("Got request to persist time series mapping")

        sink.persistAllIgnoreNested(
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

      case (ctx, Terminate) =>
        ctx.log.debug("Got termination request")

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

        sink.shutdown()

        Behaviors.stopped
    }

  /** Messages, a mutator will understand */
  sealed trait MutatorMessage
  final case class Init(
      simBenchCode: String,
      useDirectoryHierarchy: Boolean,
      targetDirectory: String,
      csvColumnSeparator: String,
      compress: Boolean,
      replyTo: ActorRef[Converter.ConverterMessage]
  ) extends MutatorMessage

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
  ) extends MutatorMessage

  final case class PersistTimeSeriesMapping(
      mapping: Map[UUID, UUID],
      replyTo: ActorRef[Converter.ConverterMessage]
  ) extends MutatorMessage

  final case class PersistNodalResults(
      results: Set[NodeResult],
      replyTo: ActorRef[Converter.ConverterMessage]
  ) extends MutatorMessage

  final case class PersistTimeSeries(
      timeSeries: IndividualTimeSeries[_],
      replyTo: ActorRef[ResConverter.Worker.WorkerMessage]
  ) extends MutatorMessage

  object Terminate extends MutatorMessage
}
