package utils.slicer.time

trait TimeSlicer {
  def getSliceForTimestamp(timestampInCsv: String): Int
}
