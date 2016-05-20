package utils.slicer.time

import java.text.SimpleDateFormat

import org.joda.time.{DateTime, Duration}

class SimpleTimeSlicer extends TimeSlicer {

  val formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
  val zero = new DateTime("2015-01-01T00:00:00.000-00:00")

  override def getSliceForTimestamp(timestampInCsv: String): Int = {
    translateTimestamp(new DateTime(formatter.parse(timestampInCsv)))
  }

  def translateTimestamp(dt: DateTime, zero: DateTime = zero): Int = {
    ((roundDateTime(dt, Duration.standardHours(2)).getMillis - zero.getMillis) / Duration.standardHours(2).getMillis).toInt
  }

  def roundDateTime(t: DateTime, d: Duration): DateTime = {
    t minus (t.getMillis - (t.getMillis.toDouble / d.getMillis).round * d.getMillis)
  }
}
