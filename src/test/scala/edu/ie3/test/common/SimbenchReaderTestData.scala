package edu.ie3.test.common

import edu.ie3.simbench.model.datamodel.ExternalNet.Simple
import edu.ie3.simbench.model.datamodel.Line.ACLine
import edu.ie3.simbench.model.datamodel._
import edu.ie3.simbench.model.datamodel.enums._
import edu.ie3.simbench.model.datamodel.profiles.LoadProfileType._
import edu.ie3.simbench.model.datamodel.profiles._
import edu.ie3.simbench.model.datamodel.types.LineType.ACLineType
import edu.ie3.simbench.model.datamodel.types.Transformer2WType
import edu.ie3.util.TimeUtil

trait SimbenchReaderTestData {

  val studyCases = Vector(
    StudyCase(
      "hL",
      BigDecimal("1"),
      BigDecimal("1"),
      BigDecimal("0"),
      BigDecimal("0"),
      BigDecimal("0"),
      BigDecimal("0.965")
    ),
    StudyCase(
      "n1",
      BigDecimal("1"),
      BigDecimal("1"),
      BigDecimal("0"),
      BigDecimal("0"),
      BigDecimal("0"),
      BigDecimal("0.965")
    ),
    StudyCase(
      "hW",
      BigDecimal("1"),
      BigDecimal("1"),
      BigDecimal("1"),
      BigDecimal("0.8"),
      BigDecimal("1"),
      BigDecimal("0.965")
    ),
    StudyCase(
      "hPV",
      BigDecimal("1"),
      BigDecimal("1"),
      BigDecimal("0.85"),
      BigDecimal("0.95"),
      BigDecimal("1"),
      BigDecimal("0.965")
    ),
    StudyCase(
      "lW",
      BigDecimal("0.1"),
      BigDecimal("0.122543"),
      BigDecimal("1"),
      BigDecimal("0.8"),
      BigDecimal("1"),
      BigDecimal("1.055")
    ),
    StudyCase(
      "lPV",
      BigDecimal("0.1"),
      BigDecimal("0.122543"),
      BigDecimal("0.85"),
      BigDecimal("0.95"),
      BigDecimal("1"),
      BigDecimal("1.055")
    )
  )

  val coordinates = Vector(
    Coordinate(
      "coord_0",
      BigDecimal("11.411"),
      BigDecimal("53.6407"),
      "LV1.101",
      7
    ),
    Coordinate(
      "coord_3",
      BigDecimal("11.4097"),
      BigDecimal("53.6413"),
      "LV1.101",
      7
    ),
    Coordinate(
      "coord_14",
      BigDecimal("11.4097"),
      BigDecimal("53.6413"),
      "MV1.101_LV1.101_Feeder1",
      5
    )
  )

  val lineTypes = Vector(
    ACLineType(
      "NAYY 4x150SE 0.6/1kV",
      BigDecimal("0.2067"),
      BigDecimal("0.0804248"),
      BigDecimal("260.752"),
      BigDecimal("270"),
      LineStyle.Cable
    )
  )

  val transformerTypes = Vector(
    Transformer2WType(
      "0.16 MVA 20/0.4 kV DOTE 160/20  SGB",
      BigDecimal("0.16"),
      BigDecimal("20"),
      BigDecimal("0.4"),
      BigDecimal("150"),
      BigDecimal("4"),
      BigDecimal("2.35"),
      BigDecimal("0.46"),
      BigDecimal("0.28751"),
      tapable = true,
      BranchElementPort.HV,
      BigDecimal("2.5"),
      BigDecimal("0"),
      0,
      -2,
      2
    )
  )

  val nodes = Vector(
    Node(
      "MV1.101 Bus 4",
      NodeType.BusBar,
      Some(BigDecimal("1.025")),
      Some(BigDecimal("0.0")),
      BigDecimal("20"),
      BigDecimal("0.965"),
      BigDecimal("1.055"),
      None,
      Some(coordinates(2)),
      "MV1.101_LV1.101_Feeder1",
      5
    ),
    Node(
      "LV1.101 Bus 1",
      NodeType.BusBar,
      None,
      None,
      BigDecimal("0.4"),
      BigDecimal("0.9"),
      BigDecimal("1.1"),
      None,
      Some(coordinates(0)),
      "LV1.101",
      7
    ),
    Node(
      "LV1.101 Bus 4",
      NodeType.BusBar,
      None,
      None,
      BigDecimal("0.4"),
      BigDecimal("0.9"),
      BigDecimal("1.1"),
      None,
      Some(coordinates(1)),
      "LV1.101",
      7
    )
  )

  val powerFlowResults = Vector(
    NodePFResult(
      nodes(1),
      BigDecimal("1.02203"),
      BigDecimal("1.17226"),
      None,
      "LV1.101",
      7
    ),
    NodePFResult(
      nodes(2),
      BigDecimal("1.02474"),
      BigDecimal("1.17148"),
      None,
      "LV1.101",
      7
    ),
    NodePFResult(
      nodes(0),
      BigDecimal("1.025"),
      BigDecimal("0"),
      None,
      "MV1.101_LV1.101_Feeder1",
      5
    )
  )

  val lines = Vector(
    ACLine(
      "LV1.101 Line 10",
      nodes(2),
      nodes(1),
      lineTypes(0),
      BigDecimal("0.132499"),
      BigDecimal("100"),
      "LV1.101",
      7
    )
  )

  val transformers = Vector(
    Transformer2W(
      "MV1.101-LV1.101-Trafo 1",
      nodes(0),
      nodes(2),
      transformerTypes(0),
      0,
      autoTap = false,
      None,
      BigDecimal("100"),
      None,
      "LV1.101",
      6
    )
  )

