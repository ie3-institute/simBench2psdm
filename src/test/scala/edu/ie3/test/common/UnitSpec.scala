/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.test.common

import com.typesafe.scalalogging.LazyLogging
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.PrivateMethodTester

trait UnitSpec
    extends AnyWordSpecLike
    with Matchers
    with LazyLogging
    with PrivateMethodTester
