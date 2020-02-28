package edu.ie3.simbench.exception

final case class ConversionException(private val msg: String,
                                     private val cause: Throwable = None.orNull)
    extends SimbenchException(msg, cause)
