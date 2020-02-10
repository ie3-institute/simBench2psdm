package edu.ie3.simbench.main

import com.typesafe.scalalogging.LazyLogging
import edu.ie3.simbench.config.ArgsParser
import edu.ie3.simbench.config.ArgsParser.Arguments
import com.typesafe.config.{ConfigFactory, Config => TypesafeConfig}
import edu.ie3.simbench.exception.io.SimbenchConfigException

trait SimbenchHelper extends LazyLogging {
  def prepareConfig(args: Array[String]): (Arguments, TypesafeConfig) = {

    val parsedArgs = ArgsParser.parse(args) match {
      case Some(pArgs) => pArgs
      case None =>
        System.exit(-1)
        throw new IllegalArgumentException(
          "Unable to parse provided Arguments.")
    }

    // ConfigConsistencyComparator.parseBeamTemplateConfFile(parsedArgs.configLocation.get) // todo implement

    // check if a config is provided
    val parsedArgsConfig = parsedArgs.config match {
      case None =>
        throw new SimbenchConfigException(
          "Please provide a valid config file via --config <path-to-config-file>.")
      case Some(parsedArgsConfig) => parsedArgsConfig
    }

    // set config file location as system property // todo do we need this? (same is valid for additional parsing below)
    System.setProperty("configFileLocation",
                       parsedArgs.configLocation.getOrElse(""))

    val configFromLocationPath =
      ConfigFactory.parseString(
        s"config=${parsedArgs.configLocation.getOrElse(throw new SimbenchConfigException("Cannot get config location from configuration!"))}")

    // note: this overrides the default config values provided in the config file!
    // THE ORDER OF THE CALLS MATTERS -> the later the call, the more "fallback" -> first config is always the primary one!
    // hence if you add some more program arguments, you have to add them before(!) your default config!
    // see https://github.com/lightbend/config#merging-config-trees for details on merging configs
    val config = parsedArgsConfig
      .withFallback(configFromLocationPath)
      .resolve()

    (parsedArgs, config)
  }

  def printOpener(): Unit = {
    println(
      "   _____ _           ____                  _    _  _   _____ _____ __  __  ____  _   _")
    println(
      "  / ____(_)         |  _ \\                | |  | || | / ____|_   _|  \\/  |/ __ \\| \\ | |   /")
    println(
      "  | (___  _ _ __ ___ | |_) | ___ _ __   ___| |__| || || (___   | | | \\  / | |  | |  \\| |  /  \\")
    println(
      "  \\___ \\| | '_ ` _ \\|  _ < / _ \\ '_ \\ / __| '_ \\__   _\\___ \\  | | | |\\/| | |  | | . ` | / /\\ \\")
    println(
      "  ____) | | | | | | | |_) |  __/ | | | (__| | | | | | ____) |_| |_| |  | | |__| | |\\  |/ ____ \\")
    println(
      "  |_____/|_|_| |_| |_|____/ \\___|_| |_|\\___|_| |_| |_||_____/|_____|_|  |_|\\____/|_| \\_/_/    \\_\\")
  }
}
