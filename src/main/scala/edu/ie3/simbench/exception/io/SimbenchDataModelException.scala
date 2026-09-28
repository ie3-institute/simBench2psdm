package edu.ie3.simbench.exception.io

import edu.ie3.simbench.exception.SimbenchException

final case class SimbenchDataModelException(
    private val msg: String,
    private val cause: Throwable = null
) extends SimbenchException(msg, cause)
