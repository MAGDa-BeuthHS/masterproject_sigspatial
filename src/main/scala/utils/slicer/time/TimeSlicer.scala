package utils.slicer.time

import com.typesafe.config.ConfigFactory
import org.joda.time.DateTime

trait TimeSlicer extends Serializable {
  val conf = ConfigFactory.load()

  def getSliceForTimestamp(timestampInCsv: String): Int
  def getTimestampForSlice(slice: Int): DateTime

}
