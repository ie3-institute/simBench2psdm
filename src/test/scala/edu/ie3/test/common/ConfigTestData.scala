package edu.ie3.test.common

import edu.ie3.simbench.config
import edu.ie3.simbench.config.SimbenchConfig
import edu.ie3.simbench.config.SimbenchConfig.Io.Input

trait ConfigTestData {
  val validIoInputConfig = new SimbenchConfig.Io.Input(
    new SimbenchConfig.CsvConfig("UTF-8", ".csv", ";"),
    new Input.Download(false, "testData/download/")
  )
  val validIoOutputConfig = new SimbenchConfig.Io.Output(
    new config.SimbenchConfig.CsvConfig("UTF-8", ".csv", ";"),
    "convertedData"
  )

  val validIo = new SimbenchConfig.Io(
    "http://141.51.193.167/simbench/gui/usecase/download",
    "testData/download/",
    validIoInputConfig,
    validIoOutputConfig,
    List("1-LV-urban6--0-sw", "1-LV-urban6--2-sw", "1-EHVHV-mixed-2-0-sw")
  )

  val ioWithInvalidSimbenchCode = new SimbenchConfig.Io(
    "http://141.51.193.167/simbench/gui/usecase/download",
    "testData/download/",
    validIoInputConfig,
    validIoOutputConfig,
    List("1-LV-urban6--0-sw", "blabla", "1-EHVHV-mixed-2-0-sw")
  )

  val validConfig = new SimbenchConfig(validIo)
}
