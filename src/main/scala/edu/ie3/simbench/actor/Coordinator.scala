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
          spawnConverter(ctx, firstSimBenchCode)
          val initializingConverters = List(firstSimBenchCode)
          idle(remainingCodes, initializingConverters)
        case None =>
          ctx.log.error("No models to convert. Shut down.")
          Behaviors.stopped
      }
  }

  def idle(
      simBenchCodes: List[String],
      initializingConverters: List[String],
      activeConverters: List[String] = List.empty
  ): Behaviors.Receive[CoordinatorMessage] = Behaviors.receive {
    case (ctx, ConverterInitialized(simBenchCode, replyTo)) =>
      ctx.log.debug(s"Reading to convert SimBench mode '$simBenchCode'.")
      replyTo ! Converter.Convert(simBenchCode)

      val stillInitializingConverters =
        initializingConverters.filterNot(_ == simBenchCode)
      val yetActiveConverters = activeConverters :+ simBenchCode
      idle(simBenchCodes, stillInitializingConverters, yetActiveConverters)
    case (ctx, Converted(simBenchCode)) if simBenchCodes.nonEmpty =>
      /* A converter has completed. Report that and start a new converter. */
      ctx.log.info(
        s"SimBench model with code '$simBenchCode' is completely converted."
      )
      val stillActiveConverters = activeConverters.filterNot(_ == simBenchCode)
      simBenchCodes.headOption match {
        case Some(nextSimBenchCode) =>
          /* Spawn a converter and wait until it is ready */
          val remainingCodes = simBenchCodes.filter(_ != nextSimBenchCode)
          spawnConverter(ctx, nextSimBenchCode)
          val yetInitializingConverters = initializingConverters :+ nextSimBenchCode
          idle(remainingCodes, yetInitializingConverters, activeConverters)
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
      simBenchCode: String
  ): Unit = {
    val converter = ctx.spawn(Converter(), s"converter_$simBenchCode")
    converter ! Converter.Init(simBenchCode, ctx.self)
  }

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
