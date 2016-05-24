package utils.slicer.grid

import utils.distance.DistanceCalculator

class SimpleGridSlicer(calc: DistanceCalculator) extends GridSlicer {
  override def getCellForLat(lat: Double): Int = {
    translateCoord((lat, DropoffLonMin), (DropoffLatMin, DropoffLonMin))
  }

  override def getCellForLon(lon: Double): Int = {
    translateCoord((DropoffLatMin, lon), (DropoffLatMin, DropoffLonMin))
  }

  def translateCoord(coord: (Double, Double), base: (Double, Double)): Int = {
    Math.floor(calc.calculate(base, coord) / GridCellSizeInKm).toInt
  }
}