  val externalNets = Vector(
    Simple(
      "MV1.101 grid at LV1.101",
      nodes(0),
      CalculationType.VaVm,
      1,
      None,
      None,
      "LV1.101_MV1.101_eq",
      5
    )
  )

  val loads = Vector(
    Load(
      "LV1.101 Load 8",
      nodes(1),
      LoadProfileType.L2A,
      BigDecimal("0.014"),
      BigDecimal("0.005533"),
      BigDecimal("0.0150538"),
      "LV1.101",
      7
    )
  )

  val res = Vector(
    RES(
      "LV1.101 SGen 3",
      nodes(2),
      ResType.PV,
      ResProfileType.PV5,
      CalculationType.PQ,
      BigDecimal("0.023"),
      BigDecimal("0"),
      BigDecimal("0.023"),
      "LV1.101",
      7
    )
  )

  val loadProfiles: Vector[LoadProfile] = Vector(
    LoadProfile(
      "H0A",
      H0A,
      Map(
        TimeUtil.withDefaults
          .toZonedDateTime("2016-01-01 00:00:00") -> (BigDecimal(
          "0.276685"
        ), BigDecimal("-0.067519")),
        TimeUtil.withDefaults
          .toZonedDateTime("2016-01-01 00:15:00") -> (BigDecimal(
          "0.066011"
        ), BigDecimal("0.060412"))
      )
    ),
    LoadProfile(
      "H0B",
      H0B,
      Map(
        TimeUtil.withDefaults
          .toZonedDateTime("2016-01-01 00:00:00") -> (BigDecimal(
          "0.065826"
        ), BigDecimal("-0.014175")),
        TimeUtil.withDefaults
          .toZonedDateTime("2016-01-01 00:15:00") -> (BigDecimal(
          "0.190476"
        ), BigDecimal("0.212622"))
      )
    ),
    LoadProfile(
      "H0C",
      H0C,
      Map(
        TimeUtil.withDefaults
          .toZonedDateTime("2016-01-01 00:00:00") -> (BigDecimal(
          "0.237082"
        ), BigDecimal("0.242253")),
        TimeUtil.withDefaults
          .toZonedDateTime("2016-01-01 00:15:00") -> (BigDecimal(
          "0.066869"
        ), BigDecimal("0.007691"))
      )
    ),
    LoadProfile(
      "L1A",
      L1A,
      Map(
        TimeUtil.withDefaults
          .toZonedDateTime("2016-01-01 00:00:00") -> (BigDecimal(
          "0.144231"
        ), BigDecimal("0.125501")),
        TimeUtil.withDefaults
          .toZonedDateTime("2016-01-01 00:15:00") -> (BigDecimal(
          "0.416628"
        ), BigDecimal("0.587453"))
      )
    ),
    LoadProfile(
      "L2A",
      L2A,
      Map(
        TimeUtil.withDefaults
          .toZonedDateTime("2016-01-01 00:00:00") -> (BigDecimal(
          "0.321053"
        ), BigDecimal("0.326352")),
        TimeUtil.withDefaults
          .toZonedDateTime("2016-01-01 00:15:00") -> (BigDecimal(
          "0.317544"
        ), BigDecimal("0.346238"))
      )
    )
  )

  val powerPlantProfiles: Vector[PowerPlantProfile] = Vector(
    PowerPlantProfile(
      "PowerPlantProfile16",
      PowerPlantProfileType.PowerPlantProfile16,
      Map(
        TimeUtil.withDefaults
          .toZonedDateTime("2016-01-01 00:00:00") -> BigDecimal("0.7"),
        TimeUtil.withDefaults
          .toZonedDateTime("2016-01-01 00:15:00") -> BigDecimal("0.7")
      )
    ),
    PowerPlantProfile(
      "PowerPlantProfile17",
      PowerPlantProfileType.PowerPlantProfile17,
      Map(
        TimeUtil.withDefaults
          .toZonedDateTime("2016-01-01 00:00:00") -> BigDecimal("0.7"),
        TimeUtil.withDefaults
          .toZonedDateTime("2016-01-01 00:15:00") -> BigDecimal("0.7")
      )
    )
  )

  val resProfiles: Vector[ResProfile] = Vector(
    ResProfile(
      "PV6",
      ResProfileType.PV6,
      Map(
        TimeUtil.withDefaults
          .toZonedDateTime("2016-01-02 10:15:00") -> BigDecimal("0.078013"),
        TimeUtil.withDefaults
          .toZonedDateTime("2016-01-02 10:30:00") -> BigDecimal("0.0770834")
      )
    ),
    ResProfile(
      "PV8",
      ResProfileType.PV8,
      Map(
        TimeUtil.withDefaults
          .toZonedDateTime("2016-01-02 10:15:00") -> BigDecimal(
          "0.0550869"
        ),
        TimeUtil.withDefaults
          .toZonedDateTime("2016-01-02 10:30:00") -> BigDecimal("0.055754")
      )
    )
  )

  val expectedGridModel: GridModel = GridModel(
    externalNets,
    lines,
    loads,
    loadProfiles,
    Vector.empty[Measurement],
    nodes,
    powerFlowResults,
    Vector.empty[PowerPlant],
    powerPlantProfiles,
    res,
    resProfiles,
    Vector.empty[Shunt],
    Vector.empty[Storage],
    Vector.empty[StorageProfile],
    studyCases,
    Vector.empty[Substation],
    Vector.empty[Switch],
    transformers,
    Vector.empty[Transformer3W]
  )
}
