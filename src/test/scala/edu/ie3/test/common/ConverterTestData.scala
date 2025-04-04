package edu.ie3.test.common

import edu.ie3.datamodel.models.StandardUnits.{
  ADMITTANCE_PER_LENGTH,
  ELECTRIC_CURRENT_MAGNITUDE,
  IMPEDANCE_PER_LENGTH,
  RATED_VOLTAGE_MAGNITUDE
}
import edu.ie3.datamodel.models.input.connector.`type`.{
  LineTypeInput,
  Transformer2WTypeInput
}
import edu.ie3.datamodel.models.input.connector.{
  LineInput,
  SwitchInput,
  Transformer2WInput
}
import edu.ie3.datamodel.models.input.system.characteristic.{
  CosPhiFixed,
  OlmCharacteristicInput
}
import edu.ie3.datamodel.models.input.system.{FixedFeedInInput, LoadInput}
import edu.ie3.datamodel.models.input.{
  MeasurementUnitInput,
  NodeInput,
  OperatorInput
}
import edu.ie3.datamodel.models.profile.LoadProfile.DefaultLoadProfiles
import edu.ie3.datamodel.models.result.NodeResult
import edu.ie3.datamodel.models.voltagelevels.GermanVoltageLevelUtils
import edu.ie3.datamodel.models.voltagelevels.GermanVoltageLevelUtils.{
  LV,
  MV_10KV,
  MV_20KV
}
import edu.ie3.datamodel.models.{Entity, OperationTime}
import edu.ie3.datamodel.utils.GridAndGeoUtils
import edu.ie3.simbench.exception.TestingException
import edu.ie3.simbench.model.datamodel.Line.ACLine
import edu.ie3.simbench.model.datamodel.Measurement.{
  LineMeasurement,
  NodeMeasurement,
  TransformerMeasurement
}
import edu.ie3.simbench.model.datamodel._
import edu.ie3.simbench.model.datamodel.enums.BranchElementPort.HV
import edu.ie3.simbench.model.datamodel.enums.CalculationType.PVm
import edu.ie3.simbench.model.datamodel.enums.MeasurementVariable.Voltage
import edu.ie3.simbench.model.datamodel.enums.NodeType.{BusBar, DoubleBusBar}
import edu.ie3.simbench.model.datamodel.enums.PowerPlantType.Lignite
import edu.ie3.simbench.model.datamodel.enums._
import edu.ie3.simbench.model.datamodel.profiles.PowerPlantProfileType.PowerPlantProfile1
import edu.ie3.simbench.model.datamodel.profiles.{
  LoadProfile,
  LoadProfileType,
  ResProfileType
}
import edu.ie3.simbench.model.datamodel.types.LineType.{ACLineType, DCLineType}
import edu.ie3.simbench.model.datamodel.types.{LineType, Transformer2WType}
import edu.ie3.util.quantities.PowerSystemUnits._
import org.locationtech.jts.geom.{
  GeometryFactory,
  Point,
  Coordinate => JTSCoordinate
}
import tech.units.indriya.quantity.Quantities
import tech.units.indriya.unit.Units._

import java.time.ZonedDateTime
import java.util.UUID
import javax.measure.MetricPrefix

trait ConverterTestData {

  /** Case class to denote a consistent pair of input and expected output of a
    * conversion
    *
    * @param input
    *   Input model
    * @param result
    *   Resulting, converted model
    * @tparam I
    *   Type of input model
    * @tparam R
    *   Type of result class
    */
  final case class ConversionPair[I <: SimbenchModel, R <: Entity](
      input: I,
      result: R
  ) {
    def getPair: (I, R) = (input, result)
  }

  val geometryFactory = new GeometryFactory()
  val coordinates = Map(
    "random coordinate" -> (
      Coordinate(
        "random coordinate",
        BigDecimal("7.412262"),
        BigDecimal("51.492689"),
        "subnet_1",
        5
      ),
      geometryFactory.createPoint(new JTSCoordinate(7.412262, 51.492689))
    ),
    "coordinate_14" -> (
      Coordinate(
        "coordinate_14",
        BigDecimal("11.4097"),
        BigDecimal("53.6413"),
        "MV1.101_LV1.101_Feeder1",
        5
      ),
      geometryFactory.createPoint(new JTSCoordinate(11.4097, 53.6413))
    ),
    "coordinate_2" -> (Coordinate(
      "coordinate_2",
      BigDecimal("11.411"),
      BigDecimal("53.6407"),
      "LV1.101",
      7
    ),
    geometryFactory.createPoint(new JTSCoordinate(11.411, 53.6407)))
  )

