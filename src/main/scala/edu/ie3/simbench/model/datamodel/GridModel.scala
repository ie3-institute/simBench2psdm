package edu.ie3.simbench.model.datamodel

case class GridModel(externalNets: Vector[ExternalNet],
                     lines: Vector[Line[_]],
                     loads: Vector[Load],
                     measurements: Vector[Measurement],
                     nodes: Vector[Node],
                     powerPlants: Vector[PowerPlant],
                     res: Vector[RES],
                     shunts: Vector[Shunt],
                     storages: Vector[Storage],
                     studyCases: Vector[StudyCase],
                     substations: Vector[Substation],
                     switches: Vector[Switch],
                     transofmers2w: Vector[Transformer2W],
                     transformers3w: Vector[Transformer3W])

case object GridModel {
  def apply() =
    new GridModel(
      Vector.empty,
      Vector.empty,
      Vector.empty,
      Vector.empty,
      Vector.empty,
      Vector.empty,
      Vector.empty,
      Vector.empty,
      Vector.empty,
      Vector.empty,
      Vector.empty,
      Vector.empty,
      Vector.empty,
      Vector.empty
    )
}
