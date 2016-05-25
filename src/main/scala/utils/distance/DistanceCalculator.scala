package utils.distance

trait DistanceCalculator extends Serializable {
  def calculate(PointA:(Double, Double), PointB:(Double, Double)):Double
}
