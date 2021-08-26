package edu.ie3.simbench.actor

import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.Behaviors

object Converter {
  def apply(): Behaviors.Receive[ConverterMessage] = uninitialized

  def uninitialized: Behaviors.Receive[ConverterMessage] = Behaviors.receive {
    case (ctx, Init(simBenchCode, replyTo)) =>
      ctx.log.info(s"Converter for '$simBenchCode' started.")
      // TODO
      Behaviors.same
  }

  /** Messages, a coordinator will understand */
  sealed trait ConverterMessage
  final case class Init(
      simBenchCode: String,
      replyTo: ActorRef[Coordinator.CoordinatorMessage]
  ) extends ConverterMessage

  /**
    * Request to convert a certain SimBench model
    *
    * @param simBenchCode Code that denotes the model
    */
  final case class Convert(simBenchCode: String) extends ConverterMessage
}
