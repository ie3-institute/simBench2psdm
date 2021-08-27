package edu.ie3.simbench.actor

import akka.actor.typed.{ActorRef, SupervisorStrategy}
import akka.actor.typed.scaladsl.{Behaviors, Routers}
import edu.ie3.datamodel.models.OperationTime
import edu.ie3.datamodel.models.input.system.FixedFeedInInput
import edu.ie3.datamodel.models.input.system.characteristic.CosPhiFixed
import edu.ie3.datamodel.models.input.{NodeInput, OperatorInput}
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries
import edu.ie3.datamodel.models.value.PValue
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
      case (ctx, Init(simBenchCode, amountOfWorkers, profiles, converter)) =>
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
          .withRoundRobinRouting()
        val workerPoolProxy =
          ctx.spawn(
            workerPool,
            s"ResConverterWorkerPool_$simBenchCode"
          )

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
      converting(activeConversions, Vector.empty, workerPool, converter)
  }

  def converting(
      activeConversions: Vector[(String, Node.NodeKey)],
      converted: Vector[FixedFeedInInput],
      workerPool: ActorRef[Worker.WorkerMessage],
      converter: ActorRef[Converter.ConverterMessage]
  ): Behaviors.Receive[ResConverterMessage] = Behaviors.receive {
    case (ctx, Converted(id, node, fixedFeedInInput)) =>
      val remainingConversions = activeConversions.filterNot(_ == (id, node))
      val updatedConverted = converted :+ fixedFeedInInput
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
      replyTo: ActorRef[Converter.ConverterMessage]
  ) extends ResConverterMessage

  final case class Converted(
      id: String,
      node: Node.NodeKey,
      fixedFeedInInput: FixedFeedInInput
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
    def apply(): Behaviors.Receive[WorkerMessage] = idle
    def idle: Behaviors.Receive[WorkerMessage] = Behaviors.receive {
      case (ctx, _) => Behaviors.same
    }

    sealed trait WorkerMessage
    final case class Convert(
        res: RES,
        node: NodeInput,
        profile: ResProfile,
        replyTo: ActorRef[ResConverterMessage]
    ) extends WorkerMessage

    /**
      * Convert a full set of renewable energy source system
      *
      * @param res      Input models to convert
      * @param nodes    Mapping from Simbench to power system data model node
      * @param profiles Collection of [[ResProfile]]s
      * @return A mapping from converted renewable energy source system to equivalent individual time series
      */
    def convert(
        res: Vector[RES],
        nodes: Map[Node, NodeInput],
        profiles: Map[ResProfileType, ResProfile]
    ): Map[FixedFeedInInput, IndividualTimeSeries[PValue]] =
      res.map { plant =>
        val node = NodeConverter.getNode(plant.node, nodes)
        val profile =
          PowerProfileConverter.getProfile(plant.profile, profiles)
        convert(plant, node, profile)
      }.toMap

    /**
      * Converts a single renewable energy source system to a fixed feed in model due to lacking information to
      * sophistically guess typical types of assets. Different voltage regulation strategies are not covered, yet.
      *
      * @param input   Input model
      * @param node    Node, the renewable energy source system is connected to
      * @param profile SimBench renewable energy source system profile
      * @param uuid    Option to a specific uuid
      * @return A pair of [[FixedFeedInInput]] and matching active power time series
      */
    def convert(
        input: RES,
        node: NodeInput,
        profile: ResProfile,
        uuid: Option[UUID] = None
    ): (FixedFeedInInput, IndividualTimeSeries[PValue]) = {
      val p = Quantities.getQuantity(input.p, MEGAWATT)
      val q = Quantities.getQuantity(input.q, MEGAVAR)
      val cosphi = cosPhi(p.getValue.doubleValue(), q.getValue.doubleValue())
      val varCharacteristicString =
        "cosPhiFixed:{(0.0,%#.2f)}".formatLocal(Locale.ENGLISH, cosphi)
      val sRated = Quantities.getQuantity(input.sR, MEGAVOLTAMPERE)

      /* Flip the sign, as infeed is negative in PowerSystemDataModel */
      val timeSeries = PowerProfileConverter.convert(profile, p.multiply(-1))

      new FixedFeedInInput(
        uuid.getOrElse(UUID.randomUUID()),
        input.id + "_" + input.resType.toString,
        OperatorInput.NO_OPERATOR_ASSIGNED,
        OperationTime.notLimited(),
        node,
        new CosPhiFixed(varCharacteristicString),
        sRated,
        cosphi
      ) -> timeSeries
    }
  }
}
