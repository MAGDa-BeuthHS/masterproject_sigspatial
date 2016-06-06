package utils.slicer.time

import java.text.SimpleDateFormat

import org.joda.time.{DateTime, Duration}

class SimpleTimeSlicer extends TimeSlicer {

  val formatter = new SimpleDateFormat(conf.getString("app.datetimeformat"))
  val zero = new DateTime(formatter.parse(conf.getString("app.zero")))

  override def getSliceForTimestamp(timestampInCsv: String, sliceSize: Int): Int = {
    translateTimestamp(new DateTime(formatter.parse(timestampInCsv)), sliceSize)
  }

  private def translateTimestamp(dt: DateTime, sliceSize: Int, zero: DateTime = zero): Int = {
    ((roundDateTime(dt, Duration.standardDays(sliceSize)).getMillis - zero.getMillis) / Duration.standardDays(2).getMillis).toInt
  }

  private def roundDateTime(t: DateTime, d: Duration): DateTime = {
    t minus (t.getMillis - (t.getMillis.toDouble / d.getMillis).round * d.getMillis)
  }

  override def getTimestampForSlice(slice: Int, sliceSize: Int): DateTime = {
    zero plus Duration.standardHours(slice.toLong * sliceSize.toLong)
  }
}
