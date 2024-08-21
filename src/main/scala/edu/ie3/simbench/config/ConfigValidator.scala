package edu.ie3.simbench.config

import edu.ie3.simbench.exception.{CodeValidationException, SimbenchException}
import edu.ie3.simbench.exception.io.SimbenchConfigException
import edu.ie3.simbench.model.SimbenchCode

import scala.util.{Failure, Success}

/** Provides functionality, to validate if a given [[SimbenchConfig]] has valid
  * content or not. Most parts of the checks (if all needed fields are apparent
  * or not) is automatically cone by the typesafe framework while parsing the
  * input data
  */
case object ConfigValidator {

  /** Checks the validity of the given [[SimbenchConfig]]. If any content is not
    * valid, a [[SimbenchConfigException]] is thrown
    * @param config
    *   The config to check
    * @throws SimbenchException
    *   If any of the content is not as it is expected
    */
  @throws[SimbenchException]
  def checkValidity(config: SimbenchConfig): Unit = {
    checkValidity(config.io)
  }

  /** Checks the validity of an IO-config part. If any content is not valid, a
    * [[SimbenchConfigException]] is thrown
    *
    * @param io
    *   The IO config to validate
    * @throws SimbenchException
    *   If any of the content is not as it is expected
    */
  @throws[SimbenchException]
  private def checkValidity(io: SimbenchConfig.Io): Unit = {
    checkSimbenchCodes(io.simbenchCodes)
    checkFileSource(io.input)
  }

  /** Checks the validity of the provided codes with the help of the permissible
    * ones in [[edu.ie3.simbench.model.SimbenchCode]]
    * @param codes
    *   The list of codes to check
    * @throws CodeValidationException
    *   if any of the code is not valid
    */
  @throws[CodeValidationException]
  private def checkSimbenchCodes(codes: List[java.lang.String]): Unit = {
    if (codes.isEmpty) {
      throw new SimbenchConfigException(s"No simbench codes were provided!")
    }

    for (code <- codes) {
      SimbenchCode.isValid(code) match {
        case Success(_)         =>
        case Failure(exception) => throw exception
      }
    }
  }

  @throws[SimbenchException]
  private def checkFileSource(cfg: SimbenchConfig.Io.Input): Unit = {
    val sources = Vector(
      cfg.localFile,
      cfg.download
    ).filter(_.isDefined)

    sources.size match {
      case 0 =>
        throw new SimbenchConfigException(s"No file sources defined in: $cfg")
      case 1 =>
      case n =>
        throw new SimbenchConfigException(
          s"Too many file sources ($n) defined in: $cfg"
        )
    }
  }
}