  def getCoordinatePair(key: String): (Coordinate, Point) =
    coordinates
      .getOrElse(
        key,
        throw TestingException(
          s"Cannot find input / result pair for ${Coordinate.getClass.getSimpleName} $key."
        )
      )

  val nodes = Map(
    "slack_node_0" -> ConversionPair(
      Node(
        "slack_node_0",
        BusBar,
        Some(BigDecimal("1.3")),
        None,
        BigDecimal("10.0"),
        BigDecimal("0.95"),
        BigDecimal("1.05"),
        None,
        Some(getCoordinatePair("random coordinate")._1),
        "MV0",
        5
      ),
      new NodeInput(
        UUID.randomUUID(),
        "slack_node_0",
        OperatorInput.NO_OPERATOR_ASSIGNED,
        OperationTime.notLimited(),
        Quantities.getQuantity(1.3, PU),
        true,
        getCoordinatePair("random coordinate")._2,
        MV_10KV,
        1
      )
    ),
    "node_0" -> ConversionPair(
      Node(
        "node_0",
        NodeType.Node,
        None,
        None,
        BigDecimal("10.0"),
        BigDecimal("0.95"),
        BigDecimal("1.05"),
        None,
        None,
        "MV1",
        5
      ),
      new NodeInput(
        UUID.randomUUID(),
        "node_0",
        OperatorInput.NO_OPERATOR_ASSIGNED,
        OperationTime.notLimited(),
        Quantities.getQuantity(1.0, PU),
        false,
        NodeInput.DEFAULT_GEO_POSITION,
        MV_10KV,
        2
      )
    ),
    "EHV Bus 177" -> ConversionPair(
      Node(
        "EHV Bus 177",
        DoubleBusBar,
        Some(BigDecimal("1.092")),
        None,
        BigDecimal("380"),
        BigDecimal("0.9"),
        BigDecimal("1.1"),
        None,
        None,
        "EHV1",
        1
      ),
      new NodeInput(
        UUID.randomUUID(),
        "EHV Bus 177",
        OperatorInput.NO_OPERATOR_ASSIGNED,
        OperationTime.notLimited(),
        Quantities.getQuantity(1.092, PU),
        false,
        NodeInput.DEFAULT_GEO_POSITION,
        GermanVoltageLevelUtils.EHV_380KV,
        1
      )
    ),
    "HV1 Bus 30" -> ConversionPair(
      Node(
        "HV1 Bus 30",
        BusBar,
        Some(BigDecimal("1.025")),
        Some(BigDecimal("0.0")),
        BigDecimal("110"),
        BigDecimal("0.9"),
        BigDecimal("1.1"),
        None,
        None,
        "HV1_MV1.102",
        1
      ),
      new NodeInput(
        UUID.randomUUID(),
        "HV1 Bus 30",
        OperatorInput.NO_OPERATOR_ASSIGNED,
        OperationTime.notLimited(),
        Quantities.getQuantity(1.025, PU),
        false,
        NodeInput.DEFAULT_GEO_POSITION,
        GermanVoltageLevelUtils.HV,
        3
      )
    ),
    "MV1.101 Bus 4" -> ConversionPair(
      Node(
        "MV1.101 Bus 4",
        NodeType.BusBar,
        Some(BigDecimal("1.025")),
        Some(BigDecimal("0.0")),
        BigDecimal("20"),
        BigDecimal("0.965"),
        BigDecimal("1.055"),
        Some(Substation("substation_1", "LV1.101", 7)),
        Some(getCoordinatePair("coordinate_14")._1),
        "MV1.101_LV1.101_Feeder1",
        5
      ),
      new NodeInput(
        UUID.randomUUID(),
        "MV1.101 Bus 4",
        OperatorInput.NO_OPERATOR_ASSIGNED,
        OperationTime.notLimited(),
        Quantities.getQuantity(1.025, PU),
        false,
        getCoordinatePair("coordinate_14")._2,
        MV_20KV,
        1
      )
    ),
    "MV1.101 Bus 5" -> ConversionPair(
      Node(
        "MV1.101 Bus 5",
        NodeType.BusBar,
        Some(BigDecimal("1.025")),
        Some(BigDecimal("0.0")),
        BigDecimal("20"),
        BigDecimal("0.965"),
        BigDecimal("1.055"),
        Some(Substation("substation_1", "LV1.101", 7)),
        Some(getCoordinatePair("coordinate_14")._1),
        "MV1.101_LV1.101_Feeder1",
        5
      ),
      new NodeInput(
        UUID.randomUUID(),
        "MV1.101 Bus 5",
        OperatorInput.NO_OPERATOR_ASSIGNED,
        OperationTime.notLimited(),
        Quantities.getQuantity(1.025, PU),
        false,
        getCoordinatePair("coordinate_14")._2,
        MV_20KV,
        5
      )
    ),
    "LV1.101 Bus 4" -> ConversionPair(
      Node(
        "LV1.101 Bus 4",
        NodeType.BusBar,
        None,
        None,
        BigDecimal("0.4"),
        BigDecimal("0.9"),
        BigDecimal("1.1"),
        None,
        Some(getCoordinatePair("coordinate_14")._1),
        "LV1.101",
        7
      ),
      new NodeInput(
        UUID.randomUUID(),
        "LV1.101 Bus 4",
        OperatorInput.NO_OPERATOR_ASSIGNED,
        OperationTime.notLimited(),
        Quantities.getQuantity(1d, PU),
        false,
        getCoordinatePair("coordinate_14")._2,
        LV,
        2
      )
    ),
    "LV1.101 Bus 1" -> ConversionPair(
      Node(
        "LV1.101 Bus 1",
        NodeType.BusBar,
        None,
        None,
        BigDecimal("0.4"),
        BigDecimal("0.9"),
        BigDecimal("1.1"),
        None,
        Some(getCoordinatePair("coordinate_2")._1),
        "LV1.101",
        7
      ),
      new NodeInput(
        UUID.randomUUID(),
        "LV1.101 Bus 1",
        OperatorInput.NO_OPERATOR_ASSIGNED,
        OperationTime.notLimited(),
        Quantities.getQuantity(1d, PU),
        false,
        getCoordinatePair("coordinate_2")._2,
        LV,
        2
      )
    )
  )

