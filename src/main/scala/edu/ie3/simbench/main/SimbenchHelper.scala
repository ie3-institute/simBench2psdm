/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.simbench.main

import com.typesafe.config.{ConfigFactory, Config => TypesafeConfig}
import com.typesafe.scalalogging.LazyLogging
import edu.ie3.simbench.config.ArgsParser
import edu.ie3.simbench.config.ArgsParser.Arguments
import edu.ie3.simbench.exception.io.SimbenchConfigException

trait SimbenchHelper extends LazyLogging {
  def prepareConfig(args: Array[String]): (Arguments, TypesafeConfig) = {

    val parsedArgs = ArgsParser.parse(args) match {
      case Some(pArgs) => pArgs
      case None =>
        System.exit(-1)
        throw new IllegalArgumentException(
          "Unable to parse provided Arguments."
        )
    }

    // ConfigConsistencyComparator.parseBeamTemplateConfFile(parsedArgs.configLocation.get) // todo implement

    // check if a config is provided
    val parsedArgsConfig = parsedArgs.config match {
      case None =>
        throw new SimbenchConfigException(
          "Please provide a valid config file via --config <path-to-config-file>."
        )
      case Some(parsedArgsConfig) => parsedArgsConfig
    }

    // set config file location as system property // todo do we need this? (same is valid for additional parsing below)
    System.setProperty(
      "configFileLocation",
      parsedArgs.configLocation.getOrElse("")
    )

    val configFromLocationPath =
      ConfigFactory.parseString(
        s"config=${parsedArgs.configLocation.getOrElse(throw new SimbenchConfigException("Cannot get config location from configuration!"))}"
      )

    // note: this overrides the default config values provided in the config file!
    // THE ORDER OF THE CALLS MATTERS -> the later the call, the more "fallback" -> first config is always the primary one!
    // hence if you add some more program arguments, you have to add them before(!) your default config!
    // see https://github.com/lightbend/config#merging-config-trees for details on merging configs
    val config = parsedArgsConfig
      .withFallback(configFromLocationPath)
      .resolve()

    (parsedArgs, config)
  }
}
