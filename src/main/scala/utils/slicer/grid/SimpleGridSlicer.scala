package utils.slicer.grid

class SimpleGridSlicer() extends GridSlicer {

  def getLatCell(coord: Double, cellSize: Double): Option[Int] = {
    if (coord >= conf.getDouble("dropoff.lat.min")
      && coord <= conf.getDouble("dropoff.lat.max")) {
      Option.apply(getCell(conf.getDouble("dropoff.lat.min"), cellSize, coord))
    }
    else {
      None
    }
  }

  def getLonCell(coord: Double, cellSize: Double): Option[Int] = {
    if (coord >= conf.getDouble("dropoff.lon.min")
      && coord <= conf.getDouble("dropoff.lon.max")) {
      Option.apply(getCell(conf.getDouble("dropoff.lon.min"), cellSize, coord))
    }
    else {
      None
    }
  }

  private def getCell(lowerBoundary: Double, cellSize: Double, coord: Double): Int = {
    Math.floor(Math.abs((lowerBoundary - coord) / cellSize)).toInt
  }

  override def getCellsForPoint(p: (Double, Double), cellSize: Double): Option[(Int, Int)] = {
    getLatCell(p._1, cellSize) match {
      case Some(latCell) =>
        getLonCell(p._2, cellSize) match {
          case Some(lonCell) => Option.apply((latCell.toInt, lonCell.toInt))
          case None => None //println("Longitude value out of bounds")
        }
      case None => None //println("Latitude value out of bounds")
    }
  }

  override def getMaxLatCell(cellSize: Double): Int = {
    getLatCell(conf.getDouble("dropoff.lat.max"),cellSize) match {
      case Some(maxLatCell) => maxLatCell.toInt
      case None => -1
    }
  }

  override def getMaxLonCell(cellSize: Double): Int = {
    getLonCell(conf.getDouble("dropoff.lon.max"),cellSize) match {
      case Some(maxLonCell) => maxLonCell.toInt
      case None => -1
    }
  }

  override def getCenterPointForCell(c: (Int, Int), cellSize: Double): Option[(Double, Double)] = {
    if ((c._1 >= 0 && c._1 <= getMaxLatCell(cellSize)) &&
      (c._2 >= 0 && c._2 <= getMaxLonCell(cellSize))) {
      Option.apply(conf.getDouble("dropoff.lat.min") + (c._1 * cellSize) + (cellSize / 2),
        conf.getDouble("dropoff.lon.min") + (c._2 * cellSize) + (cellSize / 2)
        )
    }
    else {
      None
    }
  }

}
