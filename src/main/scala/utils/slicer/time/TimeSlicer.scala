package utils.slicer.time

trait TimeSlicer extends Serializable {
  def getSliceForTimestamp(timestampInCsv: String): Int
}
