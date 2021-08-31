package edu.ie3.simbench.actor

import akka.actor.typed.{ActorRef, SupervisorStrategy}
import akka.actor.typed.scaladsl.{ActorContext, Behaviors, Routers}
import edu.ie3.datamodel.models.OperationTime
import edu.ie3.datamodel.models.input.system.FixedFeedInInput
import edu.ie3.datamodel.models.input.system.characteristic.CosPhiFixed
import edu.ie3.datamodel.models.input.{NodeInput, OperatorInput}
import edu.ie3.simbench.convert.profiles.PowerProfileConverter
import edu.ie3.simbench.convert.{NodeConverter, ShuntConverter}
import edu.ie3.simbench.model.datamodel.profiles.{ResProfile, ResProfileType}
import edu.ie3.simbench.model.datamodel.{Node, RES}
import edu.ie3.util.quantities.PowerSystemUnits.{
  MEGAVAR,
  MEGAVOLTAMPERE,
  MEGAWATT
}
import tech.units.indriya.ComparableQuantity
import tech.units.indriya.quantity.Quantities

import java.util.{Locale, UUID}
import javax.measure.quantity.Power

case object ResConverter
    extends ShuntConverter
    with ShuntConverterMessageSupport[RES, ResProfile, FixedFeedInInput] {
  def apply(): Behaviors.Receive[ShuntConverterMessage] = uninitialized

  def uninitialized: Behaviors.Receive[ShuntConverterMessage] =
    Behaviors.receive {
      case (
          ctx,
          init: Init
          ) =>
        /* Set up a worker pool */
        val workerPoolProxy = setupWorkerPoolAndReportReady(
          init.simBenchCode,
          init.amountOfWorkers,
          ctx,
          init.mutator,
          init.replyTo
        )

        /* Prepare information */
        init match {
          case InitWithTimeSeries(_, _, profiles, _, _) =>
            val typeToProfile =
              profiles.map(profile => profile.profileType -> profile).toMap
            idle(typeToProfile, workerPoolProxy, createTimeSeries = true)
          case _: InitWithoutTimeSeries =>
            idle(
              Map.empty[ResProfileType, ResProfile],
              workerPoolProxy,
              createTimeSeries = false
            )
        }
    }

  def idle(
      typeToProfile: Map[ResProfileType, ResProfile],
      workerPool: ActorRef[WorkerMessage],
      createTimeSeries: Boolean
  ): Behaviors.Receive[ShuntConverterMessage] = Behaviors.receive {
    case (ctx, Convert(simBenchCode, res, nodes, converter)) =>
      ctx.log.debug(s"Got request to convert res from '$simBenchCode'.")
      val activeConversions = res.map { plant =>
        val node = NodeConverter.getNode(plant.node, nodes)

        if (createTimeSeries) {
          val profile =
            PowerProfileConverter.getProfile(plant.profile, typeToProfile)
          workerPool ! Worker
            .ConvertWithTimeSeries(plant, node, profile, ctx.self)
        } else
          workerPool ! Worker.ConvertWithoutTimeSeries(plant, node, ctx.self)
        (plant.id, plant.node.getKey)
      }
      converting(activeConversions, Map.empty, workerPool, converter)
  }

  def converting(
      activeConversions: Vector[(String, Node.NodeKey)],
      converted: Map[FixedFeedInInput, UUID],
      workerPool: ActorRef[WorkerMessage],
      converter: ActorRef[Converter.ConverterMessage]
  ): Behaviors.Receive[ShuntConverterMessage] = Behaviors.receive {
    case (ctx, Converted(id, node, fixedFeedInInput, timeSeriesUuid)) =>
      val remainingConversions = activeConversions.filterNot(_ == (id, node))
      val updatedConverted = converted + (fixedFeedInInput -> timeSeriesUuid)
      ctx.log.debug(
        s"Model '$id' at node '$node' is converted. ${remainingConversions.size} active conversions remaining."
      )
      /* Stop the children and myself, if all conversions are done. */
      if (remainingConversions.isEmpty) {
        ctx.stop(workerPool)
        converter ! Converter.ResConverted(updatedConverted)
        Behaviors.stopped
      }
      converting(remainingConversions, updatedConverted, workerPool, converter)
  }

  /**
    * Set up the worker pool and report to be ready
    *
    * @param simBenchCode     SimBench model to convert
    * @param amountOfWorkers  Amount of workers to use
    * @param ctx              Current actor context
    * @param mutator          Reference to the mutator
    * @param converter        Reference to the converter
    * @return A reference to the worker pool proxy
    */
  private def setupWorkerPoolAndReportReady(
      simBenchCode: String,
      amountOfWorkers: Int,
      ctx: ActorContext[ShuntConverterMessage],
      mutator: ActorRef[Mutator.MutatorMessage],
      converter: ActorRef[Converter.ConverterMessage]
  ): ActorRef[WorkerMessage] = {
    val workerPoolProxy =
      spawnWorkerPool(simBenchCode, amountOfWorkers, ctx, mutator, converter)

    workerPoolProxy ! WorkerMessage.Init(mutator)
    converter ! Converter.ResConverterReady(ctx.self)
    workerPoolProxy
  }

  /**
    * Spawning a worker pool for this Converter
    *
    * @param simBenchCode     SimBench model to convert
    * @param amountOfWorkers  Amount of workers to use
    * @param ctx              Current actor context
    * @param mutator          Reference to the mutator
    * @param converter        Reference to the converter
    * @return A reference to the worker pool proxy
    */
  private def spawnWorkerPool(
      simBenchCode: String,
      amountOfWorkers: Int,
      ctx: ActorContext[ShuntConverterMessage],
      mutator: ActorRef[Mutator.MutatorMessage],
      converter: ActorRef[Converter.ConverterMessage]
  ): ActorRef[WorkerMessage] = {
    /* Set up a worker pool */
    val workerPool = Routers
      .pool(poolSize = amountOfWorkers) {
        Behaviors
          .supervise(ResConverter.Worker())
          .onFailure(SupervisorStrategy.restart)
      }
      /* Allow broadcast messages to init all workers */
      .withBroadcastPredicate {
        case _: WorkerMessage.Init => true
        case _                     => false
      }
      .withRoundRobinRouting()
    ctx.spawn(
      workerPool,
      s"ResConverterWorkerPool_$simBenchCode"
    )
  }

  final case class InitWithTimeSeries(
      simBenchCode: String,
      amountOfWorkers: Int,
      profiles: Vector[ResProfile],
      mutator: ActorRef[Mutator.MutatorMessage],
      replyTo: ActorRef[Converter.ConverterMessage]
  ) extends super.InitWithTimeSeries

  final case class InitWithoutTimeSeries(
      simBenchCode: String,
      amountOfWorkers: Int,
      mutator: ActorRef[Mutator.MutatorMessage],
      replyTo: ActorRef[Converter.ConverterMessage]
  ) extends super.InitWithoutTimeSeries

  /**
    * Request to convert all given models
    */
  final case class Convert(
      simBenchCode: String,
      inputs: Vector[RES],
      nodes: Map[Node, NodeInput],
      replyTo: ActorRef[Converter.ConverterMessage]
  ) extends super.Convert

  final case class Converted(
      id: String,
      node: Node.NodeKey,
      model: FixedFeedInInput,
      timeSeriesUuid: UUID
  ) extends super.Converted

  object Worker {
    def apply(): Behaviors.Receive[WorkerMessage] = uninitialized

    def uninitialized: Behaviors.Receive[WorkerMessage] = Behaviors.receive {
      case (_, WorkerMessage.Init(mutator)) =>
        idle(mutator)
    }

    def idle(
        mutator: ActorRef[Mutator.MutatorMessage],
        awaitTimeSeriesPersistence: Map[
          UUID,
          (
              String,
              Node.NodeKey,
              FixedFeedInInput,
              ActorRef[ShuntConverterMessage]
          )
        ] = Map.empty
    ): Behaviors.Receive[WorkerMessage] = Behaviors.receive {
      case (ctx, convertRequest: WorkerMessage.Convert[RES, _, _]) =>
        ctx.log.debug(
          s"Got request to convert res '${convertRequest.model.id} at node '${convertRequest.model.node.getKey} " +
            s"/ ${convertRequest.node.getUuid}'."
        )

        /* Convert the model */
        val model = convertModel(convertRequest.model, convertRequest.node)

        /* If needed, convert time series and await their finishing */
        convertRequest match {
          case ConvertWithTimeSeries(input, _, profile, replyTo) =>
            /* Flip the sign, as infeed is negative in PowerSystemDataModel */
            val timeSeries = PowerProfileConverter.convert(
              profile,
              Quantities
                .getQuantity(convertRequest.model.p, MEGAWATT)
                .multiply(-1)
            )

            mutator ! Mutator.PersistTimeSeries(timeSeries, ctx.self)
            val updatedAwaitedTimeSeriesPersistence = awaitTimeSeriesPersistence +
              (timeSeries.getUuid -> (input.id, input.node.getKey, model, replyTo))
            idle(mutator, updatedAwaitedTimeSeriesPersistence)
          case _: ConvertWithoutTimeSeries =>
            idle(mutator, awaitTimeSeriesPersistence)
        }

      case (ctx, WorkerMessage.TimeSeriesPersisted(uuid)) =>
        ctx.log.debug(s"Time series '$uuid' is fully persisted.")
        awaitTimeSeriesPersistence.get(uuid) match {
          case Some((id, nodeKey, model, replyTo)) =>
            val remainingPersistence =
              awaitTimeSeriesPersistence.filterNot(_._1 == uuid)

            replyTo ! ResConverter.Converted(id, nodeKey, model, uuid)

            idle(mutator, remainingPersistence)
          case None =>
            ctx.log.warn(
              s"Got informed, that the time series with uuid '$uuid' is persisted. But I didn't expect that to happen."
            )
            Behaviors.same
        }
    }

    /**
      * Override the abstract Request message with parameters, that suit your needs.
      *
      * @param model    Model itself
      * @param node     Node, the converted model will be connected to
      * @param profile  The profile, that belongs to the model
      * @param replyTo  Address to reply to
      */
    final case class ConvertWithTimeSeries(
        override val model: RES,
        override val node: NodeInput,
        override val profile: ResProfile,
        override val replyTo: ActorRef[ShuntConverterMessage]
    ) extends WorkerMessage.ConvertWithTimeSeries[
          RES,
          ResProfile,
          ShuntConverterMessage
        ]

    /**
      * Override the abstract Request message with parameters, that suit your needs.
      *
      * @param model    Model itself
      * @param node     Node, the converted model will be connected to
      * @param replyTo  Address to reply to
      */
    final case class ConvertWithoutTimeSeries(
        override val model: RES,
        override val node: NodeInput,
        override val replyTo: ActorRef[ShuntConverterMessage]
    ) extends WorkerMessage.ConvertWithoutTimeSeries[
          RES,
          ResProfile,
          ShuntConverterMessage
        ]

    /**
      * Converts a single renewable energy source system to a fixed feed in model due to lacking information to
      * sophistical guess typical types of assets. Different voltage regulation strategies are not covered, yet.
      *
      * @param input   Input model
      * @param node    Node, the renewable energy source system is connected to
      * @param uuid    Option to a specific uuid
      * @return A [[FixedFeedInInput]]
      */
    def convertModel(
        input: RES,
        node: NodeInput,
        uuid: Option[UUID] = None
    ): FixedFeedInInput = {
      val p = Quantities.getQuantity(input.p, MEGAWATT)
      val q = Quantities.getQuantity(input.q, MEGAVAR)
      val cosphi = cosPhi(p.getValue.doubleValue(), q.getValue.doubleValue())
      val varCharacteristicString =
        "cosPhiFixed:{(0.0,%#.2f)}".formatLocal(Locale.ENGLISH, cosphi)
      val sRated = Quantities.getQuantity(input.sR, MEGAVOLTAMPERE)

      new FixedFeedInInput(
        uuid.getOrElse(UUID.randomUUID()),
        input.id + "_" + input.resType.toString,
        OperatorInput.NO_OPERATOR_ASSIGNED,
        OperationTime.notLimited(),
        node,
        new CosPhiFixed(varCharacteristicString),
        sRated,
        cosphi
      )
    }
  }
}
