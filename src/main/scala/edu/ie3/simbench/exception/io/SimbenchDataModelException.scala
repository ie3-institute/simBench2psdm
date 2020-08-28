/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.simbench.exception.io

import edu.ie3.simbench.exception.SimbenchException

final case class SimbenchDataModelException(
    private val msg: String,
    private val cause: Throwable = None.orNull
) extends SimbenchException(msg, cause)
