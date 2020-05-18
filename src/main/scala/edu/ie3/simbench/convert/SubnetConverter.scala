package edu.ie3.simbench.convert

/**
  * Converting the String type subnet information in Simbench data set to Int type subnet information in
  * PowerSystemDataModel. The mapping is made by alphabetically sorting all available sub nets and counting from 1 onwards.
  *
  * @param simbenchSubnets Vector of known Simbench sub nets
  */
final case class SubnetConverter(simbenchSubnets: Vector[String]) {
  val mapping: Map[String, Int] =
    simbenchSubnets.distinct.sorted.zipWithIndex
      .map(entry => (entry._1, entry._2 + 1))
      .toMap

  /**
    * Get the converted subnet as Int
    *
    * @param simbenchSubnet Subnet information from SimBench data set
    * @return Int representation of the SimBench sub net
    */
  def convert(simbenchSubnet: String): Int =
    mapping.getOrElse(
      simbenchSubnet,
      throw new IllegalArgumentException(
        s"The simbench subnet $simbenchSubnet has not been initialized with the converter."
      )
    )
}
