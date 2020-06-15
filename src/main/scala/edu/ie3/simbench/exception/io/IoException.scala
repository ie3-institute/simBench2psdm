package edu.ie3.simbench.exception.io

import edu.ie3.simbench.exception.SimbenchException

final case class IoException(
    private val msg: String,
    private val cause: Throwable = None.orNull
) extends SimbenchException(msg, cause)
