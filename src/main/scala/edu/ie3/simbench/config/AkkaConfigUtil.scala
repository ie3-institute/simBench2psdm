package edu.ie3.simbench.config

import com.typesafe.config.{Config, ConfigException, ConfigFactory}
import com.typesafe.scalalogging.LazyLogging

import scala.math.ceil

object AkkaConfigUtil extends LazyLogging {
  def adaptThreadPool(config: Config, path: String): Config =
    if (config.hasPath(path)) {
      /* Check if the thread pool is as expected */
      logThreadPoolConfig(config, path)
      config
    } else {
      logger.info("No thread pool config given. Set default one.")
      val adaptedConfig = config.withFallback(defaultThreadPoolConfig(path))
      logThreadPoolConfig(adaptedConfig, path)
      adaptedConfig
    }

  /**
    * Logs the given thread pool config in case it is restricting the usage of cores. If the config is not of that
    * shape, the user is asked to revise, if the provided config actually is correct.
    *
    * @param config The config to extract information from
    * @param path   The path, at which the thread pool config starts
    */
  private def logThreadPoolConfig(config: Config, path: String): Unit =
    try {
      val minCores = config.getInt(
        s"$path.thread-pool-executor.core-pool-size-min"
      )
      val maxCores = config.getInt(
        s"$path.thread-pool-executor.core-pool-size-max"
      )
      val coreFactor = config.getDouble(
        s"$path.thread-pool-executor.core-pool-size-factor"
      )
      logger.info(
        "You configured akka with the following parameters:\n\tMinimum amount of cores: {}" +
          "\n\tMaximum amount of cores: {}\n\tMaximum amount of threads: {}",
        minCores,
        maxCores,
        ceil(maxCores * coreFactor).toInt
      )
    } catch {
      case _: ConfigException.Missing =>
        logger.warn(
          "The config has not the expected entries. Assuming you know what you did. If you're in doubt, " +
            "please double check."
        )
    }

  /**
    * Create the default thread pool config. It limits the amount of utilized cores.
    *
    * @param path                     The target path of the thread pool config
    * @param targetPortionOfUsedCores Targeted portion of used cores. E.g. if you have 10 cores and set this value to
    *                                 0.8, at max, 10 cores are utilized
    * @return The default thread pool config
    */
  private def defaultThreadPoolConfig(
      path: String,
      targetPortionOfUsedCores: Double = 0.8
  ) = {
    val availableProcessors = Runtime.getRuntime.availableProcessors()
    val minCoreSize = scala.math.min(availableProcessors, 2)
    val maxCoreSize = ceil(availableProcessors * targetPortionOfUsedCores).toInt
    ConfigFactory.parseString(
      s"""
         | $path = {
         |  type = Dispatcher
         |  executor = "thread-pool-executor"
         |  thread-pool-executor {
         |    core-pool-size-min = $minCoreSize
         |    core-pool-size-factor = 2.0
         |    core-pool-size-max = $maxCoreSize
         |  }
         |  throughput = 1
         |}""".stripMargin
    )
  }
}
