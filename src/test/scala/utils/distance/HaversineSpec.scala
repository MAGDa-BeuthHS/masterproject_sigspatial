package utils.distance

import org.scalatest.{FlatSpec, Matchers}

class HaversineSpec extends FlatSpec with Matchers {
  // Hardcode values here to not use ConfigLoader (remember: this is ut not it)
  val DropoffLatMin: Double = 40.477399
  val DropoffLonMin: Double = -74.25909
  val DropoffLatMax: Double = 40.917577
  val DropoffLonMax: Double = -73.70009
  val calc = new Haversine

  "The haversine distance calculator" should "calculate the size of the bounding box correctly" in {
    BigDecimal(calc.calculate((DropoffLatMin, DropoffLonMin), (DropoffLatMax, DropoffLonMin))).setScale(2, BigDecimal.RoundingMode.HALF_UP).shouldBe(48.95) // W
    BigDecimal(calc.calculate((DropoffLatMax, DropoffLonMin), (DropoffLatMax, DropoffLonMax))).setScale(2, BigDecimal.RoundingMode.HALF_UP).shouldBe(46.97) // N
    BigDecimal(calc.calculate((DropoffLatMin, DropoffLonMax), (DropoffLatMax, DropoffLonMax))).setScale(2, BigDecimal.RoundingMode.HALF_UP).shouldBe(48.95) // E
    BigDecimal(calc.calculate((DropoffLatMin, DropoffLonMin), (DropoffLatMin, DropoffLonMax))).setScale(2, BigDecimal.RoundingMode.HALF_UP).shouldBe(47.28) // S
  }
}
