package edu.ie3.simbench.main

import edu.ie3.simbench.main.RunSimbench.readGridModel
import edu.ie3.test.common.{ConfigTestData, UnitSpec}
import edu.ie3.util.io.FileIOUtils
import org.scalatest.prop.TableDrivenPropertyChecks

import java.nio.file.Path

class SimbenchHelperSpec
    extends UnitSpec
    with ConfigTestData
    with TableDrivenPropertyChecks {
  "The SimbenchHelper" should {
    "read the specified grid model" in {
      val code = "1-LV-rural1--0-no_sw"

      val config = validIoInputConfigWithLocalFileSource.copy(folder =
        "testData/localSource/"
      )

      val gridModel = readGridModel(
        code,
        config
      )

      FileIOUtils.deleteRecursively(Path.of(config.folder, code))

      gridModel.simbenchCode shouldBe code
      gridModel.externalNets.size shouldBe 1
      gridModel.lines.size shouldBe 13
      gridModel.loads.size shouldBe 13
      gridModel.loadProfiles.size shouldBe 5
      gridModel.measurements.size shouldBe 0
      gridModel.nodes.size shouldBe 15
      gridModel.nodePFResults.size shouldBe 15
      gridModel.powerPlants.size shouldBe 0
      gridModel.powerPlantProfiles.size shouldBe 0
      gridModel.res.size shouldBe 4
      gridModel.resProfiles.size shouldBe 3
      gridModel.shunts.size shouldBe 0
      gridModel.storages.size shouldBe 0
      gridModel.storageProfiles.size shouldBe 0
      gridModel.studyCases.size shouldBe 6
      gridModel.substations.size shouldBe 0
      gridModel.switches.size shouldBe 0
      gridModel.transformers2w.size shouldBe 1
      gridModel.transformers3w.size shouldBe 0
    }
  }
}
