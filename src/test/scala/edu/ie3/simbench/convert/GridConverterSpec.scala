package edu.ie3.simbench.convert

import java.nio.file.Path
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
    Path.of("src/test/resources/gridData/1-LV-rural1--0-no_sw")
  )
  val input: GridModel = simbenchReader.readGrid()

  "The grid converter" should {
    "bring the correct amount of converted models" in {
      val actual = GridConverter.convert("1-LV-rural1--0-no_sw", input)
      inside(actual) {
        case (gridContainer, timeSeries) =>
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
      }
    }
  }

  def countClassOccurrences(
      entities: util.List[UniqueEntity]
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
