package edu.ie3.simbench.io

import edu.ie3.simbench.exception.io.IoException
import edu.ie3.test.common.UnitSpec

class CsvReaderSpec extends UnitSpec {
  "The csv reader" should {
    val classLoader = this.getClass.getClassLoader
    val validCsvFilePath = classLoader.getResource("io/csv/Load.csv").getPath
    val invalidFilePath =
      classLoader.getResource("io/csv/invalidFile.png").getPath
    val separator = ";"

    "be instantiated correctly with valid file path" in {
      val csvReader = CsvReader(validCsvFilePath, separator)
      csvReader.filePath shouldBe validCsvFilePath
      csvReader.separator shouldBe separator
      csvReader.fileEnding shouldBe ".csv"
      csvReader.fileEncoding shouldBe "UTF-8"
    }

    "throw an IoException when instantiated with an invalid file path" in {
      val thrown = intercept[IoException](CsvReader(invalidFilePath, separator))
      thrown.getMessage.endsWith("Only .csv is supported (case sensitive).") shouldBe true
    }

    val csvReader = CsvReader(validCsvFilePath, separator)
    val mapFieldsMethod = PrivateMethod[Map[String, Int]](Symbol("mapFields"))

    "correctly map the desired fields of a valid headline" in {
      val headline = "id;node;profile;pLoad;qLoad;sR;subnet;voltLvl"
      val desiredFields = Array("id",
                                "node",
                                "profile",
                                "pLoad",
                                "qLoad",
                                "sR",
                                "subnet",
                                "voltLvl")

      val method = PrivateMethod[Map[String, Int]](Symbol("mapFields"))
      val mapping = csvReader invokePrivate method(headline, desiredFields)

      mapping shouldBe Map("id" -> 0,
                           "node" -> 1,
                           "profile" -> 2,
                           "pLoad" -> 3,
                           "qLoad" -> 4,
                           "sR" -> 5,
                           "subnet" -> 6,
                           "voltLvl" -> 7)
    }

    "correctly map the desired fields of a valid headline with more than the desired fields" in {
      val headline =
        "id;node;profile;pLoad;qLoad;voltage;sR;subnet;voltLvl;height"
      val desiredFields = Array("id",
                                "node",
                                "profile",
                                "pLoad",
                                "qLoad",
                                "sR",
                                "subnet",
                                "voltLvl")

      val mapping = csvReader invokePrivate mapFieldsMethod(headline,
                                                            desiredFields)

      mapping shouldBe Map("id" -> 0,
                           "node" -> 1,
                           "profile" -> 2,
                           "pLoad" -> 3,
                           "qLoad" -> 4,
                           "sR" -> 6,
                           "subnet" -> 7,
                           "voltLvl" -> 8)
    }

    "throw an exception, if one desired field is missing in an invalid headline" in {
      val headline = "id;node;profile;pLoad;qLoad;sR;voltLvl"
      val desiredFields = Array("id",
                                "node",
                                "profile",
                                "pLoad",
                                "qLoad",
                                "sR",
                                "subnet",
                                "voltLvl")

      val thrown = intercept[IoException](
        csvReader invokePrivate mapFieldsMethod(headline, desiredFields))

      thrown.getMessage.endsWith("does not contain the desired field subnet") shouldBe true
    }

    val readLineMethod = PrivateMethod[Map[String, String]](Symbol("readLine"))

    "read a single valid line correctly" in {
      val validLine =
        "LV1.101 Load 4;LV1.101 Bus 13;H0-B;0.002;0.00079;0.00215054;LV1.101;7"
      val fieldMapping = Map("id" -> 0,
                             "node" -> 1,
                             "profile" -> 2,
                             "pLoad" -> 3,
                             "qLoad" -> 4,
                             "sR" -> 5,
                             "subnet" -> 6,
                             "voltLvl" -> 7)

      val result = csvReader invokePrivate readLineMethod(validLine,
                                                          fieldMapping)
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
      val fieldMapping = Map("id" -> 0,
                             "node" -> 1,
                             "profile" -> 2,
                             "pLoad" -> 3,
                             "qLoad" -> 4,
                             "sR" -> 5,
                             "subnet" -> 6,
                             "voltLvl" -> 7)

      val thrown = intercept[IoException](
        csvReader invokePrivate readLineMethod(validLine, fieldMapping))

      thrown.getMessage.contains(
        "does not contain the correct amount of fields (apparent = 7, needed = 8)") shouldBe true
    }
  }
}
