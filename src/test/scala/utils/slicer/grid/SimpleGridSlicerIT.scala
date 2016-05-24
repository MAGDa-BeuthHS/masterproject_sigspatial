package utils.slicer.grid

import com.typesafe.config.ConfigFactory
import org.scalatest.{Matchers, FlatSpec}
import utils.distance.Haversine

class SimpleGridSlicerIT extends FlatSpec with Matchers {
  val conf = ConfigFactory.load()

  val slicer = new SimpleGridSlicer(new Haversine)

  "The simple grid slicer" should "place the lower left boundary in Cell 0" in {
    slicer.getCellsForPoint((conf.getDouble("dropoff.lat.min"),conf.getDouble("dropoff.lon.min"))).shouldBe(0, 0)
  }
  it should "place the upper right boundary in Cell 200" in {
    slicer.getCellsForPoint((conf.getDouble("dropoff.lat.max"),conf.getDouble("dropoff.lon.max"))).shouldBe(244, 234)
  }
  it should "calculate single parts correctly" in {
    slicer.getCellsForPoint((conf.getDouble("dropoff.lat.max"),conf.getDouble("dropoff.lon.min"))).shouldBe(244, 0)
    slicer.getCellsForPoint((conf.getDouble("dropoff.lat.min"),conf.getDouble("dropoff.lon.max"))).shouldBe(0, 236)
  }
  it should "place nearby coords in the same cell" in {}
  it should "consider cell borders" in {}
}