  def getNodePair(key: String): (Node, NodeInput) =
    nodes
      .getOrElse(
        key,
        throw TestingException(
          s"Cannot find input / result pair for ${Node.getClass.getSimpleName} $key."
        )
      )
      .getPair

  val nodeResults = Map(
    "0" -> ConversionPair(
      NodePFResult(
        getNodePair("LV1.101 Bus 1")._1,
        BigDecimal("1.04913"),
        BigDecimal("-144.292"),
        Some(Substation("substation_1", "LV1.101", 7)),
        "LV1.101",
        7
      ),
      new NodeResult(
        ZonedDateTime.parse("1970-01-01T00:00:00Z"),
        getNodePair("LV1.101 Bus 1")._2.getUuid,
        Quantities.getQuantity(1.04913, PU),
        Quantities.getQuantity(-144.292, DEGREE_GEOM)
      )
    ),
    "1" -> ConversionPair(
      NodePFResult(
        getNodePair("LV1.101 Bus 1")._1,
        BigDecimal("1.04729"),
        BigDecimal("-144.168"),
        None,
        "LV1.101",
        7
      ),
      new NodeResult(
        ZonedDateTime.parse("1970-01-01T00:00:00Z"),
        getNodePair("LV1.101 Bus 1")._2.getUuid,
        Quantities.getQuantity(1.04729, PU),
        Quantities.getQuantity(-144.168, DEGREE_GEOM)
      )
    )
  )

  def getNodeResultPair(key: String): (NodePFResult, NodeResult) =
    nodeResults
      .getOrElse(
        key,
        throw TestingException(
          s"Cannot find input / result pair for ${NodePFResult.getClass.getSimpleName} $key."
        )
      )
      .getPair

