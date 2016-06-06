package utils.slicer.grid

import com.typesafe.config.ConfigFactory

trait GridSlicer extends Serializable {
  val conf = ConfigFactory.load()

  def getCellsForPoint(p: (Double, Double), cellSize: Double): (Int, Int)
}
