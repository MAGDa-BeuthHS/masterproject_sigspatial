package utils.slicer.grid

import com.typesafe.config.ConfigFactory
import org.scalatest.{Matchers, FlatSpec}
import utils.distance.Haversine

class SimpleGridSlicerIT extends FlatSpec with Matchers {
  val conf = ConfigFactory.load()
  val DropoffLatMin: Double = conf.getLong("dropoff.lat.min")
  val DropoffLonMin: Double = conf.getLong("dropoff.lon.min")
  val DropoffLatMax: Double = conf.getLong("dropoff.lat.max")
  val DropoffLonMax: Double = conf.getLong("dropoff.lon.max")

  val slicer = new SimpleGridSlicer(new Haversine)

  "The simple grid slicer" should "place the lower left boundary in Cell 0" in {
    slicer.getCellForLat(DropoffLatMin).shouldBe(0)
    slicer.getCellForLon(DropoffLonMin).shouldBe(0)
  }
  it should "place the upper right boundary in Cell 200" in {
    slicer.getCellForLat(DropoffLatMax).shouldBe(200)
    slicer.getCellForLon(DropoffLonMax).shouldBe(200)
  }
  it should "place nearby coords in the same cell" in {}
  it should "consider cell borders" in {}
}
