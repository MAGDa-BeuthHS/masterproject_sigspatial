package utils.slicer.grid

class SimpleGridSlicer() extends GridSlicer {

  def getLatCell(coord: Double): Int = {
    getCell(conf.getDouble("dropoff.lat.min"), conf.getDouble("app.cellsize"), coord)
  }

  def getLonCell(coord: Double): Int = {
    getCell(conf.getDouble("dropoff.lon.max"), conf.getDouble("app.cellsize"), coord)
  }

  private def getCell(lowerBoundary: Double, cellSize: Double, coord: Double): Int = {
    val max = Math.max(coord, lowerBoundary)
    val min = Math.min(coord, lowerBoundary)
    Math.floor(max - min / cellSize).toInt
  }

  override def getCellsForPoint(p: (Double, Double)): (Int, Int) = {
    (
      getLatCell(p._1),
      getLonCell(p._2)
      )
  }
}
