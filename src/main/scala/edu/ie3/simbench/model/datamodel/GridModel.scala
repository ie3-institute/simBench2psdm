package edu.ie3.simbench.model.datamodel

import edu.ie3.simbench.model.datamodel.profiles.{LoadProfile, PowerPlantProfile, ResProfile, StorageProfile}
import edu.ie3.simbench.model.datamodel.types.LineType

case class GridModel(
    externalNets: Vector[ExternalNet],
    lines: Vector[Line[_ <: LineType]],
    loads: Vector[Load],
    loadProfiles: Vector[LoadProfile],
    measurements: Vector[Measurement],
    nodes: Vector[Node],
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
