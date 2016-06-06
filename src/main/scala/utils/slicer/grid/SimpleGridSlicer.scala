package utils.slicer.grid

class SimpleGridSlicer() extends GridSlicer {

  def getLatCell(coord: Double, cellSize: Double): Int = {
    getCell(conf.getDouble("dropoff.lat.min"), cellSize, coord)
  }

  def getLonCell(coord: Double, cellSize: Double): Int = {
    getCell(conf.getDouble("dropoff.lon.max"), cellSize, coord)
  }

  private def getCell(lowerBoundary: Double, cellSize: Double, coord: Double): Int = {
    val max = Math.max(coord, lowerBoundary)
    val min = Math.min(coord, lowerBoundary)
    Math.floor(max - min / cellSize).toInt
  }

  override def getCellsForPoint(p: (Double, Double), cellSize: Double): (Int, Int) = {
    (
      getLatCell(p._1, cellSize),
      getLonCell(p._2, cellSize)
      )
  }
}
