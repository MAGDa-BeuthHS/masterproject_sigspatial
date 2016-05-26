package utils.slicer.time

import com.typesafe.config.ConfigFactory

trait TimeSlicer extends Serializable {
  val conf = ConfigFactory.load()

  def getSliceForTimestamp(timestampInCsv: String): Int
}