  val acLineTypes = Map(
    "NAYY 4x150SE 0.6/1kV" -> ConversionPair(
      ACLineType(
        "NAYY 4x150SE 0.6/1kV",
        BigDecimal("0.2067"),
        BigDecimal("0.0804248"),
        BigDecimal("260.752"),
        BigDecimal("270"),
        LineStyle.Cable
      ),
      new LineTypeInput(
        UUID.randomUUID(),
        "NAYY 4x150SE 0.6/1kV",
        Quantities
          .getQuantity(260.752, ADMITTANCE_PER_LENGTH),
        Quantities.getQuantity(0d, ADMITTANCE_PER_LENGTH),
        Quantities.getQuantity(0.2067, IMPEDANCE_PER_LENGTH),
        Quantities.getQuantity(0.0804248, IMPEDANCE_PER_LENGTH),
        Quantities.getQuantity(270d, ELECTRIC_CURRENT_MAGNITUDE),
        Quantities.getQuantity(0.4, RATED_VOLTAGE_MAGNITUDE)
      )
    ),
    "24-AL1/4-ST1A 20.0" -> ConversionPair(
      ACLineType(
        "24-AL1/4-ST1A 20.0",
        BigDecimal("1.2012"),
        BigDecimal("0.394"),
        BigDecimal("3.53429"),
        BigDecimal("140"),
        LineStyle.OverheadLine
      ),
      new LineTypeInput(
        UUID.randomUUID(),
        "24-AL1/4-ST1A 20.0",
        Quantities
          .getQuantity(3.53429, ADMITTANCE_PER_LENGTH),
        Quantities.getQuantity(0d, ADMITTANCE_PER_LENGTH),
        Quantities.getQuantity(1.2012, IMPEDANCE_PER_LENGTH),
        Quantities.getQuantity(0.394, IMPEDANCE_PER_LENGTH),
        Quantities.getQuantity(140d, ELECTRIC_CURRENT_MAGNITUDE),
        Quantities.getQuantity(0.4, RATED_VOLTAGE_MAGNITUDE)
      )
    ),
    "1x630_RM/50" -> ConversionPair(
      ACLineType(
        "1x630_RM/50",
        BigDecimal("0.122"),
        BigDecimal("0.122522"),
        BigDecimal("58.7478"),
        BigDecimal("652"),
        LineStyle.Cable
      ),
      new LineTypeInput(
        UUID.randomUUID(),
        "1x630_RM/50",
        Quantities
          .getQuantity(58.7478, ADMITTANCE_PER_LENGTH),
        Quantities.getQuantity(0d, ADMITTANCE_PER_LENGTH),
        Quantities.getQuantity(0.122, IMPEDANCE_PER_LENGTH),
        Quantities.getQuantity(0.122522, IMPEDANCE_PER_LENGTH),
        Quantities.getQuantity(652d, ELECTRIC_CURRENT_MAGNITUDE),
        Quantities.getQuantity(20d, RATED_VOLTAGE_MAGNITUDE)
      )
    )
  )

  val dcLineTypes = Map(
    "dc line type" -> ConversionPair(
      DCLineType(
        "dc line type",
        BigDecimal("0"),
        BigDecimal("0"),
        BigDecimal("0"),
        Some(BigDecimal("0")),
        Some(BigDecimal("0")),
        Some(BigDecimal("0")),
        Some(BigDecimal("0")),
        Some(BigDecimal("0"))
      ),
      null
    ),
    "dcline 1_type" -> ConversionPair(
      DCLineType(
        "dcline 1_type",
        BigDecimal("0"),
        BigDecimal("1.2"),
        BigDecimal("0"),
        None,
        None,
        None,
        None,
        None
      ),
      null
    ),
    "dcline 2_type" -> ConversionPair(
      DCLineType(
        "dcline 2_type",
        BigDecimal("0"),
        BigDecimal("1.2"),
        BigDecimal("0"),
        Some(BigDecimal("100.1")),
        Some(BigDecimal("-50.5")),
        Some(BigDecimal("50.5")),
        Some(BigDecimal("-25.6")),
        Some(BigDecimal("25.6"))
      ),
      null
    )
  )

  def getACLineTypes(key: String): (ACLineType, LineTypeInput) =
    acLineTypes
      .getOrElse(
        key,
        throw TestingException(
          s"Cannot find input / result pair for ${ACLineType.getClass.getSimpleName} $key."
        )
      )
      .getPair

  def getDCLineTypes(key: String): (DCLineType, LineTypeInput) =
    dcLineTypes
      .getOrElse(
        key,
        throw TestingException(
          s"Cannot find input / result pair for ${DCLineType.getClass.getSimpleName} $key."
        )
      )
      .getPair

