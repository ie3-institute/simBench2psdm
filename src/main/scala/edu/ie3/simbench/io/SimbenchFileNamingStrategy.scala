package edu.ie3.simbench.io

import edu.ie3.simbench.exception.io.SimbenchFileNamingException
import edu.ie3.simbench.model.datamodel.ExternalNet.{Simple, Ward, WardExtended}
import edu.ie3.simbench.model.datamodel.Line.{ACLine, DCLine}
import edu.ie3.simbench.model.datamodel.types.LineType.{ACLineType, DCLineType}
import edu.ie3.simbench.model.datamodel.types.{
  LineType,
  Transformer2WType,
  Transformer3WType
}
import edu.ie3.simbench.model.datamodel.{
  Coordinate,
  ExternalNet,
  Line,
  Load,
  Node,
  RES,
  SimbenchModel,
  StudyCase,
  Transformer2W,
  Transformer3W
}

import scala.util.{Failure, Success, Try}

/**
  * Describes a sophisticated mapping between classes and equivalent SimBench files
  */
case object SimbenchFileNamingStrategy {
  private def modelFileMapping: Map[Class[_ <: SimbenchModel], String] =
    Map[Class[_ <: SimbenchModel], String](
      classOf[Coordinate] -> "Coordinates",
      classOf[ExternalNet] -> "ExternalNet",
      classOf[Simple] -> "ExternalNet",
      classOf[Ward] -> "ExternalNet",
      classOf[WardExtended] -> "ExternalNet",
      classOf[Line[_]] -> "Line",
      classOf[ACLine] -> "Line",
      classOf[DCLine] -> "Line",
      classOf[LineType] -> "LineType",
      classOf[ACLineType] -> "LineType",
      classOf[DCLineType] -> "LineType",
      classOf[Load] -> "Load",
      classOf[Node] -> "Node",
      classOf[RES] -> "RES",
      classOf[StudyCase] -> "StudyCase",
      classOf[Transformer2W] -> "Transformer",
      classOf[Transformer2WType] -> "Transformer",
      classOf[Transformer3W] -> "Transformer3W",
      classOf[Transformer3WType] -> "Transformer3W"
    )
  // TODO: Add naming strategy for profiles + NodePFResult

  /**
    * Determine the file name based on the provided class
    *
    * @param clazz The class to determine the according file name for
    * @return [[scala.util.Success]], if the file name can be determine, [[scala.util.Failure]], if not
    */
  def getFileName(clazz: Class[_]): Try[String] = {
    if (classOf[SimbenchModel].isAssignableFrom(clazz)) {
      modelFileMapping.get(clazz.asSubclass(classOf[SimbenchModel])) match {
        case Some(filename) => Success(filename)
        case None =>
          Failure(new SimbenchFileNamingException(
            s"Cannot determine the filename for class ${clazz.getSimpleName}"))
      }
    } else
      Failure(
        new SimbenchFileNamingException(
          s"Cannot determine the filename for class ${clazz.getSimpleName}"))
  }
}
