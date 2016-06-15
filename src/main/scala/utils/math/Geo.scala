package utils.math

import scala.math.{Pi, cos}

object Geo extends Serializable {
  val aLat = 111300

  def calcDeltaLat(deltaLng: Float, lat: Float): Float = {
    calcDistLng(deltaLng, lat) / aLat
  }

  def calcDistLng(deltaLng: Float, lat: Float): Float = {
    (aLat * cos(lat * (Pi / 180)) * deltaLng).toFloat
  }
}