package edu.ie3.test.common

import edu.ie3.simbench.config
import edu.ie3.simbench.config.SimbenchConfig
import edu.ie3.simbench.config.SimbenchConfig.Conversion
import edu.ie3.simbench.config.SimbenchConfig.Io.Input

trait ConfigTestData {
  val validIoInputConfig = new SimbenchConfig.Io.Input(
    new SimbenchConfig.CsvConfig(false, "UTF-8", ".csv", ";"),
    new Input.Download(
      "http://141.51.193.167/simbench/gui/usecase/download",
      false,
      "testData/download/"
    )
  )
  val validIoOutputConfig = new SimbenchConfig.Io.Output(
    false,
    new config.SimbenchConfig.CsvConfig(false, "UTF-8", ".csv", ";"),
    "convertedData"
  )

  val validIo = new SimbenchConfig.Io(
    validIoInputConfig,
    validIoOutputConfig,
    List("1-LV-urban6--0-sw", "1-LV-urban6--2-sw", "1-EHVHV-mixed-2-0-sw")
  )

  val ioWithInvalidSimbenchCode = new SimbenchConfig.Io(
    validIoInputConfig,
    validIoOutputConfig,
    List("1-LV-urban6--0-sw", "blabla", "1-EHVHV-mixed-2-0-sw")
  )

  val validConversionConfig: Conversion =
    Conversion(removeSwitches = false, participantWorkersPerType = 20)

  val validConfig = new SimbenchConfig(validConversionConfig, validIo)
}
