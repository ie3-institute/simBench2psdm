package edu.ie3.simbench.actor

import akka.actor.typed.{ActorRef, SupervisorStrategy}
import akka.actor.typed.scaladsl.{Behaviors, Routers}
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
import tech.units.indriya.quantity.Quantities

import java.util.{Locale, UUID}

case object ResConverter extends ShuntConverter {
  def apply(): Behaviors.Receive[ResConverterMessage] = uninitialized

  def uninitialized: Behaviors.Receive[ResConverterMessage] =
    Behaviors.receive {
      case (
          ctx,
          Init(simBenchCode, amountOfWorkers, profiles, mutator, converter)
          ) =>
        /* Prepare information */
        val typeToProfile =
          profiles.map(profile => profile.profileType -> profile).toMap

        /* Set up a worker pool */
        val workerPool = Routers
          .pool(poolSize = amountOfWorkers) {
            Behaviors
              .supervise(ResConverter.Worker())
              .onFailure(SupervisorStrategy.restart)
          }
          /* Allow broadcast messages to init all workers */
          .withBroadcastPredicate {
            case _: Worker.Init => true
            case _              => false
          }
          .withRoundRobinRouting()
        val workerPoolProxy =
          ctx.spawn(
            workerPool,
            s"ResConverterWorkerPool_$simBenchCode"
          )

        workerPoolProxy ! Worker.Init(mutator)
        converter ! Converter.ResConverterReady(ctx.self)

        idle(typeToProfile, workerPoolProxy)
    }

  def idle(
      typeToProfile: Map[ResProfileType, ResProfile],
      workerPool: ActorRef[ResConverter.Worker.WorkerMessage]
  ): Behaviors.Receive[ResConverterMessage] = Behaviors.receive {
    case (ctx, Convert(simBenchCode, res, nodes, converter)) =>
      ctx.log.debug(s"Got request to convert res from '$simBenchCode'.")
      val activeConversions = res.map { plant =>
        val node = NodeConverter.getNode(plant.node, nodes)
        val profile =
          PowerProfileConverter.getProfile(plant.profile, typeToProfile)

        workerPool ! Worker.Convert(plant, node, profile, ctx.self)
        (plant.id, plant.node.getKey)
      }
      converting(activeConversions, Map.empty, workerPool, converter)
  }

  def converting(
      activeConversions: Vector[(String, Node.NodeKey)],
      converted: Map[FixedFeedInInput, UUID],
      workerPool: ActorRef[Worker.WorkerMessage],
      converter: ActorRef[Converter.ConverterMessage]
  ): Behaviors.Receive[ResConverterMessage] = Behaviors.receive {
    case (ctx, Converted(id, node, fixedFeedInInput, timeSeriesUuid)) =>
      val remainingConversions = activeConversions.filterNot(_ == (id, node))
      val updatedConverted = converted + (fixedFeedInInput -> timeSeriesUuid)
      ctx.log.debug(
        s"Model '$id' at node '$node' is converted. ${remainingConversions.size} active conversions remaining."
      )
      /* Stop the children and myself, if all conversions are done. */
      if (remainingConversions.isEmpty) {
        ctx.stop(workerPool)
        converter ! Converter.ResConverted(converted)
        Behaviors.stopped
      }
      converting(remainingConversions, updatedConverted, workerPool, converter)
  }

  sealed trait ResConverterMessage

  final case class Init(
      simBenchCode: String,
      amountOfWorkers: Int,
      profiles: Vector[ResProfile],
      mutator: ActorRef[Mutator.MutatorMessage],
      replyTo: ActorRef[Converter.ConverterMessage]
  ) extends ResConverterMessage

  final case class Converted(
      id: String,
      node: Node.NodeKey,
      fixedFeedInInput: FixedFeedInInput,
      timeSeriesUuid: UUID
  ) extends ResConverterMessage

  /**
    * Request to convert all given RES
    *
    * @param simBenchCode Code of the referencing model
    * @param res          Input models to convert
    * @param nodes        Mapping from SimBench to power system data model node
    * @param replyTo      Converter to report to
    */
  final case class Convert(
      simBenchCode: String,
      res: Vector[RES],
      nodes: Map[Node, NodeInput],
      replyTo: ActorRef[Converter.ConverterMessage]
  ) extends ResConverterMessage

  object Worker {
    def apply(): Behaviors.Receive[WorkerMessage] = uninitialized

    def uninitialized: Behaviors.Receive[WorkerMessage] = Behaviors.receive {
      case (_, Init(mutator)) =>
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
              ActorRef[ResConverterMessage]
          )
        ] = Map.empty
    ): Behaviors.Receive[WorkerMessage] = Behaviors.receive {
      case (ctx, Convert(res, node, profile, replyTo)) =>
        Behaviors.same
        ctx.log.debug(
          s"Got request to convert RES '${res.id} at node '${res.node.getKey} / ${node.getUuid}'."
        )

        val model = convertModel(res, node)
        /* Flip the sign, as infeed is negative in PowerSystemDataModel */
        val timeSeries = PowerProfileConverter.convert(
          profile,
          Quantities.getQuantity(res.p, MEGAWATT).multiply(-1)
        )

        mutator ! Mutator.PersistTimeSeries(timeSeries, ctx.self)
        val updatedAwaitedTimeSeriesPersistence = awaitTimeSeriesPersistence + (timeSeries.getUuid -> (res.id, res.node.getKey, model, replyTo))
        idle(mutator, updatedAwaitedTimeSeriesPersistence)

      case (ctx, TimeSeriesPersisted(uuid)) =>
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

    sealed trait WorkerMessage
    final case class Init(mutator: ActorRef[Mutator.MutatorMessage])
        extends WorkerMessage
    final case class Convert(
        res: RES,
        node: NodeInput,
        profile: ResProfile,
        replyTo: ActorRef[ResConverterMessage]
    ) extends WorkerMessage
    final case class TimeSeriesPersisted(uuid: UUID) extends WorkerMessage

    /**
      * Converts a single renewable energy source system to a fixed feed in model due to lacking information to
      * sophistical guess typical types of assets. Different voltage regulation strategies are not covered, yet.
      *
      * @param input   Input model
      * @param node    Node, the renewable energy source system is connected to
      * @param uuid    Option to a specific uuid
      * @return A pair of [[FixedFeedInInput]] and matching active power time series
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
