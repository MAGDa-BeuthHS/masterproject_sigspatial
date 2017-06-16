package utils.slicer.grid

import com.typesafe.config.ConfigFactory
import org.scalatest.{FlatSpec, Matchers}


class SimpleGridSlicerSpec extends FlatSpec with Matchers {
  val conf = ConfigFactory.load()
  val slicer = new SimpleGridSlicer()

  "SimpleGridSlicer" should "do the right thing (lon)" in {
    slicer.getLonCell(-0.123, conf.getDouble("app.cellsize")).shouldBe(9)
    slicer.getLonCell(-1.123, conf.getDouble("app.cellsize")).shouldBe(8)
    slicer.getLonCell(-7.123, conf.getDouble("app.cellsize")).shouldBe(2)
  }

  it should "do the right thing (lat)" in {
    slicer.getLatCell(0.123, conf.getDouble("app.cellsize")).shouldBe(0)
    slicer.getLatCell(1.123, conf.getDouble("app.cellsize")).shouldBe(1)
    slicer.getLatCell(7.123, conf.getDouble("app.cellsize")).shouldBe(7)
  }

  it should "return null if an ordinate is out of bounds" in {
    slicer.getLatCell(11, conf.getDouble("app.cellsize")).shouldBe(None)
    slicer.getLonCell(-11, conf.getDouble("app.cellsize")).shouldBe(None)
    slicer.getCellsForPoint((5, -11), cellSize).shouldBe(None)
  }

  it should "place a point correctly" in {
    slicer.getCellsForPoint((0, 0), conf.getDouble("app.cellsize")).shouldBe((0, 0))
    slicer.getCellsForPoint((1, -1), conf.getDouble("app.cellsize")).shouldBe((1, 9))
    slicer.getCellsForPoint((9.2133214125122, -2.124124125325), conf.getDouble("app.cellsize")).shouldBe((9, 7))
  }

  it should "return the correct total amount of lat grid cells" in {
    slicer.getMaxLatCell(1).shouldBe(10)
    slicer.getMaxLatCell(0.1).shouldBe(100)
  }

  it should "return the correct total amount of lon grid cells" in {
    slicer.getMaxLonCell(1).shouldBe(10)
    slicer.getMaxLonCell(0.1).shouldBe(100)
  }

  it should "convert back and forth correctly" in {
    val cellSize: Double = 0.001
    val gridCells = slicer.getCellsForPoint((3.4724, -4.9342), cellSize)
    val centerPoints = slicer.getCenterPointForCell(gridCells, cellSize)
    val gridCellsForCenter = slicer.getCellsForPoint(centerPoints, cellSize)
    gridCells shouldBe (3472,5065)
    centerPoints shouldBe (3.47250000000000000000,-4.93450000000000000000)
    gridCellsForCenter shouldBe (3472,5065)
  }

}
