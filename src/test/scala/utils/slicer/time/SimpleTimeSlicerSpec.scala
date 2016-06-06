package utils.slicer.time

import java.text.SimpleDateFormat

import com.typesafe.config.ConfigFactory
import org.joda.time.{Duration, DateTime}
import org.scalatest.{FlatSpec, Matchers}

class SimpleTimeSlicerSpec extends FlatSpec with Matchers {
  val conf = ConfigFactory.load()
  val formatter = new SimpleDateFormat(conf.getString("app.datetimeformat"))
  val underTest = new SimpleTimeSlicer


  "The SimpleTimeSlicer" should "put the first timestamp in slice 0" in {
    underTest.getSliceForTimestamp(conf.getString("app.zero")).shouldBe(0)
  }

  it should "put 02.01.2015 in slice 12" in {
    val dt: DateTime = new DateTime(formatter.parse(conf.getString("app.zero")))
    val actual: String = formatter.format((dt plus Duration.standardDays(1)).toDate)
    val expected: Int = 12
    underTest.getSliceForTimestamp(actual).shouldBe(expected)
  }

  it should "put the last timestamp in slice x" in {
    val timeSlicesPerDay = 12
    val daysPerYear = 365
    val yearsInTotal = 1
    val numberOfLeapYears = 0

    // subtract 1, because time slices are zero based
    val expected: Int = timeSlicesPerDay * daysPerYear * yearsInTotal + numberOfLeapYears * timeSlicesPerDay - 1

    underTest.getSliceForTimestamp("2015-12-31 23:59:59").shouldBe(expected)
  }

  it should "return the correct DateTime for a slice" in {
    val ts: String = conf.getString("app.zero")
    val slice = underTest.getSliceForTimestamp(ts)
    val expected: DateTime = new DateTime(formatter.parse(ts))
    underTest.getTimestampForSlice(slice).shouldBe(expected)
  }

}
