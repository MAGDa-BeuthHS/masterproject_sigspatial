package utils

import org.scalatest.{Matchers, WordSpec}
import utils.math.GiStar
import scala.math.round

class GiStarSpec extends WordSpec with Matchers
{
  val x = List(1, 2, 3, 4, 5, 6, 7)
  val w = List(4, 5, 6)
  "GiStar helper-functions" should
  {
    "return 4 for calcAvaerageValue(x)" in {
      GiStar.calcAverageValue(x) shouldBe 4
    }
    "return 140 for calcSumX2(x)" in {
      GiStar.calcSumXToPowerOf2(x) shouldBe 140
    }
    "return (s^2 =) 4 for calcStdDeviation(x)" in {
      GiStar.calcStdDeviation(x) shouldBe 4
    }
    "return 77 for calcSumW2(w)" in {
      GiStar.calcSumWToPowerOf2(w) shouldBe 77
    }
  }

  /*
  ---------------------------------------------------------------------------------
  |  1 |  1 |  1 |  5 |  0 |  0 |  0 |  1 |  0 |  0 |  0 |  0 |  0 |  0 |  3 |  2 |
  ---------------------------------------------------------------------------------
  |  0 |  3 |  0 |  0 |  6 |  1 |  0 |  1 |  1 |  0 |  0 |  0 |  0 |  0 |  1 |  3 |
  ---------------------------------------------------------------------------------
  |  5 |  0 |  0 |  0 |  0 |  1 |  9 |  5 |  0 |  0 |  3 |  0 |  0 |  1 |  0 |  1 |
  ---------------------------------------------------------------------------------
  |  1 |  4 |  0 |  2 |  0 |  5 |  0 |  0 |  0 |  1 |  1 |  0 |  0 |  0 |  0 |  2 |
  ---------------------------------------------------------------------------------
  |  1 |  0 |  2 |  3 |  0 |  3 |  6 |  0 |  1 |  2 |  0 |  0 |  0 |  1 |  5 |  0 |
  ---------------------------------------------------------------------------------
  |  3 |  5 |  0 |  4 |  0 |  0 |  0 |  2 |  1 |  2 |  1 |  1 |  0 |  0 |  1 |  0 |
  ---------------------------------------------------------------------------------
  |  0 |  0 |  1 |  1 |  8 |  1 |  6 |  6 |  2 |  2 |  0 |  1 |  0 |  1 |  2 |  0 |
  ---------------------------------------------------------------------------------
  |  0 |  2 |  2 |  2 |  4 |  6 | 12 |  9 |  2 |  2 |  3 |  6 |  2 |  0 |  0 |  2 |
  ---------------------------------------------------------------------------------
  |  0 |  0 |  3 |  8 |  5 |  1 |  2 |  1 |  1 |  1 |  5 |  0 |  0 |  0 |  2 |  2 |
  ---------------------------------------------------------------------------------
  |  1 |  2 |  4 |  2 |  1 |  0 |  1 |  0 |  1 |  3 |  0 |  0 |  2 |  3 |  0 |  2 |
  ---------------------------------------------------------------------------------
  |  4 |  4 |  1 |  0 |  0 |  1 |  1 |  1 |  0 |  2 |  1 |  4 |  2 |  1 |  6 |  4 |
  ---------------------------------------------------------------------------------
  |  1 |  1 |  0 |  0 |  0 |  0 |  0 |  0 |  1 |  4 |  5 |  2 |  2 |  6 |  1 |  0 |
  ---------------------------------------------------------------------------------
  |  0 |  0 |  0 |  2 |  0 |  0 |  1 |  0 |  2 |  6 |  1 |  3 |  0 |  4 |  0 |  0 |
  ---------------------------------------------------------------------------------
  |  1 |  1 |  0 |  0 |  0 |  0 |  0 |  0 |  0 |  2 |  0 |  0 | 13 |  0 |  0 |  0 |
  ---------------------------------------------------------------------------------
  |  0 |  0 |  0 |  1 |  1 |  0 |  0 |  0 |  1 |  4 |  6 |  0 |  2 |  0 |  0 |  0 |
  ---------------------------------------------------------------------------------
  |  0 |  8 |  2 |  6 |  0 |  0 |  0 |  4 |  3 |  1 |  4 |  7 |  0 |  0 |  0 |  0 |
  ---------------------------------------------------------------------------------
   */

