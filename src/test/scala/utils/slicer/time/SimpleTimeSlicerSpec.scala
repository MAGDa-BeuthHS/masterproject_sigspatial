package utils.slicer.time

import java.text.SimpleDateFormat

import com.typesafe.config.ConfigFactory
import org.joda.time.{DateTime, Duration}
import org.scalatest.{FlatSpec, Matchers}

class SimpleTimeSlicerSpec extends FlatSpec with Matchers {
  val conf = ConfigFactory.load()
  val formatter = new SimpleDateFormat(conf.getString("app.datetimeformat"))
  val underTest = new SimpleTimeSlicer


  "The SimpleTimeSlicer" should "put the first timestamp in slice 0" in {
    underTest.getSliceForTimestamp(conf.getString("app.zero"), conf.getInt("app.timeslice")).shouldBe(0)
  }

  it should "put 03.01.2015 in slice 1" in {
    val dt: DateTime = new DateTime(formatter.parse(conf.getString("app.zero")))
    val actual: String = formatter.format((dt plus Duration.standardDays(2)).toDate)
    val expected: Int = 1
    underTest.getSliceForTimestamp(actual, conf.getInt("app.timeslice")).shouldBe(expected)
  }

  it should "put the last timestamp in slice x" in {
    val daysPerYear = 365
    val expected: Int = daysPerYear / conf.getInt("app.timeslice")
    underTest.getSliceForTimestamp("2015-12-31 23:59:59", conf.getInt("app.timeslice")).shouldBe(expected)
  }

  it should "return the correct DateTime for a slice" in {
    val ts: String = conf.getString("app.zero")
    val slice = underTest.getSliceForTimestamp(ts, conf.getInt("app.timeslice"))
    val expected: DateTime = new DateTime(formatter.parse(ts))
    underTest.getTimestampForSlice(slice, conf.getInt("app.timeslice")).shouldBe(expected)
  }

}
