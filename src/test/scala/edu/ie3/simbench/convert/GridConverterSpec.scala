package edu.ie3.simbench.convert

import java.nio.file.Paths
import java.util
import edu.ie3.datamodel.models.UniqueEntity
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.connector.{LineInput, Transformer2WInput}
import edu.ie3.datamodel.models.input.system.{FixedFeedInInput, LoadInput}
import edu.ie3.simbench.convert.NodeConverter.AttributeOverride.JoinOverride
import edu.ie3.simbench.io.SimbenchReader
import edu.ie3.simbench.model.datamodel.{GridModel, Node, Switch}
import edu.ie3.test.common.{SwitchTestingData, UnitSpec}
import org.scalatest.Inside._

import scala.jdk.CollectionConverters._

class GridConverterSpec extends UnitSpec with SwitchTestingData {
  val simbenchReader: SimbenchReader = SimbenchReader(
    "1-LV-rural1--0-no_sw",
    Paths.get("src/test/resources/gridData/1-LV-rural1--0-no_sw")
  )
  val input: GridModel = simbenchReader.readGrid()

  "The grid converter" when {
    "joining nodes around closed switches" should {
      val determineJoinOverridesForSwitchGroup =
        PrivateMethod[Vector[JoinOverride]](
          Symbol("determineJoinOverridesForSwitchGroup")
        )
      "determine join overrides in a switch group of only one switch correctly" in {
        val actual =
          GridConverter invokePrivate determineJoinOverridesForSwitchGroup(
            switchGroup2,
            Vector.empty[Node.NodeKey]
          )
        actual.size shouldBe 1
        val nodeKeys = actual.flatMap { case JoinOverride(key, joinWith) =>
          Vector(key, joinWith)
        }
        nodeKeys.contains(nodeH.getKey) shouldBe true
        nodeKeys.contains(nodeI.getKey) shouldBe true
      }

      "determine join overrides in a switch group of two switches correctly" in {
        val actual =
          GridConverter invokePrivate determineJoinOverridesForSwitchGroup(
            switchGroup0,
            Vector.empty[Node.NodeKey]
          )
        actual.size shouldBe 2

        val targets = actual.map(_.joinWith).distinct
        targets.size shouldBe 1
        targets.head shouldBe nodeB.getKey
      }

      "determine join overrides in a switch group of three switches with central node correctly" in {
        val actual =
          GridConverter invokePrivate determineJoinOverridesForSwitchGroup(
            switchGroup1,
            Vector.empty[Node.NodeKey]
          )
        actual.size shouldBe 3

        val targets = actual.map(_.joinWith).distinct
        targets.size shouldBe 1
        targets.head shouldBe nodeD.getKey
      }

      "determine join overrides in a switch group of two switches correctly, if slack node keys are given" in {
        val actual =
          GridConverter invokePrivate determineJoinOverridesForSwitchGroup(
            switchGroup0,
            Vector(nodeC.getKey)
          )
        actual.size shouldBe 2

        val targets = actual.map(_.joinWith).distinct
        targets.size shouldBe 1
        targets.head shouldBe nodeC.getKey
      }

      val determineSwitchGroups =
        PrivateMethod[Vector[Vector[Switch]]](Symbol("determineSwitchGroups"))
      "determine switch groups correctly" in {
        val actual = GridConverter invokePrivate determineSwitchGroups(
          switches,
          Vector.empty[Vector[Switch]]
        )

        /* We expect three groups */
        actual.size shouldBe 3
        /* We expect all switches to be covered */
        actual.flatten.distinct.size shouldBe 6

        actual.sortBy(_.size).foreach { group =>
          if (group.contains(switchAB)) {
            group.contains(switchBC) shouldBe true
          } else if (group.contains(switchDE)) {
            group.contains(switchDF) shouldBe true
            group.contains(switchDG) shouldBe true
          } else {
            group.contains(switchHI) shouldBe true
          }
        }
      }

      val determineJoinOverrides =
        PrivateMethod[Vector[JoinOverride]](Symbol("determineJoinOverrides"))
      "determine join overrides for the whole grid correctly" in {
        val actual = GridConverter invokePrivate determineJoinOverrides(
          switches,
          Vector(nodeC.getKey)
        )

        val joinMap = actual.map { case JoinOverride(key, joinWith) =>
          key -> joinWith
        }.toMap

        joinMap.size shouldBe 6
        joinMap.values.toSeq.distinct.size shouldBe 3

        val expected = Map(
          nodeA.getKey -> nodeC.getKey,
          nodeB.getKey -> nodeC.getKey,
          nodeE.getKey -> nodeD.getKey,
          nodeF.getKey -> nodeD.getKey,
          nodeG.getKey -> nodeD.getKey,
          nodeH.getKey -> nodeI.getKey,
          nodeI.getKey -> nodeH.getKey
        )

        joinMap.foreach { case key -> joinWith =>
          expected.get(key) match {
            case Some(expectedJoin) =>
              joinWith shouldBe expectedJoin
            case None => fail(s"No mapping expected for node key '$key'")
          }
        }
      }
    }

    "converting a full data set" should {
      "bring the correct amount of converted models" in {
        val actual = GridConverter.convert(
          "1-LV-rural1--0-no_sw",
          input,
          removeSwitches = false
        )
        inside(actual) {
          case (
                gridContainer,
                timeSeries,
                timeSeriesMapping,
                powerFlowResults
              ) =>
            /* Evaluate the correctness of the container by counting the occurrence of models (the correct conversion is
             * tested in separate unit tests */
            gridContainer.getGridName shouldBe "1-LV-rural1--0-no_sw"
            countClassOccurrences(
              gridContainer.getRawGrid.allEntitiesAsList()
            ) shouldBe Map(
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
            countClassOccurrences(
              gridContainer.getGraphics.allEntitiesAsList()
            ) shouldBe Map
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
            timeSeriesMapping.exists(entry =>
              !participantUuids.contains(entry.participant())
            ) shouldBe false

            /* Evaluate the amount of converted power flow results */
            powerFlowResults.size shouldBe 15
        }
      }
    }
  }

  def countClassOccurrences(
      entities: util.List[_ <: UniqueEntity]
  ): Map[Class[_ <: UniqueEntity], Int] =
    entities.asScala
      .groupBy(_.getClass)
      .map(classToOccurrences =>
        classToOccurrences._1 -> classToOccurrences._2.size
      )
      .toSeq
      .sortBy(_._1.getName)
      .toMap
}
