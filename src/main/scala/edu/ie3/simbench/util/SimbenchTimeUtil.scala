package edu.ie3.simbench.util

import java.time.format.DateTimeFormatter
import java.time.{Instant, ZoneId, ZonedDateTime}
import java.util.{Locale, TimeZone}

import edu.ie3.util.TimeUtil

/**
  * This is a simple delegate object, that forwards all requests to a [[TimeUtil]] with the date format pattern 'dd.MM.yyyy HH:mm'
  */
case object SimbenchTimeUtil {
  private val timeUtil: TimeUtil =
    new TimeUtil(ZoneId.of("UTC"), Locale.GERMANY, "dd.MM.yyyy HH:mm")

  /**
    * Converts a given instant to a time string
    *
    * @param instant { @link Instant} to be converted to { @link String}
    * @return A String of the given Instant
    */
  def toString(instant: Instant): String = timeUtil.toString(instant)

  /**
    * Converts a given ZoneDateTime to a time string
    *
    * @param zonedDateTime { @link Instant} to be converted to { @link String}
    * @return A String of the given Instant
    */
  def toString(zonedDateTime: ZonedDateTime): String =
    timeUtil.toString(zonedDateTime)

  /**
    * Returns an ZoneDateTime based on a given String, which can be understood by the class private
    * DateTimeFormatter
    *
    * @param timeString { @link String} to convert to { @link Instant}
    * @return Instant object
    */
  def toZonedDateTime(timeString: String): ZonedDateTime =
    timeUtil.toZonedDateTime(timeString)

  def getZoneId: ZoneId = timeUtil.getZoneId

  def getTimeZone: TimeZone = timeUtil.getTimeZone

  def getLocale: Locale = timeUtil.getLocale

  def getDtfPattern: String = timeUtil.getDtfPattern

  def getDateTimeFormatter: DateTimeFormatter = timeUtil.getDateTimeFormatter
}
