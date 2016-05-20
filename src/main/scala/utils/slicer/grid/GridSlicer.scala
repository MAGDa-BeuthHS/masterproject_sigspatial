package utils.slicer.grid

trait GridSlicer {
  /**
    * Latitude part of the minimum point for the NYC bounding box.
    */
  val DropoffLatMin: Double = 40.477399

  /**
    * Latitude part of the maximum point for the NYC bounding box.
    */
  val DropoffLonMin: Double = -74.25909

  /**
    * Grid cell size in kilometers
    */
  val GridCellSizeInKm: Double = 0.2

  def getCellForLat(coord: (Double, Double)): Int
  def getCellForLon(coord: (Double, Double)): Int
}
