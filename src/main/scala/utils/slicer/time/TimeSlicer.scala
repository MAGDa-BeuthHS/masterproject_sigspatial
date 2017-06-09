package utils.slicer.time

import com.typesafe.config.ConfigFactory
import org.joda.time.DateTime

trait TimeSlicer extends Serializable {
  val conf = ConfigFactory.load()

  def getSlice(timestampInCsv: String, sliceSize: Long): Int
  def getTimestamp(slice: Int, sliceSize: Long): DateTime
  def getMaxSlice(sliceSize: Long): Int
}
