package utils.distance

trait DistanceCalculator {
  def calculate(PointA:(Double, Double), PointB:(Double, Double)):Double
}
