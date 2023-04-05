package edu.ie3.simbench.exception

/** Simple "interface trait" so group the SimBench related Exceptions together
  */
class SimbenchException(
    private val msg: String,
    private val cause: Throwable = None.orNull
) extends Exception(msg, cause)
