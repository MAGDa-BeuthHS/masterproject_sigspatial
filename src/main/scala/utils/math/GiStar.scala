package utils.math

import scala.math.sqrt

object GiStar extends Serializable {

  /*
  x: List of all cells
  n: Number of all cells --> Length of x
  avg: Average value of all cells
  s: Standart Deviation of cell set
  W: List of all neighbours attributes
   */

  def calcZ(w: List[Int], n: Long, avg: Double, s: Double): Double = {
    val numerator = w.sum - (avg * w.length)
    val den1 = w.length.toDouble * (n.toDouble - w.length) * s
    val denominator = sqrt(den1 / (n - 1).toDouble)
    numerator / denominator
  }

  def calcP(z: Double): Double = {
    0.0 * z
  }

  def calcStdDeviation(x: List[Int]): Double = {
    val sumX2 = calcSumXToPowerOf2(x)
    val avg = calcAverageValue(x)
    calcStdDeviation(x.length, avg, sumX2)
  }

  def calcStdDeviation(n: Long, avg: Double, sumX2: Double): Double = {
    // returns s^2 and not s!
    (sumX2 / n.toDouble) - (avg * avg)
  }

  def calcStdDeviation(x: List[Int], avg: Double): Double = {
    val sumX2 = calcSumXToPowerOf2(x)
    calcStdDeviation(x.length, avg, sumX2)
  }

  def calcStdDeviation(x: List[Int], sumX2: Long): Double = {
    val avg = calcAverageValue(x)
    calcStdDeviation(x.length, avg, sumX2)
  }

  def calcAverageValue(x: List[Int]): Double = {
    x.sum / x.length.toDouble
  }

  def calcSumWToPowerOf2(w: List[Int]): Long = {
    calcSumXToPowerOf2(w)
  }

  def calcSumXToPowerOf2(x: List[Int]): Long = {
    var i = 0
    var sum = 0
    while (i < x.length) {
      sum += x(i) * x(i)
      i += 1
    }
    sum
  }

//    def calcZWithFloat(w: List[Float], n: Long, avg: Float, stdDeviation: Float): Float =
//    {
//      val numerator   = w.sum - (avg * w.length)
//      val denominator = sqrt((w.length * (n - w.length) * stdDeviation) / (n - 1)).toFloat
//      numerator / denominator
//    }
//
//    def calcStdDeviationWithFloat(x: List[Float]): Float =
//    {
//      val sumX2 = calcSumXToPowerOf2WithFloat(x)
//      val avg = calcAverageValueWithFloat(x)
//      calcStdDeviationWithFloat(x.length, avg, sumX2)
//    }
//
//    def calcStdDeviationWithFloat(n: Long, avg: Float, sumX2: Float): Float =
//    {
//      (sumX2 / n.toFloat) - (avg * avg)
//    }
//
//    def calcAverageValueWithFloat(x: List[Float]): Float =
//    {
//      x.sum / x.length.toFloat
//    }
//
//    def calcSumXToPowerOf2WithFloat(x: List[Float]): Float =
//    {
//      var i = 0
//      var sum = 0f
//      while (i < x.length) {
//        sum += x(i) * x(i)
//        i += 1
//      }
//      sum
//    }
}