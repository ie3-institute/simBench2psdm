package edu.ie3.simbench.convert

import scala.math.{atan, cos, round, signum}

trait ShuntConverter {

  /**
    * Calculate the power factor based on active and reactive power, taking care of possible division by zero
    *
    * @param p Active power
    * @param q Reactive power
    * @return Power factor
    */
  def cosPhi(p: BigDecimal, q: BigDecimal): Double = {
    round({
      if (p == BigDecimal("0.0")) {
        if (q == BigDecimal("0.0"))
          1d
        else
          signum(q.doubleValue) * Double.MaxValue

      } else {
        cos(atan((q / p).doubleValue))
      }
    } * 100) / 100d
  }
}
