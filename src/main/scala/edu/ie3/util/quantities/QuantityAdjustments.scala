package edu.ie3.util.quantities

import tech.units.indriya.function.Calculus

case object QuantityAdjustments {

  /**
    * The [[tech.units.indriya.function.DefaultNumberSystem]] is only covering java [[Number]] children. As
    * [[BigDecimal]] is not related to [[java.math.BigDecimal]], this causes issues, why the
    * [[tech.units.indriya.spi.NumberSystem]] has to be to be used has to be specified to something, that actually is
    * able to handle the scala number system.
    */
  def adjust(): Unit =
    Calculus.setCurrentNumberSystem(
      Calculus.getNumberSystem("edu.ie3.util.quantities.ScalaNumberSystem")
    )
}
