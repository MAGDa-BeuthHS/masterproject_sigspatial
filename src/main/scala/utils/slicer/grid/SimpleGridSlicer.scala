package utils.slicer.grid

import utils.distance.DistanceCalculator

class SimpleGridSlicer(calc: DistanceCalculator) extends GridSlicer {

  private def translateCoord(coord: (Double, Double), base: (Double, Double)): Int = {
    Math.floor(calc.calculate(base, coord) / conf.getDouble("app.cellsize")).toInt
  }

  override def getCellsForPoint(p: (Double, Double)): (Int, Int) = {
    (
      translateCoord(p, (conf.getDouble("dropoff.lat.min"), p._2)),
      translateCoord(p, (p._1, conf.getDouble("dropoff.lon.min")))
    )
  }
}
