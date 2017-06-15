package utils.slicer.grid

class SimpleGridSlicer() extends GridSlicer {

  def getLatCell(coord: Double, cellSize: Double): Int = {
    getCell(conf.getDouble("dropoff.lat.min"), cellSize, coord)
  }

  def getLonCell(coord: Double, cellSize: Double): Int = {
    getCell(conf.getDouble("dropoff.lon.min"), cellSize, coord)
  }

  private def getCell(lowerBoundary: Double, cellSize: Double, coord: Double): Int = {
    Math.floor(Math.abs((lowerBoundary - coord) / cellSize)).toInt
  }

  override def getCellsForPoint(p: (Double, Double), cellSize: Double): (Int, Int) = {
    (
      getLatCell(p._1, cellSize),
      getLonCell(p._2, cellSize)
      )
  }

  override def getMaxLatCell(cellSize: Double): Int = {
    getLatCell(conf.getDouble("dropoff.lat.max"),cellSize)
  }

  override def getMaxLonCell(cellSize: Double): Int = {
    getLatCell(conf.getDouble("dropoff.lon.max"),cellSize)
  }

  override def getCenterPointForCell(c: (Int, Int), cellSize: Double): (Double, Double) = {
    if ((c._1 >= 0 && c._1 < getMaxLatCell(cellSize)) &&
      (c._2 >= 0 && c._2 < getMaxLonCell(cellSize))) {
      (conf.getDouble("dropoff.lat.min") + (c._1 * cellSize) + (cellSize / 2),
        conf.getDouble("dropoff.lon.min") + (c._2 * cellSize) + (cellSize / 2)
        )
    }
    else {
      null
    }
  }

}
