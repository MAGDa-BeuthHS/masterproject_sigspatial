package utils.distance

class Haversine extends DistanceCalculator {

  /**
    * @see http://www.movable-type.co.uk/scripts/latlong.html
    *
    * @param pointA
    * @param pointB
    * @return Distance between the two points in km.
    */
  override def calculate(pointA: (Double, Double), pointB: (Double, Double)): Double = {
    val deltaLat = math.toRadians(pointB._1 - pointA._1)
    val deltaLong = math.toRadians(pointB._2 - pointA._2)
    val a = math.pow(math.sin(deltaLat / 2), 2) +
        math.cos(math.toRadians(pointA._1)) *
        math.cos(math.toRadians(pointB._1)) *
        math.pow(math.sin(deltaLong / 2), 2)
    val greatCircleDistance = 2 * math.atan2(math.sqrt(a), math.sqrt(1 - a))
    6371 * greatCircleDistance
  }
}
