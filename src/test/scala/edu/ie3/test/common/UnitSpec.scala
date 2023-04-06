package edu.ie3.test.common

import com.typesafe.scalalogging.LazyLogging
import edu.ie3.util.quantities.QuantityAdjustments
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.PrivateMethodTester

trait UnitSpec
    extends AnyWordSpecLike
    with Matchers
    with LazyLogging
    with PrivateMethodTester {

  /** Adjust the quantity library to be able to "understand" the Scala number
    * system
    */
  QuantityAdjustments.adjust()
}
