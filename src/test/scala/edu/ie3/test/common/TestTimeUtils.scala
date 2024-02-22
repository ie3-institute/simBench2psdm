package edu.ie3.test.common

import edu.ie3.util.TimeUtil

import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/** [[TimeUtil]]s that are useful for testing
  */
object TestTimeUtils {

  val simbench: TimeUtil = new TimeUtil(
    DateTimeFormatter
      .ofPattern("dd.MM.yyyy HH:mm")
      .withZone(ZoneId.of("UTC"))
      .withLocale(Locale.GERMANY)
  )

  val international: TimeUtil = new TimeUtil(
    DateTimeFormatter
      .ofPattern(TimeUtil.LOCAL_DATE_TIME_PATTERN)
      .withZone(ZoneId.of("UTC"))
      .withLocale(Locale.GERMANY)
  )

}
