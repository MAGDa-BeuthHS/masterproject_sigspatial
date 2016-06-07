package utils.math

import scala.math.{Pi, cos}

object Geo
{
  def calcDeltaLat(deltaLng: Float, lat: Float): Float =
  {
    calcDistLng(deltaLng, lat) / 111300
  }

  def calcDistLng(deltaLng: Float, lat: Float): Float =
  {
    (111300 * cos(lat * (Pi/180)) * deltaLng).toFloat
  }
}