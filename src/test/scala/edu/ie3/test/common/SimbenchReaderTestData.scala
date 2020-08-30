/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
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
      1,
      1,
      0,
      0,
      0,
      0.965
    ),
    StudyCase(
      "n1",
      1,
      1,
      0,
      0,
      0,
      0.965
    ),
    StudyCase(
      "hW",
      1,
      1,
      1,
      0.8,
      1,
      0.965
    ),
    StudyCase(
      "hPV",
      1,
      1,
      0.85,
      0.95,
      1,
      0.965
    ),
    StudyCase(
      "lW",
      0.1,
      0.122543,
      1,
      0.8,
      1,
      1.055
    ),
    StudyCase(
      "lPV",
      0.1,
      0.122543,
      0.85,
      0.95,
      1,
      1.055
    )
  )

  val coordinates = Vector(
    Coordinate(
      "coord_0",
      11.411,
      53.6407,
      "LV1.101",
      7
    ),
    Coordinate(
      "coord_3",
      11.4097,
      53.6413,
      "LV1.101",
      7
    ),
    Coordinate(
      "coord_14",
      11.4097,
      53.6413,
      "MV1.101_LV1.101_Feeder1",
      5
    )
  )

  val lineTypes = Vector(
    ACLineType(
      "NAYY 4x150SE 0.6/1kV",
      0.2067,
      0.0804248,
      260.752,
      270,
      LineStyle.Cable
    )
  )

  val transformerTypes = Vector(
    Transformer2WType(
      "0.16 MVA 20/0.4 kV DOTE 160/20  SGB",
      0.16,
      20,
      0.4,
      150,
      4,
      2.35,
      0.46,
      0.28751,
      tapable = true,
      BranchElementPort.HV,
      2.5,
      0,
      0,
      -2,
      2
    )
  )

  val nodes = Vector(
    Node(
      "MV1.101 Bus 4",
      NodeType.BusBar,
      Some(1.025),
      Some(0.0),
      20,
      0.965,
      1.055,
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
      0.4,
      0.9,
      1.1,
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
      0.4,
      0.9,
      1.1,
      None,
      Some(coordinates(1)),
      "LV1.101",
      7
    )
  )

  val powerFlowResults = Vector(
    NodePFResult(
      nodes(1),
      1.02203,
      1.17226,
      None,
      "LV1.101",
      7
    ),
    NodePFResult(
      nodes(2),
      1.02474,
      1.17148,
      None,
      "LV1.101",
      7
    ),
    NodePFResult(
      nodes(0),
      1.025,
      0,
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
      0.132499,
      100,
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
      100,
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
      0.014,
      0.005533,
      0.0150538,
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
      0.023,
      0,
      0.023,
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
          .toZonedDateTime("2016-01-01 00:00:00") -> (0.276685, -0.067519),
        TimeUtil.withDefaults
          .toZonedDateTime("2016-01-01 00:15:00") -> (0.066011, 0.060412)
      )
    ),
    LoadProfile(
      "H0B",
      H0B,
      Map(
        TimeUtil.withDefaults
          .toZonedDateTime("2016-01-01 00:00:00") -> (0.065826, -0.014175),
        TimeUtil.withDefaults
          .toZonedDateTime("2016-01-01 00:15:00") -> (0.190476, 0.212622)
      )
    ),
    LoadProfile(
      "H0C",
      H0C,
      Map(
        TimeUtil.withDefaults
          .toZonedDateTime("2016-01-01 00:00:00") -> (0.237082, 0.242253),
        TimeUtil.withDefaults
          .toZonedDateTime("2016-01-01 00:15:00") -> (0.066869, 0.007691)
      )
    ),
    LoadProfile(
      "L1A",
      L1A,
      Map(
        TimeUtil.withDefaults
          .toZonedDateTime("2016-01-01 00:00:00") -> (0.144231, 0.125501),
        TimeUtil.withDefaults
          .toZonedDateTime("2016-01-01 00:15:00") -> (0.416628, 0.587453)
      )
    ),
    LoadProfile(
      "L2A",
      L2A,
      Map(
        TimeUtil.withDefaults
          .toZonedDateTime("2016-01-01 00:00:00") -> (0.321053, 0.326352),
        TimeUtil.withDefaults
          .toZonedDateTime("2016-01-01 00:15:00") -> (0.317544, 0.346238)
      )
    )
  )

  val powerPlantProfiles: Vector[PowerPlantProfile] = Vector(
    PowerPlantProfile(
      "PowerPlantProfile16",
      PowerPlantProfileType.PowerPlantProfile16,
      Map(
        TimeUtil.withDefaults
          .toZonedDateTime("2016-01-01 00:00:00") -> 0.7,
        TimeUtil.withDefaults
          .toZonedDateTime("2016-01-01 00:15:00") -> 0.7
      )
    ),
    PowerPlantProfile(
      "PowerPlantProfile17",
      PowerPlantProfileType.PowerPlantProfile17,
      Map(
        TimeUtil.withDefaults
          .toZonedDateTime("2016-01-01 00:00:00") -> 0.7,
        TimeUtil.withDefaults
          .toZonedDateTime("2016-01-01 00:15:00") -> 0.7
      )
    )
  )

  val resProfiles: Vector[ResProfile] = Vector(
    ResProfile(
      "PV6",
      ResProfileType.PV6,
      Map(
        TimeUtil.withDefaults
          .toZonedDateTime("2016-01-02 10:15:00") -> 0.078013,
        TimeUtil.withDefaults
          .toZonedDateTime("2016-01-02 10:30:00") -> 0.0770834
      )
    ),
    ResProfile(
      "PV8",
      ResProfileType.PV8,
      Map(
        TimeUtil.withDefaults
          .toZonedDateTime("2016-01-02 10:15:00") -> 0.0550869,
        TimeUtil.withDefaults
          .toZonedDateTime("2016-01-02 10:30:00") -> 0.055754
      )
    )
  )

  val expectedGridModel: GridModel = GridModel(
    "simpleDataset",
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
