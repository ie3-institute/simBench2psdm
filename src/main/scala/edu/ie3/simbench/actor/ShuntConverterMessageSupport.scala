package edu.ie3.simbench.actor

import akka.actor.typed.ActorRef
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.system.SystemParticipantInput
import edu.ie3.simbench.model.datamodel.{Node, ShuntModel}
import edu.ie3.simbench.model.datamodel.profiles.ProfileModel

import java.util.UUID

trait ShuntConverterMessageSupport[I <: ShuntModel, P <: ProfileModel[_, _], R <: SystemParticipantInput] {
  sealed trait ShuntConverterMessage

  protected[actor] abstract class Init extends ShuntConverterMessage {
    val simBenchCode: String
    val amountOfWorkers: Int
    val profiles: Vector[P]
    val mutator: ActorRef[Mutator.MutatorMessage]
    val replyTo: ActorRef[Converter.ConverterMessage]
  }

  protected[actor] abstract class Converted extends ShuntConverterMessage {
    val id: String
    val node: Node.NodeKey
    val model: R
    val timeSeriesUuid: UUID
  }

  /**
    * Request to convert all given models
    */
  protected[actor] abstract class Convert extends ShuntConverterMessage {
    val simBenchCode: String
    val inputs: Vector[I]
    val nodes: Map[Node, NodeInput]
    val replyTo: ActorRef[Converter.ConverterMessage]
  }
}
