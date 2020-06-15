package edu.ie3.simbench.util

import edu.ie3.util.StringUtils

abstract class ParsableEnumeration extends Enumeration {

  /**
    * Parses the given key String to an enumeration value. All non-word characters are removed from the key string and
    * converted to lower case.
    *
    * @param key Input key to parse
    * @return Matching enumeration value
    * @throws NoSuchElementException If no element with the given definition is apparent
    */
  def parse(key: String): Option[Value] =
    super.values.find(
      value =>
        StringUtils.cleanString(value.toString).toLowerCase == StringUtils
          .cleanString(key)
          .toLowerCase
    )
}
