package edu.ie3.simbench.io

import edu.ie3.simbench.config.SimbenchConfig

final case class ParticipantToInput(
    pvInput: Boolean,
    wecInput: Boolean = false
)

object ParticipantToInput {

  def apply(cfg: SimbenchConfig.Conversion): ParticipantToInput =
    ParticipantToInput(
      cfg.convertPv
    )
}
