/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.simbench.config

final case class SimbenchConfig(
    io: SimbenchConfig.Io
)
object SimbenchConfig {
  final case class CsvConfig(
      fileEncoding: java.lang.String,
      fileEnding: java.lang.String,
      separator: java.lang.String
  )
  object CsvConfig {
    def apply(
        c: com.typesafe.config.Config,
        parentPath: java.lang.String,
        $tsCfgValidator: $TsCfgValidator
    ): SimbenchConfig.CsvConfig = {
      SimbenchConfig.CsvConfig(
        fileEncoding =
          if (c.hasPathOrNull("fileEncoding")) c.getString("fileEncoding")
          else "UTF-8",
        fileEnding =
          if (c.hasPathOrNull("fileEnding")) c.getString("fileEnding")
          else ".csv",
        separator =
          if (c.hasPathOrNull("separator")) c.getString("separator") else ";"
      )
    }
  }

  final case class Io(
      baseUrl: java.lang.String,
      downloadFolder: java.lang.String,
      input: SimbenchConfig.Io.Input,
      output: SimbenchConfig.Io.Output,
      simbenchCodes: scala.List[java.lang.String]
  )
  object Io {
    final case class Input(
        csv: CsvConfig
    )
    object Input {
      def apply(
          c: com.typesafe.config.Config,
          parentPath: java.lang.String,
          $tsCfgValidator: $TsCfgValidator
      ): SimbenchConfig.Io.Input = {
        SimbenchConfig.Io.Input(
          csv = CsvConfig(
            if (c.hasPathOrNull("csv")) c.getConfig("csv")
            else com.typesafe.config.ConfigFactory.parseString("csv{}"),
            parentPath + "csv.",
            $tsCfgValidator
          )
        )
      }
    }

    final case class Output(
        csv: CsvConfig,
        targetFolder: java.lang.String
    )
    object Output {
      def apply(
          c: com.typesafe.config.Config,
          parentPath: java.lang.String,
          $tsCfgValidator: $TsCfgValidator
      ): SimbenchConfig.Io.Output = {
        SimbenchConfig.Io.Output(
          csv = CsvConfig(
            if (c.hasPathOrNull("csv")) c.getConfig("csv")
            else com.typesafe.config.ConfigFactory.parseString("csv{}"),
            parentPath + "csv.",
            $tsCfgValidator
          ),
          targetFolder =
            if (c.hasPathOrNull("targetFolder")) c.getString("targetFolder")
            else "convertedData"
        )
      }
    }

    def apply(
        c: com.typesafe.config.Config,
        parentPath: java.lang.String,
        $tsCfgValidator: $TsCfgValidator
    ): SimbenchConfig.Io = {
      SimbenchConfig.Io(
        baseUrl =
          if (c.hasPathOrNull("baseUrl")) c.getString("baseUrl")
          else "http://141.51.193.167/simbench/gui/usecase/download",
        downloadFolder =
          if (c.hasPathOrNull("downloadFolder")) c.getString("downloadFolder")
          else "inputData/download/",
        input = SimbenchConfig.Io.Input(
          if (c.hasPathOrNull("input")) c.getConfig("input")
          else com.typesafe.config.ConfigFactory.parseString("input{}"),
          parentPath + "input.",
          $tsCfgValidator
        ),
        output = SimbenchConfig.Io.Output(
          if (c.hasPathOrNull("output")) c.getConfig("output")
          else com.typesafe.config.ConfigFactory.parseString("output{}"),
          parentPath + "output.",
          $tsCfgValidator
        ),
        simbenchCodes =
          $_L$_str(c.getList("simbenchCodes"), parentPath, $tsCfgValidator)
      )
    }
  }

  def apply(c: com.typesafe.config.Config): SimbenchConfig = {
    val $tsCfgValidator: $TsCfgValidator = new $TsCfgValidator()
    val parentPath: java.lang.String = ""
    val $result = SimbenchConfig(
      io = SimbenchConfig.Io(
        if (c.hasPathOrNull("io")) c.getConfig("io")
        else com.typesafe.config.ConfigFactory.parseString("io{}"),
        parentPath + "io.",
        $tsCfgValidator
      )
    )
    $tsCfgValidator.validate()
    $result
  }

  private def $_L$_str(
      cl: com.typesafe.config.ConfigList,
      parentPath: java.lang.String,
      $tsCfgValidator: $TsCfgValidator
  ): scala.List[java.lang.String] = {
    import scala.jdk.CollectionConverters._
    cl.asScala.map(cv => $_str(cv)).toList
  }
  private def $_expE(
      cv: com.typesafe.config.ConfigValue,
      exp: java.lang.String
  ) = {
    val u: Any = cv.unwrapped
    new java.lang.RuntimeException(
      s"${cv.origin.lineNumber}: " +
        "expecting: " + exp + " got: " +
        (if (u.isInstanceOf[java.lang.String]) "\"" + u + "\"" else u)
    )
  }

  private def $_str(cv: com.typesafe.config.ConfigValue): java.lang.String = {
    java.lang.String.valueOf(cv.unwrapped())
  }

  private final class $TsCfgValidator {
    private val badPaths =
      scala.collection.mutable.ArrayBuffer[java.lang.String]()

    def addBadPath(
        path: java.lang.String,
        e: com.typesafe.config.ConfigException
    ): Unit = {
      badPaths += s"'$path': ${e.getClass.getName}(${e.getMessage})"
    }

    def validate(): Unit = {
      if (badPaths.nonEmpty) {
        throw new com.typesafe.config.ConfigException(
          badPaths.mkString("Invalid configuration:\n    ", "\n    ", "")
        ) {}
      }
    }
  }
}
