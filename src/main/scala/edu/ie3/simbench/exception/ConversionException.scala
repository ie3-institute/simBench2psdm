package edu.ie3.simbench.exception

final case class ConversionException(
    private val msg: String,
    private val cause: Throwable = null
) extends SimbenchException(msg, cause)
