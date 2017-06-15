package utils.slicer.grid

import com.typesafe.config.ConfigFactory

trait GridSlicer extends Serializable {
  val conf = ConfigFactory.load()

  def getCellsForPoint(p: (Double, Double), cellSize: Double): (Int, Int)
  def getCenterPointForCell(p: (Int, Int), cellSize: Double): (Double, Double)
  def getLatCell(coord: Double, cellSize: Double): Int
  def getMaxLatCell(cellSize: Double): Int
  def getLonCell(coord: Double, cellSize: Double): Int
  def getMaxLonCell(cellSize: Double): Int
}
