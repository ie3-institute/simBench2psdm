/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.simbench.io

import edu.ie3.simbench.exception.io.IoException
import edu.ie3.simbench.io.HeadLineField.{MandatoryField, OptionalField}
import edu.ie3.simbench.model.RawModelData
import edu.ie3.simbench.model.datamodel.Load
import edu.ie3.test.common.UnitSpec

class CsvReaderSpec extends UnitSpec {
  "The csv reader" should {
    val classLoader = this.getClass.getClassLoader
    val validCsvFilePath = IoUtils.trimFirstSeparatorInWindowsPath(
      classLoader.getResource("io/csv/Load.csv").getPath
    )
    val validFields: Array[HeadLineField] = Array(
      MandatoryField("id"),
      MandatoryField("node"),
      MandatoryField("profile"),
      MandatoryField("pLoad"),
      MandatoryField("qLoad"),
      MandatoryField("sR"),
      MandatoryField("subnet"),
      MandatoryField("voltLvl")
    )
    val invalidFilePath =
      IoUtils.trimFirstSeparatorInWindowsPath(
        classLoader.getResource("io/csv/invalidFile.png").getPath
      )
    val separator = ";"

    "be instantiated correctly with valid file path" in {
      CsvReader(classOf[Load], validCsvFilePath, separator) match {
        case Some(reader) =>
          reader.filePath shouldBe validCsvFilePath
          reader.separator shouldBe separator
          reader.fileEnding shouldBe ".csv"
          reader.fileEncoding shouldBe "UTF-8"
        case None =>
          fail("Expected to get a valid instance of CsvReader")
      }
    }

    "return None when instantiated with an invalid file path" in {
      CsvReader(classOf[Load], invalidFilePath, separator) shouldBe None
    }

    val csvReader = CsvReader(classOf[Load], validCsvFilePath, separator)
      .getOrElse(fail("Expected to get an actual instance of CsvReader."))
    val mapFieldsMethod = PrivateMethod[Map[String, Int]](Symbol("mapFields"))

    "correctly map the desired field ids of a valid headline" in {
      val headline = "id;node;profile;pLoad;qLoad;sR;subnet;voltLvl"
      val desiredFields = Array(
        MandatoryField("id"),
        MandatoryField("node"),
        MandatoryField("profile"),
        MandatoryField("pLoad"),
        MandatoryField("qLoad"),
        MandatoryField("sR"),
        MandatoryField("subnet"),
        MandatoryField("voltLvl")
      )

      val method = PrivateMethod[Map[String, Int]](Symbol("mapFields"))
      val mapping = csvReader invokePrivate method(headline, desiredFields)

      mapping shouldBe Map(
        "id" -> 0,
        "node" -> 1,
        "profile" -> 2,
        "pLoad" -> 3,
        "qLoad" -> 4,
        "sR" -> 5,
        "subnet" -> 6,
        "voltLvl" -> 7
      )
    }

    "correctly map the desired field ids of a valid headline with more than the desired fields" in {
      val headline =
        "id;node;profile;pLoad;qLoad;voltage;sR;subnet;voltLvl;height"
      val desiredFields = Array(
        MandatoryField("id"),
        MandatoryField("node"),
        MandatoryField("profile"),
        MandatoryField("pLoad"),
        MandatoryField("qLoad"),
        MandatoryField("sR"),
        MandatoryField("subnet"),
        MandatoryField("voltLvl")
      )

      val mapping = csvReader invokePrivate mapFieldsMethod(
        headline,
        desiredFields
      )

      mapping shouldBe Map(
        "id" -> 0,
        "node" -> 1,
        "profile" -> 2,
        "pLoad" -> 3,
        "qLoad" -> 4,
        "sR" -> 6,
        "subnet" -> 7,
        "voltLvl" -> 8
      )
    }

    "throw an exception, if one mandatory field is missing in an invalid headline" in {
      val headline = "id;node;profile;pLoad;qLoad;sR;voltLvl"
      val desiredFields = Array(
        MandatoryField("id"),
        MandatoryField("node"),
        MandatoryField("profile"),
        MandatoryField("pLoad"),
        MandatoryField("qLoad"),
        MandatoryField("sR"),
        MandatoryField("subnet"),
        MandatoryField("voltLvl")
      )

      val thrown = intercept[IoException](
        csvReader invokePrivate mapFieldsMethod(headline, desiredFields)
      )

      thrown.getMessage.endsWith("does not contain the mandatory field subnet") shouldBe true
    }

    "tolerate, that an optional field is not apparent" in {
      val headline = "id;node;profile;pLoad;qLoad;sR;voltLvl"
      val desiredFields: Array[HeadLineField] = Array(
        MandatoryField("id"),
        MandatoryField("node"),
        MandatoryField("profile"),
        MandatoryField("pLoad"),
        MandatoryField("qLoad"),
        MandatoryField("sR"),
        OptionalField("subnet"),
        MandatoryField("voltLvl")
      )

      val mapping = csvReader invokePrivate mapFieldsMethod(
        headline,
        desiredFields
      )

      mapping shouldBe Map(
        "id" -> 0,
        "node" -> 1,
        "profile" -> 2,
        "pLoad" -> 3,
        "qLoad" -> 4,
        "sR" -> 5,
        "voltLvl" -> 6
      )
    }

    val readLineMethod = PrivateMethod[Map[String, String]](Symbol("readLine"))

    "read a single valid line correctly" in {
      val validLine =
        "LV1.101 Load 4;LV1.101 Bus 13;H0-B;0.002;0.00079;0.00215054;LV1.101;7"
      val fieldMapping = Map(
        "id" -> 0,
        "node" -> 1,
        "profile" -> 2,
        "pLoad" -> 3,
        "qLoad" -> 4,
        "sR" -> 5,
        "subnet" -> 6,
        "voltLvl" -> 7
      )

      val result = csvReader invokePrivate readLineMethod(
        validLine,
        fieldMapping
      )
      result shouldBe Map(
        "id" -> "LV1.101 Load 4",
        "node" -> "LV1.101 Bus 13",
        "profile" -> "H0-B",
        "pLoad" -> "0.002",
        "qLoad" -> "0.00079",
        "sR" -> "0.00215054",
        "subnet" -> "LV1.101",
        "voltLvl" -> "7"
      )
    }

    "throw an exception, if one field is missing" in {
      val validLine =
        "LV1.101 Load 4;LV1.101 Bus 13;H0-B;0.002;0.00215054;LV1.101;7"
      val fieldMapping = Map(
        "id" -> 0,
        "node" -> 1,
        "profile" -> 2,
        "pLoad" -> 3,
        "qLoad" -> 4,
        "sR" -> 5,
        "subnet" -> 6,
        "voltLvl" -> 7
      )

      val thrown = intercept[IoException](
        csvReader invokePrivate readLineMethod(validLine, fieldMapping)
      )

      thrown.getMessage.contains(
        "does not contain the correct amount of fields (apparent = 7, needed = 8)"
      ) shouldBe true
    }

    "read the valid file correctly" in {
      CsvReader(classOf[Load], validCsvFilePath, separator) match {
        case Some(csvReader) =>
          val result = csvReader.read(validFields)

          val expected = Vector(
            RawModelData(
              classOf[Load],
              Map(
                "id" -> "LV1.101 Load 1",
                "node" -> "LV1.101 Bus 10",
                "profile" -> "L2-A",
                "pLoad" -> "0.006",
                "qLoad" -> "0.002371",
                "sR" -> "0.00645161",
                "subnet" -> "LV1.101",
                "voltLvl" -> "7"
              )
            ),
            RawModelData(
              classOf[Load],
              Map(
                "id" -> "LV1.101 Load 2",
                "node" -> "LV1.101 Bus 8",
                "profile" -> "H0-C",
                "pLoad" -> "0.003",
                "qLoad" -> "0.001186",
                "sR" -> "0.00322581",
                "subnet" -> "LV1.101",
                "voltLvl" -> "7"
              )
            )
          )

          result shouldBe expected
        case None =>
          fail("Expected to get a valid instance of CsvReader")
      }
    }
  }
}
