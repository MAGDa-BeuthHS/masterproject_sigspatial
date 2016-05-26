import com.typesafe.config.ConfigFactory
import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkConf, SparkContext}
import utils.slicer.grid.GridSlicer
import utils.slicer.time.TimeSlicer

class SparkProcessor(timeSlicer: TimeSlicer, gridSlicer: GridSlicer) extends Serializable {

  val conf = ConfigFactory.load()
  val DropoffLatIdx: Int = conf.getInt("dropoff.lat.idx")
  val DropoffLonIdx: Int = conf.getInt("dropoff.lon.idx")
  val DropoffTimeIdx: Int = conf.getInt("dropoff.time.idx")
  val DropoffLatMin: Double = conf.getLong("dropoff.lat.min")
  val DropoffLatMax: Double = conf.getDouble("dropoff.lat.max")
  val DropoffLonMin: Double = conf.getDouble("dropoff.lon.min")
  val DropoffLonMax: Double = conf.getDouble("dropoff.lon.max")


  def process(file: String): Unit = {
    val sparkConf = new SparkConf()
      .setAppName(conf.getString("app.name"))

    val sc = new SparkContext(sparkConf)

    // For sanity's sake
    Logger.getLogger("org").setLevel(Level.ERROR)
    Logger.getLogger("akka").setLevel(Level.ERROR)

    val taxiFile = sc.textFile(file)

    val taxiData = taxiFile
      // Filter for the year 2015 with non-empty longitude and latitude
      .filter(_.contains("2015"))
      .filter(_.split(",")(DropoffLatIdx) != "")
      .filter(_.split(",")(DropoffLonIdx) != "")
      // Filter for the NYC area
      .filter(_.split(",")(DropoffLatIdx).toDouble > DropoffLatMin)
      .filter(_.split(",")(DropoffLatIdx).toDouble < DropoffLatMax)
      .filter(_.split(",")(DropoffLonIdx).toDouble > DropoffLonMin)
      .filter(_.split(",")(DropoffLonIdx).toDouble < DropoffLonMax)
      // get only the relevant fields off of every csv line and map them to our (t, x, y) representation
      .map(parseCsvLine)
      // reduce by counting everything with the same key
      .reduceByKey(_ + _)
      // take only locations with more than 250 dropoffs
      //.filter(_._2 > 250)
      // and sort by dropoff count
      .sortBy(_._2)
      // take top 50
      .take(50)
      .foreach(println)

    sc.stop()
  }

  def parseCsvLine(line: String): ((Int, Int, Int), Int) = {
    val fields = line.split(",")
    val cells = gridSlicer.getCellsForPoint((fields(DropoffLatIdx).toDouble, fields(DropoffLonIdx).toDouble))
    (
      (
        timeSlicer.getSliceForTimestamp(fields(DropoffTimeIdx)),
        cells._1,
        cells._2
        ),
      1
      )
  }
}
