package utils.slicer.time

import org.scalatest.{FlatSpec, Matchers}

class SimpleTimeSlicerSpec extends FlatSpec with Matchers {

  val underTest = new SimpleTimeSlicer


  "The SimpleTimeSlicer" should "put the first timestamp in slice 0" in {
    underTest.getSliceForTimestamp("2009-01-01 00:00:00").shouldBe(0)
  }

  it should "put 01.02.2009 in slice 12" in {
    underTest.getSliceForTimestamp("2009-01-02 00:00:00").shouldBe(12)
  }

  it should "put the last timestamp in slice x" in {
    val timeSlicesPerDay = 12
    val daysPerYear = 365
    val yearsInTotal = 7
    val numberOfLeapYears = 1

    // subtract 1, because time slices are zero based
    val expected: Int = timeSlicesPerDay * daysPerYear * yearsInTotal + numberOfLeapYears * timeSlicesPerDay -1

    underTest.getSliceForTimestamp("2015-12-31 23:59:59").shouldBe(expected)
  }

}
