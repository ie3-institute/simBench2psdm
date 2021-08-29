package edu.ie3.simbench.convert

import akka.actor.testkit.typed.scaladsl.ActorTestKit

import java.nio.file.Paths
import java.util
import edu.ie3.datamodel.models.UniqueEntity
import edu.ie3.simbench.actor.GridConverter.ConvertGridStructure
import edu.ie3.simbench.actor.{Converter, GridConverter}
import edu.ie3.simbench.convert.NodeConverter.AttributeOverride.JoinOverride
import edu.ie3.simbench.io.SimbenchReader
import edu.ie3.simbench.model.datamodel.{GridModel, Node, Switch}
import edu.ie3.test.common.{SwitchTestingData, UnitSpec}
import org.scalatest.BeforeAndAfterAll

import scala.concurrent.duration.{Duration, FiniteDuration}
import scala.jdk.CollectionConverters._

class GridConverterSpec
    extends UnitSpec
    with BeforeAndAfterAll
    with SwitchTestingData {
  val akkaTestKit: ActorTestKit = ActorTestKit()

  override protected def afterAll(): Unit = {
    akkaTestKit.shutdownTestKit()
    super.afterAll()
  }

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
        val actual = GridConverter invokePrivate determineJoinOverridesForSwitchGroup(
          switchGroup2,
          Vector.empty[Node.NodeKey]
        )
        actual.size shouldBe 1
        val nodeKeys = actual.flatMap {
          case JoinOverride(key, joinWith) => Vector(key, joinWith)
        }
        nodeKeys.contains(nodeH.getKey) shouldBe true
        nodeKeys.contains(nodeI.getKey) shouldBe true
      }

      "determine join overrides in a switch group of two switches correctly" in {
        val actual = GridConverter invokePrivate determineJoinOverridesForSwitchGroup(
          switchGroup0,
          Vector.empty[Node.NodeKey]
        )
        actual.size shouldBe 2

        val targets = actual.map(_.joinWith).distinct
        targets.size shouldBe 1
        targets.head shouldBe nodeB.getKey
      }

      "determine join overrides in a switch group of three switches with central node correctly" in {
        val actual = GridConverter invokePrivate determineJoinOverridesForSwitchGroup(
          switchGroup1,
          Vector.empty[Node.NodeKey]
        )
        actual.size shouldBe 3

        val targets = actual.map(_.joinWith).distinct
        targets.size shouldBe 1
        targets.head shouldBe nodeD.getKey
      }

      "determine join overrides in a switch group of two switches correctly, if slack node keys are given" in {
        val actual = GridConverter invokePrivate determineJoinOverridesForSwitchGroup(
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

        val joinMap = actual.map {
          case JoinOverride(key, joinWith) => key -> joinWith
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

        joinMap.foreach {
          case key -> joinWith =>
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
        /* Set up the actors */
        val gridConverter = akkaTestKit.spawn(GridConverter(), "gridConverter")
        val converter =
          akkaTestKit.createTestProbe[Converter.ConverterMessage]("converter")

        /* Issue the conversion */
        gridConverter ! ConvertGridStructure(
          "1-LV-rural1--0-no_sw",
          input.nodes,
          input.nodePFResults,
          input.externalNets,
          input.powerPlants,
          input.res,
          input.transformers2w,
          input.transformers3w,
          input.lines,
          input.switches,
          input.measurements,
          removeSwitches = false,
          converter.ref
        )

        converter.expectMessageType[Converter.GridStructureConverted](
          FiniteDuration(60, "s")
        ) match {
          case Converter.GridStructureConverted(
              nodeConversion,
              nodeResults,
              lines,
              transformers2w,
              transformers3w,
              switches,
              measurements
              ) =>
            nodeConversion.size shouldBe 15
            nodeResults.size shouldBe 15
            lines.size shouldBe 13
            transformers2w.size shouldBe 1
            transformers3w.size shouldBe 0
            switches.size shouldBe 0
            measurements.size shouldBe 0
        }
      }
      /* TODO: Test amount of converted participants
     *  4 x FixedFeedInInput
     *  13 x LoadInput
     *  17 x Time Series and Mapping
     *  All participants have a corresponding time series
     */
    }
  }
}
