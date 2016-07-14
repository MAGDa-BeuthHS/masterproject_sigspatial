package utils.slicer.time

import java.text.SimpleDateFormat

import org.joda.time.{DateTime, Days, Duration}

class SimpleTimeSlicer extends TimeSlicer {

  val formatter = new SimpleDateFormat(conf.getString("app.datetimeformat"))
  val zero = new DateTime(formatter.parse(conf.getString("app.zero")))
  val max = new DateTime(formatter.parse(conf.getString("app.max")))

  override def getSliceForTimestamp(timestampInCsv: String, sliceSize: Double): Int = {
    translateTimestamp(new DateTime(formatter.parse(timestampInCsv)), sliceSize)
  }

  private def translateTimestamp(dt: DateTime, sliceSize: Double, zero: DateTime = zero): Int = {
    ((roundDateTime(dt, Duration.standardHours((sliceSize * 24).toLong)).getMillis - zero.getMillis) / Duration.standardDays(2).getMillis).toInt
  }

  private def roundDateTime(t: DateTime, d: Duration): DateTime = {
    t minus (t.getMillis - (t.getMillis.toDouble / d.getMillis).round * d.getMillis)
  }

  override def getTimestampForSlice(slice: Int, sliceSize: Int): DateTime = {
    zero plus Duration.standardHours(slice.toLong * sliceSize.toLong)
  }

  override def getMaxSlice(sliceSize: Double): Int = {
    (Days.daysBetween(zero.withTimeAtStartOfDay(), max.withTimeAtStartOfDay()).getDays.toDouble / sliceSize).toInt
  }
}
