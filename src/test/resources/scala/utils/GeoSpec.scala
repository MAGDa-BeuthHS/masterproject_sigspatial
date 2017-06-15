package utils

import org.scalatest.{Matchers, WordSpec}
import utils.math.Geo
import scala.math.floor

/**
  * Created class GeoSpec by Sebastian Urbanek on 05.06.16.
  */
class GeoSpec extends WordSpec with Matchers
{
  "Geo" should
  {
    // multiplied by 10,000,000 because of floating point imprecision
    "return 0.000761 for calculating deltaLat with deltaLng = 0.001 and lat = 40.477399" in {
      floor(Geo.calcDeltaLat(0.001f, 40.477399f)*10000000) shouldBe 7606
    }

    // multiplied by 10,000,000 because of floating point imprecision
    "return 0.002267 for calculating deltaLat with deltaLng = 0.003 and lat = 40.917577" in {
      floor(Geo.calcDeltaLat(0.003f, 40.917577f)*10000000) shouldBe 22669
    }
  }
}
