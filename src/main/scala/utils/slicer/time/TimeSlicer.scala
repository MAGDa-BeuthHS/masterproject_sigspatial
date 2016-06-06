package utils.slicer.time

import com.typesafe.config.ConfigFactory
import org.joda.time.DateTime

trait TimeSlicer extends Serializable {
  val conf = ConfigFactory.load()

  def getSliceForTimestamp(timestampInCsv: String, sliceSize: Int): Int

  def getTimestampForSlice(slice: Int, sliceSize: Int): DateTime

}
