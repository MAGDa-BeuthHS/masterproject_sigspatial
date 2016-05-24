package utils.slicer.grid

import com.typesafe.config.ConfigFactory

trait GridSlicer extends Serializable {
  val conf = ConfigFactory.load()
  val DropoffLatMin: Double = conf.getLong("dropoff.lat.min")
  val DropoffLonMin: Double = conf.getDouble("dropoff.lon.min")
  val GridCellSizeInKm: Double = conf.getDouble("app.cellsize")

  def getCellForLat(lat: Double): Int

  def getCellForLon(lon: Double): Int
}
