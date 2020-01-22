package edu.ie3.test.common

import com.typesafe.scalalogging.LazyLogging
import org.scalatest.{Matchers, PrivateMethodTester, WordSpecLike}

trait UnitSpec
    extends WordSpecLike
    with Matchers
    with LazyLogging
    with PrivateMethodTester {}
