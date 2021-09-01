package edu.ie3.simbench.main

import akka.actor.typed.{ActorSystem, DispatcherSelector}
import edu.ie3.simbench.actor.Coordinator
import edu.ie3.simbench.actor.Coordinator.{CoordinatorMessage, Start}
import edu.ie3.simbench.config.{AkkaConfigUtil, ConfigValidator, SimbenchConfig}

/**
  * This is not meant to be final production code. It is more a place for "testing" the full method stack.
  */
object RunSimbench extends SimbenchHelper {
  def main(args: Array[String]): Unit = {
    printOpener()

    logger.info("Parsing the config")
    val (_, config) = prepareConfig(args)
    val simBenchConfig = SimbenchConfig(config)

    /* Validate the config */
    ConfigValidator.checkValidity(simBenchConfig)

    /* Adapt typesafe config to restrict resource utilization */
    val adaptedConfig =
      AkkaConfigUtil.adaptThreadPool(config, "restricted-core-pool-dispatcher")

    /* Start the actor system */
    val actorSystem = ActorSystem[CoordinatorMessage](
      Coordinator(),
      "Coordinator",
      adaptedConfig,
      DispatcherSelector.fromConfig("restricted-core-pool-dispatcher")
    )
    actorSystem ! Start(simBenchConfig)
  }
}
