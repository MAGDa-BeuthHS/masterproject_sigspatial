package utils.slicer.grid

import utils.distance.DistanceCalculator

class SimpleGridSlicer(calc:DistanceCalculator) extends GridSlicer {
  override def getCellForLat(coord: (Double, Double)): Int = {
    translateCoord(coord, (DropoffLatMin, DropoffLonMin))
  }

  override def getCellForLon(coord: (Double, Double)): Int = {
    translateCoord(coord, (DropoffLatMin, DropoffLonMin))
  }

  def translateCoord(coord: (Double, Double), base: (Double, Double)): Int = {
    Math.floor(calc.calculate(base, coord) / GridCellSizeInKm).toInt
  }
}
