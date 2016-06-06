package utils.slicer.time

import java.text.SimpleDateFormat

import org.joda.time.{DateTime, Duration}

class SimpleTimeSlicer extends TimeSlicer {

  val formatter = new SimpleDateFormat(conf.getString("app.datetimeformat"))
  val zero = new DateTime(formatter.parse(conf.getString("app.zero")))

  override def getSliceForTimestamp(timestampInCsv: String): Int = {
    translateTimestamp(new DateTime(formatter.parse(timestampInCsv)))
  }

  private def translateTimestamp(dt: DateTime, zero: DateTime = zero): Int = {
    ((roundDateTime(dt, Duration.standardHours(conf.getInt("app.timeslice"))).getMillis - zero.getMillis) / Duration.standardHours(2).getMillis).toInt
  }

  private def roundDateTime(t: DateTime, d: Duration): DateTime = {
    t minus (t.getMillis - (t.getMillis.toDouble / d.getMillis).round * d.getMillis)
  }

  override def getTimestampForSlice(slice: Int): DateTime = {
    zero plus Duration.standardHours(slice.toLong * conf.getInt("app.timeslice"))
  }
}
