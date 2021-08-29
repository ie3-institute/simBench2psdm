package edu.ie3.simbench.actor

import akka.actor.typed.ActorRef
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.simbench.model.datamodel.ShuntModel
import edu.ie3.simbench.model.datamodel.profiles.ProfileModel

import java.util.UUID

sealed trait WorkerMessage
object WorkerMessage {
  final case class Init(mutator: ActorRef[Mutator.MutatorMessage])
      extends WorkerMessage

  abstract protected[actor] class Convert[M <: ShuntModel, P <: ProfileModel[
    _,
    _
  ], R]
      extends WorkerMessage {
    val model: M
    val node: NodeInput
    val profile: P
    val replyTo: ActorRef[R]
  }

  final case class TimeSeriesPersisted(uuid: UUID) extends WorkerMessage
}
