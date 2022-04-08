package edu.ie3.simbench.convert

import akka.actor.testkit.typed.scaladsl.{ActorTestKit, TestProbe}
import akka.actor.typed.ActorRef
import edu.ie3.simbench.actor.Mutator.MutatorMessage
import edu.ie3.simbench.actor.{Converter, Coordinator, Mutator, WorkerMessage}
import edu.ie3.test.common.UnitSpec
import org.scalatest.BeforeAndAfterAll

import scala.concurrent.duration.FiniteDuration

class ConverterWithoutTimeSeriesSpec extends UnitSpec with BeforeAndAfterAll {
  val akkaTestKit: ActorTestKit = ActorTestKit()

  override protected def afterAll(): Unit = {
    akkaTestKit.shutdownTestKit()
    super.afterAll()
  }

  val coordinator: TestProbe[Coordinator.CoordinatorMessage] =
    akkaTestKit.createTestProbe[Coordinator.CoordinatorMessage]("coordinator")
  val mutator: TestProbe[MutatorMessage] =
    akkaTestKit.createTestProbe[Mutator.MutatorMessage]("mutator")

  "Converting a full grid model" when {
    "omitting time series" should {
      val converter: ActorRef[Converter.ConverterMessage] = akkaTestKit.spawn(
        Converter.idle(
          Converter.StateData(
            "1-LV-rural1--0-no_sw",
            "src/test/resources/gridData/1-LV-rural1--0-no_sw",
            "http://141.51.193.167/simbench/gui/usecase/download",
            failOnExistingFiles = false,
            ".csv",
            "UTF-8",
            ";",
            removeSwitches = false,
            20,
            createTimeSeries = false,
            mutator.ref,
            coordinator.ref
          )
        ),
        "converter"
      )
      converter ! Converter.Convert("1-LV-rural1--0-no_sw")
      "provide correct amount of converted models" in {

        /* Not receiving time series comes implicitly. Receive all all other awaited messages */
        mutator.expectMessageType[Mutator.PersistGridStructure](
          FiniteDuration(60, "s")
        ) match {
          case Mutator.PersistGridStructure(
              simBenchCode,
              nodes,
              lines,
              transformers2w,
              transformers3w,
              switches,
              measurements,
              loads,
              fixedFeedIns,
              _
              ) =>
            simBenchCode shouldBe "1-LV-rural1--0-no_sw"
            nodes.size shouldBe 15
            lines.size shouldBe 13
            transformers2w.size shouldBe 1
            transformers3w.size shouldBe 0
            switches.size shouldBe 0
            measurements.size shouldBe 0
            loads.size shouldBe 13
            fixedFeedIns.size shouldBe 4
        }

        mutator.expectMessageType[Mutator.PersistTimeSeriesMapping](
          FiniteDuration(60, "s")
        ) match {
          case Mutator.PersistTimeSeriesMapping(mapping, _) =>
            mapping.size shouldBe 0
        }

        mutator.expectMessageType[Mutator.PersistNodalResults](
          FiniteDuration(60, "s")
        ) match {
          case Mutator.PersistNodalResults(results, _) =>
            results.size shouldBe 15
        }
      }
    }
  }
}
