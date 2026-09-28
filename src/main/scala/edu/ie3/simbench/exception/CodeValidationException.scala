package edu.ie3.simbench.exception

final case class CodeValidationException(
    private val msg: String,
    private val cause: Throwable = null
) extends SimbenchException(msg, cause)