  val X = List(
    1,1,1,5,0,0,0,1,0,0,0,0,0,0,3,2,
    0,3,0,0,6,1,0,1,1,0,0,0,0,0,1,3,
    5,0,0,0,0,1,9,5,0,0,3,0,0,1,0,1,
    1,4,0,2,0,5,0,0,0,1,1,0,0,0,0,2,
    1,0,2,3,0,3,6,0,1,2,0,0,0,1,5,0,
    3,5,0,4,0,0,0,2,1,2,1,1,0,0,1,0,
    0,0,1,1,8,1,6,6,2,2,0,1,0,1,2,0,
    0,2,2,2,4,6,12,9,2,2,3,6,2,0,0,2,
    0,0,3,8,5,1,2,1,1,1,5,0,0,0,2,2,
    1,2,4,2,1,0,1,0,1,3,0,0,2,3,0,2,
    4,4,1,0,0,1,1,1,0,2,1,4,2,1,6,4,
    1,1,0,0,0,0,0,0,1,4,5,2,2,6,1,0,
    0,0,0,2,0,0,1,0,2,6,1,3,0,4,0,0,
    1,1,0,0,0,0,0,0,0,2,0,0,13,0,0,0,
    0,0,0,1,1,0,0,0,1,4,6,0,2,0,0,0,
    0,8,2,6,0,0,0,4,3,1,4,7,0,0,0,0)

  "GiStar" should
  {
    "return z = 4,178523 for (8, 8) (central position with 8 neighbors)" in {
      val W = List(6, 6, 2, 12, 9, 2, 2, 1, 1)
      val n = X.length
      val avg = GiStar.calcAverageValue(X)
      val s = GiStar.calcStdDeviation(X)

      // multiplied by 1,000,000 because of floating point imprecision
      round(GiStar.calcZ(W, n, avg, s) * 1000000) shouldBe 4178523.0
    }

    "return z = -0.23129 for (8, 16) (border position with 5 neighbors)" in {
      val W = List(0, 0, 1, 0, 4, 3)
      val n = X.length
      val avg = GiStar.calcAverageValue(X)
      val s = GiStar.calcStdDeviation(X)

      round(GiStar.calcZ(W, n , avg, s) * 100000) shouldBe -23129.0
    }

    "return z = -0.26428 for (1, 1) (corner position with 3 neighbors)" in {
      val W = List(1, 1, 0, 3)
      val n = X.length
      val avg = GiStar.calcAverageValue(X)
      val s = GiStar.calcStdDeviation(X)

      round(GiStar.calcZ(W, n , avg, s) * 100000) shouldBe -26428.0
    }

    "return z = 0.213201 for (2, 3) (another position with 8 neighbors)" in {
      val W = List(0, 3, 0, 5, 0, 0, 1, 4, 0)
      val n = X.length
      val avg = GiStar.calcAverageValue(X)
      val s = GiStar.calcStdDeviation(X)

      round(GiStar.calcZ(W, n , avg, s) * 1000000) shouldBe 213201.0
    }
  }

//   Test-Case from the original paper by Getis and Ord
//    val X_o = List(-0.35f, -1.62f, -0.05f, 1.86f, 2.17f, 1.67f, 0.21f, -1.03f)
//    "GiStar" should
//      {
//        "return z = 1.9629 for 2.17 as j" in {
//          val W_o = List(-0.35f, 1.86f, 2.17f, 1.67f, 0.21f)
//          val n = X_o.length
//          val avg = GiStar.calcAverageValueWithFloat(X_o)
//          val s = GiStar.calcStdDeviationWithFloat(X_o)
//          GiStar.calcZWithFloat(W_o, n, avg, s) shouldBe 1.9629f
//        }
//
//        "return z = 1.8179 for 2.17 as j" in {
//          val W_o = List(2.17f, 1.67f)
//          val n = X_o.length
//          val avg = GiStar.calcAverageValueWithFloat(X_o)
//          val s = GiStar.calcStdDeviationWithFloat(X_o)
//          GiStar.calcZWithFloat(W_o, n, avg, s) shouldBe 1.8179f
//        }
//
//        "return z = 2.4078 for 2.17 as j" in {
//          val W_o = List(2.17f, 1.67f, 1.86f)
//          val n = X_o.length
//          val avg = GiStar.calcAverageValueWithFloat(X_o)
//          val s = GiStar.calcStdDeviationWithFloat(X_o)
//          GiStar.calcZWithFloat(W_o, n, avg, s) shouldBe 2.4078f
//        }
//      }
//  worked! (only with special float handling functions)
}