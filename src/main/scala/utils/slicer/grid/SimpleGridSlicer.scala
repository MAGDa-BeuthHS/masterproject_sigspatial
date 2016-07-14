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
    Math.floor((max - min) / cellSize).toInt
  }

  override def getCellsForPoint(p: (Double, Double), cellSize: Double): (Int, Int) = {
    (
      getLatCell(p._1, cellSize),
      getLonCell(p._2, cellSize)
      )
  }

  override def getMaxLatCell(cellSize: Double): Int = {
    ((conf.getDouble("dropoff.lat.max") - conf.getDouble("dropoff.lat.min")) / cellSize).toInt
  }

  override def getMaxLonCell(cellSize: Double): Int = {
    ((conf.getDouble("dropoff.lon.max") - conf.getDouble("dropoff.lon.min")) / cellSize).toInt
  }

  override def getPointForCells(p: (Int, Int), cellSize: Double): (Double, Double) = {
    (conf.getDouble("dropoff.lat.min") + p._1 * cellSize, conf.getDouble("dropoff.lon.max") - p._2 * cellSize)
  }
}
