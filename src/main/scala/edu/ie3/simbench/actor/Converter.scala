package edu.ie3.simbench.actor

import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}

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
      initializing(simBenchCode, converter)
  }

  /**
    * Receive needed information while being in the process of initialization
    *
    * @param simBenchCode SimBench code to handle later
    * @param coordinator  Reference to the coordinator
    * @return Behavior to do so
    */
  def initializing(
      simBenchCode: String,
      coordinator: ActorRef[Coordinator.CoordinatorMessage]
  ): Behaviors.Receive[ConverterMessage] = Behaviors.receive {
    case (ctx, MutatorInitialized(mutator)) =>
      ctx.log.debug(s"Mutator for model '$simBenchCode' is ready.")
      coordinator ! Coordinator.ConverterInitialized(simBenchCode, ctx.self)
      idle(mutator)
  }

  def idle(
      mutator: ActorRef[Mutator.MutatorMessage]
  ): Behaviors.Receive[ConverterMessage] = Behaviors.receive {
    case _ => Behaviors.same
  }

  // TODO: Terminate mutator and await it's termination [[MutatorTerminated]] when terminating this actor

  def spawnMutator(
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

  /** Messages, a converter will understand */
  sealed trait ConverterMessage
  final case class Init(
      simBenchCode: String,
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
