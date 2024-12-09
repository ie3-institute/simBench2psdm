// generated by tscfg 1.0.0 on Thu Aug 08 13:29:09 CEST 2024
// source: src/main/resources/config-template.conf

package edu.ie3.simbench.config

final case class SimbenchConfig(
    conversion: SimbenchConfig.Conversion,
    io: SimbenchConfig.Io
)
object SimbenchConfig {
  final case class CsvConfig(
      directoryHierarchy: scala.Boolean,
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
        directoryHierarchy =
          c.hasPathOrNull("directoryHierarchy") && c.getBoolean(
            "directoryHierarchy"
          ),
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

  final case class Conversion(
      removeSwitches: scala.Boolean
  )
  object Conversion {
    def apply(
        c: com.typesafe.config.Config,
        parentPath: java.lang.String,
        $tsCfgValidator: $TsCfgValidator
    ): SimbenchConfig.Conversion = {
      SimbenchConfig.Conversion(
        removeSwitches =
          c.hasPathOrNull("removeSwitches") && c.getBoolean("removeSwitches")
      )
    }
  }

  final case class Io(
      input: SimbenchConfig.Io.Input,
      output: SimbenchConfig.Io.Output,
      simbenchCodes: scala.List[java.lang.String]
  )
  object Io {
    final case class Input(
        csv: SimbenchConfig.CsvConfig,
        download: scala.Option[SimbenchConfig.Io.Input.Download],
        folder: java.lang.String,
        localFile: scala.Option[SimbenchConfig.Io.Input.LocalFile]
    )
    object Input {
      final case class Download(
          baseUrl: java.lang.String,
          failOnExistingFiles: scala.Boolean
      )
      object Download {
        def apply(
            c: com.typesafe.config.Config,
            parentPath: java.lang.String,
            $tsCfgValidator: $TsCfgValidator
        ): SimbenchConfig.Io.Input.Download = {
          SimbenchConfig.Io.Input.Download(
            baseUrl =
              if (c.hasPathOrNull("baseUrl")) c.getString("baseUrl")
              else "https://daks.uni-kassel.de/bitstreams",
            failOnExistingFiles =
              c.hasPathOrNull("failOnExistingFiles") && c.getBoolean(
                "failOnExistingFiles"
              )
          )
        }
      }

      final case class LocalFile(
          failOnExistingFiles: scala.Boolean,
          isZipped: scala.Boolean
      )
      object LocalFile {
        def apply(
            c: com.typesafe.config.Config,
            parentPath: java.lang.String,
            $tsCfgValidator: $TsCfgValidator
        ): SimbenchConfig.Io.Input.LocalFile = {
          SimbenchConfig.Io.Input.LocalFile(
            failOnExistingFiles =
              c.hasPathOrNull("failOnExistingFiles") && c.getBoolean(
                "failOnExistingFiles"
              ),
            isZipped = !c.hasPathOrNull("isZipped") || c.getBoolean("isZipped")
          )
        }
      }

      def apply(
          c: com.typesafe.config.Config,
          parentPath: java.lang.String,
          $tsCfgValidator: $TsCfgValidator
      ): SimbenchConfig.Io.Input = {
        SimbenchConfig.Io.Input(
          csv = SimbenchConfig.CsvConfig(
            if (c.hasPathOrNull("csv")) c.getConfig("csv")
            else com.typesafe.config.ConfigFactory.parseString("csv{}"),
            parentPath + "csv.",
            $tsCfgValidator
          ),
          download =
            if (c.hasPathOrNull("download"))
              scala.Some(
                SimbenchConfig.Io.Input.Download(
                  c.getConfig("download"),
                  parentPath + "download.",
                  $tsCfgValidator
                )
              )
            else None,
          folder =
            if (c.hasPathOrNull("folder")) c.getString("folder")
            else "input/download/",
          localFile =
            if (c.hasPathOrNull("localFile"))
              scala.Some(
                SimbenchConfig.Io.Input.LocalFile(
                  c.getConfig("localFile"),
                  parentPath + "localFile.",
                  $tsCfgValidator
                )
              )
            else None
        )
      }
    }

    final case class Output(
        compress: scala.Boolean,
        csv: SimbenchConfig.CsvConfig,
        targetDir: java.lang.String
    )
    object Output {
      def apply(
          c: com.typesafe.config.Config,
          parentPath: java.lang.String,
          $tsCfgValidator: $TsCfgValidator
      ): SimbenchConfig.Io.Output = {
        SimbenchConfig.Io.Output(
          compress = !c.hasPathOrNull("compress") || c.getBoolean("compress"),
          csv = SimbenchConfig.CsvConfig(
            if (c.hasPathOrNull("csv")) c.getConfig("csv")
            else com.typesafe.config.ConfigFactory.parseString("csv{}"),
            parentPath + "csv.",
            $tsCfgValidator
          ),
          targetDir =
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
      conversion = SimbenchConfig.Conversion(
        if (c.hasPathOrNull("conversion")) c.getConfig("conversion")
        else com.typesafe.config.ConfigFactory.parseString("conversion{}"),
        parentPath + "conversion.",
        $tsCfgValidator
      ),
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

  final class $TsCfgValidator {
    private val badPaths =
      scala.collection.mutable.ArrayBuffer[java.lang.String]()

    def addBadPath(
        path: java.lang.String,
        e: com.typesafe.config.ConfigException
    ): Unit = {
      badPaths += s"'$path': ${e.getClass.getName}(${e.getMessage})"
    }

    def addInvalidEnumValue(
        path: java.lang.String,
        value: java.lang.String,
        enumName: java.lang.String
    ): Unit = {
      badPaths += s"'$path': invalid value $value for enumeration $enumName"
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
