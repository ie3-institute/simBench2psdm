package edu.ie3.test.common

import edu.ie3.simbench.config.SimbenchConfig

trait ConfigTestData {
  val validCsv = new SimbenchConfig.Io.Csv("UTF-8", ".csv", ";")

  val validIo = new SimbenchConfig.Io(
    "http://141.51.193.167/simbench/gui/usecase/download",
    validCsv,
    "testData/download/",
    List("1-LV-urban6--0-sw", "1-LV-urban6--2-sw", "1-EHVHV-mixed-2-0-sw")
  )

  val ioWithInvalidSimbenchCode = new SimbenchConfig.Io(
    "http://141.51.193.167/simbench/gui/usecase/download",
    validCsv,
    "testData/download/",
    List("1-LV-urban6--0-sw", "blabla", "1-EHVHV-mixed-2-0-sw")
  )

  val validConfig = new SimbenchConfig(validIo)
}