  val lines = Map(
    "ac line" -> ConversionPair(
      ACLine(
        "ac line",
        getNodePair("slack_node_0")._1,
        getNodePair("node_0")._1,
        getACLineTypes("NAYY 4x150SE 0.6/1kV")._1,
        BigDecimal("100"),
        BigDecimal("120"),
        "subnet 1",
        7
      ),
      new LineInput(
        UUID.randomUUID(),
        "ac line",
        OperatorInput.NO_OPERATOR_ASSIGNED,
        OperationTime.notLimited(),
        getNodePair("slack_node_0")._2,
        getNodePair("node_0")._2,
        1,
        getACLineTypes("NAYY 4x150SE 0.6/1kV")._2,
        Quantities.getQuantity(100d, KILOMETRE),
        GridAndGeoUtils.buildSafeLineStringBetweenNodes(
          getNodePair("slack_node_0")._2,
          getNodePair("node_0")._2
        ),
        OlmCharacteristicInput.CONSTANT_CHARACTERISTIC
      )
    )
  )

  def getLinePair(key: String): (Line[_ <: LineType], LineInput) =
    lines
      .getOrElse(
        key,
        throw TestingException(
          s"Cannot find input / result pair for ${Line.getClass.getSimpleName} $key."
        )
      )
      .getPair

  val transformerTypes = Map(
    "test type" -> ConversionPair(
      Transformer2WType(
        id = "test type",
        sR = BigDecimal("40"),
        vmHV = BigDecimal("110"),
        vmLV = BigDecimal("10"),
        va0 = BigDecimal("150"),
        vmImp = BigDecimal("5"),
        pCu = BigDecimal("6"),
        pFe = BigDecimal("10"),
        iNoLoad = BigDecimal("1"),
        tapable = true,
        tapside = HV,
        dVm = BigDecimal("2.5"),
        dVa = BigDecimal("5"),
        tapNeutr = 0,
        tapMin = -10,
        tapMax = 10
      ),
      new Transformer2WTypeInput(
        UUID.randomUUID(),
        "test type",
        Quantities.getQuantity(45.375, MetricPrefix.MILLI(OHM)),
        Quantities.getQuantity(15.1249319, OHM),
        Quantities.getQuantity(40000d, KILOVOLTAMPERE),
        Quantities.getQuantity(110d, KILOVOLT),
        Quantities.getQuantity(10d, KILOVOLT),
        Quantities.getQuantity(2480.5790, MetricPrefix.NANO(SIEMENS)),
        Quantities
          .getQuantity(32972.94113, MetricPrefix.NANO(SIEMENS))
          .to(MetricPrefix.NANO(SIEMENS)),
        Quantities.getQuantity(2.5, PERCENT),
        Quantities.getQuantity(5d, DEGREE_GEOM),
        false,
        0,
        10,
        -10
      )
    )
  )

  def getTransformer2WTypePair(
      key: String
  ): (Transformer2WType, Transformer2WTypeInput) =
    transformerTypes
      .getOrElse(
        key,
        throw TestingException(
          s"Cannot find input / result pair for ${Transformer2WType.getClass.getSimpleName} $key."
        )
      )
      .getPair

  val transformers2w = Map(
    "MV1.101-LV1.101-Trafo 1" -> ConversionPair(
      Transformer2W(
        "MV1.101-LV1.101-Trafo 1",
        getNodePair("MV1.101 Bus 4")._1,
        getNodePair("LV1.101 Bus 4")._1,
        getTransformer2WTypePair("test type")._1,
        10,
        autoTap = true,
        Some(HV),
        BigDecimal("100"),
        None,
        "LV1.101",
        6
      ),
      new Transformer2WInput(
        UUID.randomUUID(),
        "MV1.101-LV1.101-Trafo 1",
        OperatorInput.NO_OPERATOR_ASSIGNED,
        OperationTime.notLimited(),
        getNodePair("MV1.101 Bus 4")._2,
        getNodePair("LV1.101 Bus 4")._2,
        1,
        getTransformer2WTypePair("test type")._2,
        10,
        true
      )
    )
  )

  def getTransformer2WPair(
      key: String
  ): (Transformer2W, Transformer2WInput) =
    transformers2w
      .getOrElse(
        key,
        throw TestingException(
          s"Cannot find input / result pair for ${Transformer2W.getClass.getSimpleName} $key."
        )
      )
      .getPair

