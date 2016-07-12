package utils.slicer.grid

import com.typesafe.config.ConfigFactory
import org.scalatest.{FlatSpec, Matchers}


class SimpleGridSlicerSpec extends FlatSpec with Matchers {
  val conf = ConfigFactory.load()
  val slicer = new SimpleGridSlicer()

  "SimpleGridSlicer" should "do the right thing (lon)" in {
    slicer.getLonCell(-0.123, conf.getDouble("app.cellsize")).shouldBe(0)
    slicer.getLonCell(-1.123, conf.getDouble("app.cellsize")).shouldBe(1)
    slicer.getLonCell(-7.123, conf.getDouble("app.cellsize")).shouldBe(7)
  }

  it should "do the right thing (lat)" in {
    slicer.getLatCell(0.123, conf.getDouble("app.cellsize")).shouldBe(0)
    slicer.getLatCell(1.123, conf.getDouble("app.cellsize")).shouldBe(1)
    slicer.getLatCell(7.123, conf.getDouble("app.cellsize")).shouldBe(7)
  }

  it should "place coords correctly even if they're out of the boundaries" in {
    slicer.getLonCell(-11, conf.getDouble("app.cellsize")).shouldBe(11)
    slicer.getLatCell(11, conf.getDouble("app.cellsize")).shouldBe(11)
  }

  it should "place a point correctly" in {
    slicer.getCellsForPoint((0, 0), conf.getDouble("app.cellsize")).shouldBe((0, 0))
    slicer.getCellsForPoint((1, -1), conf.getDouble("app.cellsize")).shouldBe((1, 1))
    slicer.getCellsForPoint((9.2133214125122, -2.124124125325), conf.getDouble("app.cellsize")).shouldBe((9, 2))
  }

  it should "return the correct total amount of lat grid cells" in {
    slicer.getMaxLatCell(1).shouldBe(10)
    slicer.getMaxLatCell(0.1).shouldBe(100)
  }

  it should "return the correct total amount of lon grid cells" in {
    slicer.getMaxLonCell(1).shouldBe(10)
    slicer.getMaxLonCell(0.1).shouldBe(100)
  }

}
