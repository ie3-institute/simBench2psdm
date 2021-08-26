package edu.ie3.simbench.actor

import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.Behaviors
import edu.ie3.datamodel.io.naming.{
  DefaultDirectoryHierarchy,
  EntityPersistenceNamingStrategy,
  FileNamingStrategy
}
import edu.ie3.datamodel.io.sink.CsvFileSink
import edu.ie3.simbench.io.IoUtils

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
      idle(csvSink, compress)
  }

  def idle(
      sink: CsvFileSink,
      compress: Boolean
  ): Behaviors.Receive[MutatorMessage] =
    Behaviors.receive {
      case _ => Behaviors.ignore
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
}