  val switches = Map(
    "LV1.101 Switch 1" -> ConversionPair(
      Switch(
        "LV1.101 Switch 1",
        getNodePair("LV1.101 Bus 1")._1,
        getNodePair("LV1.101 Bus 4")._1,
        SwitchType.LoadSwitch,
        cond = true,
        Some(Substation("substation_1", "LV1.101", 7)),
        "LV1.101",
        7
      ),
      new SwitchInput(
        UUID.randomUUID(),
        "LV1.101 Switch 1",
        OperatorInput.NO_OPERATOR_ASSIGNED,
        OperationTime.notLimited(),
        getNodePair("LV1.101 Bus 1")._2,
        getNodePair("LV1.101 Bus 4")._2,
        true
      )
    )
  )

  def getSwitchPair(key: String): (Switch, SwitchInput) =
    switches
      .getOrElse(
        key,
        throw TestingException(
          s"Cannot find input / result pair for ${Switch.getClass.getSimpleName} $key."
        )
      )
      .getPair

  val loadProfiles = Map(
    "test profile" ->
      LoadProfile(
        "test profile",
        LoadProfileType.L2A,
        Map(
          TestTimeUtils.international
            .toZonedDateTime("1990-01-01 00:00:00") -> (BigDecimal(
            "0.75"
          ), BigDecimal(
            "0.85"
          )),
          TestTimeUtils.international
            .toZonedDateTime("1990-01-01 00:15:00") -> (BigDecimal(
            "0.55"
          ), BigDecimal(
            "0.75"
          )),
          TestTimeUtils.international
            .toZonedDateTime("1990-01-01 00:30:00") -> (BigDecimal(
            "0.35"
          ), BigDecimal(
            "0.65"
          )),
          TestTimeUtils.international
            .toZonedDateTime("1990-01-01 00:45:00") -> (BigDecimal(
            "0.15"
          ), BigDecimal(
            "0.55"
          ))
        )
      )
  )

  def getLoadProfile(key: String): LoadProfile =
    loadProfiles
      .getOrElse(
        key,
        throw TestingException(
          s"Cannot find input / result pair for ${LoadProfile.getClass.getSimpleName} $key."
        )
      )

  val loads = Map(
    "LV1.101 Load 8" -> ConversionPair(
      Load(
        "LV1.101 Load 8",
        getNodePair("LV1.101 Bus 1")._1,
        LoadProfileType.L2A,
        BigDecimal("0.014"),
        BigDecimal("0.005533"),
        BigDecimal("0.0150538"), // cosphi = 0.93
        "LV1.101",
        7
      ),
      new LoadInput(
        UUID.randomUUID(),
        "LV1.101 Load 8",
        OperatorInput.NO_OPERATOR_ASSIGNED,
        OperationTime.notLimited(),
        getNodePair("LV1.101 Bus 1")._2,
        new CosPhiFixed("cosPhiFixed:{(0.0,0.93)}"),
        null,
        DefaultLoadProfiles.NO_LOAD_PROFILE,
        Quantities.getQuantity(0d, KILOWATTHOUR),
        Quantities.getQuantity(15.0538, KILOVOLTAMPERE),
        0.93
      )
    )
  )

  def getLoadPair(key: String): (Load, LoadInput) =
    loads
      .getOrElse(
        key,
        throw TestingException(
          s"Cannot find input / result pair for ${Load.getClass.getSimpleName} $key."
        )
      )
      .getPair

  val measurements
      : Map[String, ConversionPair[Measurement, MeasurementUnitInput]] = Map(
    "MV1.102 Measurement 1" -> ConversionPair(
      NodeMeasurement(
        "MV1.102 Measurement 1",
        getNodePair("HV1 Bus 30")._1,
        Voltage,
        "HV1_MV1.102",
        3
      ),
      new MeasurementUnitInput(
        UUID.randomUUID(),
        "MV1.102 Measurement 1",
        OperatorInput.NO_OPERATOR_ASSIGNED,
        OperationTime.notLimited(),
        getNodePair("HV1 Bus 30")._2,
        true,
        true,
        false,
        false
      )
    ),
    "MV1.102 Measurement 3" -> ConversionPair(
      LineMeasurement(
        "ac line measurement",
        getLinePair("ac line")._1,
        getNodePair("slack_node_0")._1,
        MeasurementVariable.ActivePower,
        "subnet 1",
        7
      ),
      null
    ),
    "MV1.102 Measurement 28" -> ConversionPair(
      TransformerMeasurement(
        "MV1.101-LV1.101-Trafo 1 measurement",
        getTransformer2WPair("MV1.101-LV1.101-Trafo 1")._1,
        getNodePair("LV1.101 Bus 4")._1,
        MeasurementVariable.Current,
        "LV1.101",
        6
      ),
      null
    )
  )

