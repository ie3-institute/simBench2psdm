package edu.ie3.simbench.main

import edu.ie3.simbench.main.RunSimbench.getGridPath
import edu.ie3.test.common.{ConfigTestData, UnitSpec}
import edu.ie3.util.io.FileIOUtils
import org.scalatest.prop.TableDrivenPropertyChecks

import java.nio.file.Path

class SimbenchHelperSpec
    extends UnitSpec
    with ConfigTestData
    with TableDrivenPropertyChecks {
  "The SimbenchHelper" should {
    "return the specified grid path correctly" in {
      val code = "1-LV-rural1--0-no_sw"

      val config = validIoInputConfigWithLocalFileSource.copy(directory =
        "testData/localSource/"
      )

      getGridPath(
        code,
        config
      ) shouldBe Path.of("testData", "localSource", code)

      FileIOUtils.deleteRecursively(Path.of(config.directory, code))
    }
  }
}
