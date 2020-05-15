package edu.ie3.simbench.io

import edu.ie3.simbench.exception.io.SimbenchFileNamingException
import edu.ie3.simbench.model.datamodel.ExternalNet.{Simple, Ward, WardExtended}
import edu.ie3.simbench.model.datamodel.Line.{ACLine, DCLine}
import edu.ie3.simbench.model.datamodel._
import edu.ie3.simbench.model.datamodel.profiles.{LoadProfile, PowerPlantProfile, ResProfile}
import edu.ie3.simbench.model.datamodel.types.LineType.{ACLineType, DCLineType}
import edu.ie3.simbench.model.datamodel.types.{LineType, Transformer2WType, Transformer3WType}

import scala.util.{Failure, Success, Try}

/**
  * Describes a sophisticated mapping between classes and equivalent SimBench files
  */
case object SimbenchFileNamingStrategy {
  private def fileMapping: Map[Class[_], String] =
    Map(
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
      classOf[StudyCase] -> "StudyCases",
      classOf[Transformer2W] -> "Transformer",
      classOf[Transformer2WType] -> "TransformerType",
      classOf[Transformer3W] -> "Transformer3W",
      classOf[Transformer3WType] -> "Transformer3WType",
      classOf[LoadProfile] -> "LoadProfile",
      classOf[ResProfile] -> "RESProfile",
      classOf[PowerPlantProfile] -> "PowerPlantProfile"
    )

  /**
    * Determine the file name based on the provided class
    *
    * @param clazz The class to determine the according file name for
    * @return [[scala.util.Success]], if the file name can be determine, [[scala.util.Failure]], if not
    */
  def getFileName(clazz: Class[_]): Try[String] = {
    fileMapping.get(clazz) match {
      case Some(filename) => Success(filename)
      case None =>
        Failure(
          new SimbenchFileNamingException(
            s"Cannot determine the filename for class ${clazz.getSimpleName}"
          )
        )
    }
  }
}