  def getMeasurementPair(key: String): (Measurement, MeasurementUnitInput) =
    measurements
      .getOrElse(
        key,
        throw TestingException(
          s"Cannot find input / result pair for ${Measurement.getClass.getSimpleName} $key."
        )
      )
      .getPair

  val powerPlants = Map(
    "EHV Gen 1" -> ConversionPair(
      PowerPlant(
        "EHV Gen 1",
        getNodePair("EHV Bus 177")._1,
        Lignite,
        PowerPlantProfile1,
        PVm,
        BigDecimal("1"),
        BigDecimal("297"),
        None,
        BigDecimal("312.632"),
        BigDecimal("50"),
        BigDecimal("297"),
        BigDecimal("-97.6192"),
        BigDecimal("97.6192"),
        "EHV1",
        1
      ),
      new FixedFeedInInput(
        UUID.randomUUID(),
        "EHV Gen 1",
        OperatorInput.NO_OPERATOR_ASSIGNED,
        OperationTime.notLimited(),
        getNodePair("EHV Bus 177")._2,
        new CosPhiFixed("cosPhiFixed:{(0.0,1.0)}"),
        null,
        Quantities.getQuantity(312.632, MEGAVOLTAMPERE),
        1.0
      )
    ),
    "EHV Gen 1_withQ" -> ConversionPair(
      PowerPlant(
        "EHV Gen 1",
        getNodePair("EHV Bus 177")._1,
        Lignite,
        PowerPlantProfile1,
        PVm,
        BigDecimal("1"),
        BigDecimal("297"),
        Some(BigDecimal("97.61")),
        BigDecimal("312.632"),
        BigDecimal("50"),
        BigDecimal("297"),
        BigDecimal("-97.6192"),
        BigDecimal("97.6192"),
        "EHV1",
        1
      ),
      new FixedFeedInInput(
        UUID.randomUUID(),
        "EHV Gen 1",
        OperatorInput.NO_OPERATOR_ASSIGNED,
        OperationTime.notLimited(),
        getNodePair("EHV Bus 177")._2,
        new CosPhiFixed("cosPhiFixed:{(0.0,0.95)}"),
        null,
        Quantities.getQuantity(312.632, MEGAVOLTAMPERE),
        0.95
      )
    )
  )

  def getPowerPlantPair(key: String): (PowerPlant, FixedFeedInInput) =
    powerPlants
      .getOrElse(
        key,
        throw TestingException(
          s"Cannot find input / result pair for ${PowerPlant.getClass.getSimpleName} $key."
        )
      )
      .getPair

  val res = Map(
    "MV1.101 SGen 2" -> ConversionPair(
      RES(
        "MV1.101 SGen 2",
        getNodePair("MV1.101 Bus 4")._1,
        ResType.LvRes,
        ResProfileType.LvRural1,
        CalculationType.PQ,
        BigDecimal("0.16"),
        BigDecimal("0.0"),
        BigDecimal("0.16"),
        "MV1.101_LV1.101_eq",
        5
      ),
      new FixedFeedInInput(
        UUID.randomUUID(),
        "MV1.101 SGen 2_lv_res",
        OperatorInput.NO_OPERATOR_ASSIGNED,
        OperationTime.notLimited(),
        getNodePair("MV1.101 Bus 4")._2,
        new CosPhiFixed("cosPhiFixed:{(0.0,1.0)}"),
        null,
        Quantities.getQuantity(0.16, MEGAVOLTAMPERE),
        1.0
      )
    )
  )

  def getResPair(key: String): (RES, FixedFeedInInput) =
    res
      .getOrElse(
        key,
        throw TestingException(
          s"Cannot find input / result pair for ${RES.getClass.getSimpleName} $key."
        )
      )
      .getPair
}
