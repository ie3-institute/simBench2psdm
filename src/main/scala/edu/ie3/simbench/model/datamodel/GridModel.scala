/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.simbench.model.datamodel

import edu.ie3.simbench.model.datamodel.profiles.{
  LoadProfile,
  PowerPlantProfile,
  ResProfile,
  StorageProfile
}
import edu.ie3.simbench.model.datamodel.types.LineType

final case class GridModel(
    simbenchCode: String,
    externalNets: Vector[ExternalNet],
    lines: Vector[Line[_ <: LineType]],
    loads: Vector[Load],
    loadProfiles: Vector[LoadProfile],
    measurements: Vector[Measurement],
    nodes: Vector[Node],
    nodePFResults: Vector[NodePFResult],
    powerPlants: Vector[PowerPlant],
    powerPlantProfiles: Vector[PowerPlantProfile],
    res: Vector[RES],
    resProfiles: Vector[ResProfile],
    shunts: Vector[Shunt],
    storages: Vector[Storage],
    storageProfiles: Vector[StorageProfile],
    studyCases: Vector[StudyCase],
    substations: Vector[Substation],
    switches: Vector[Switch],
    transformers2w: Vector[Transformer2W],
    transformers3w: Vector[Transformer3W]
)
