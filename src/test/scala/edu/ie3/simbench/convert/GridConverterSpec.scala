package edu.ie3.simbench.convert

import java.nio.file.Paths
import java.util

import edu.ie3.datamodel.models.UniqueEntity
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.connector.{LineInput, Transformer2WInput}
import edu.ie3.datamodel.models.input.system.{FixedFeedInInput, LoadInput}
import edu.ie3.simbench.io.SimbenchReader
import edu.ie3.simbench.model.datamodel.GridModel
import edu.ie3.test.common.UnitSpec
import org.scalatest.Inside._

import scala.jdk.CollectionConverters._

class GridConverterSpec extends UnitSpec {
  val simbenchReader: SimbenchReader = SimbenchReader(
    "1-LV-rural1--0-no_sw",
    Paths.get("src/test/resources/gridData/1-LV-rural1--0-no_sw")
  )
  val input: GridModel = simbenchReader.readGrid()

  "The grid converter" should {
    "bring the correct amount of converted models" in {
      val actual = GridConverter.convert("1-LV-rural1--0-no_sw", input, removeSwitches = false)
      inside(actual) {
        case (gridContainer, timeSeries, timeSeriesMapping, powerFlowResults) =>
          /* Evaluate the correctness of the container by counting the occurrence of models (the correct conversion is
           * tested in separate unit tests */
          gridContainer.getGridName shouldBe "1-LV-rural1--0-no_sw"
          countClassOccurrences(gridContainer.getRawGrid.allEntitiesAsList()) shouldBe Map(
            classOf[NodeInput] -> 15,
            classOf[LineInput] -> 13,
            classOf[Transformer2WInput] -> 1
          )
          countClassOccurrences(
            gridContainer.getSystemParticipants.allEntitiesAsList()
          ) shouldBe Map(
            classOf[FixedFeedInInput] -> 4,
            classOf[LoadInput] -> 13
          )
          countClassOccurrences(gridContainer.getGraphics.allEntitiesAsList()) shouldBe Map
            .empty[Class[_ <: UniqueEntity], Int]

          /* Evaluate the correctness of the time series by counting the occurrence of models */
          timeSeries.size shouldBe 17

          /* Evaluate the existence of time series mappings for all participants */
          timeSeriesMapping.size shouldBe 17
          val participantUuids = gridContainer.getSystemParticipants
            .allEntitiesAsList()
            .asScala
            .map(_.getUuid)
            .toVector
          /* There is no participant uuid in mapping, that is not among participants */
          timeSeriesMapping.exists(
            entry => !participantUuids.contains(entry.getParticipant)
          ) shouldBe false

          /* Evaluate the amount of converted power flow results */
          powerFlowResults.size shouldBe 15
      }
    }
  }

  def countClassOccurrences(
      entities: util.List[_ <: UniqueEntity]
  ): Map[Class[_ <: UniqueEntity], Int] =
    entities.asScala
      .groupBy(_.getClass)
      .map(
        classToOccurrences =>
          classToOccurrences._1 -> classToOccurrences._2.size
      )
      .toSeq
      .sortBy(_._1.getName)
      .toMap
}
