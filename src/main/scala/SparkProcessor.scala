import com.typesafe.config.ConfigFactory
import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkConf, SparkContext}
import utils.slicer.grid.GridSlicer
import utils.slicer.time.TimeSlicer
import utils.writer.Writer

class SparkProcessor(timeSlicer: TimeSlicer, gridSlicer: GridSlicer, writers: Seq[Writer]) extends Serializable {

  val conf = ConfigFactory.load()
  val DropoffTimeHeader: String = conf.getString("dropoff.time.header")
  val DropoffLatHeader: String = conf.getString("dropoff.lat.header")
  val DropoffLonHeader: String = conf.getString("dropoff.lon.header")
  val DropoffLatIdx: Int = conf.getInt("dropoff.lat.idx")
  val DropoffLonIdx: Int = conf.getInt("dropoff.lon.idx")
  val DropoffTimeIdx: Int = conf.getInt("dropoff.time.idx")
  val DropoffLatMin: Double = conf.getLong("dropoff.lat.min")
  val DropoffLatMax: Double = conf.getDouble("dropoff.lat.max")
  val DropoffLonMin: Double = conf.getDouble("dropoff.lon.min")
  val DropoffLonMax: Double = conf.getDouble("dropoff.lon.max")


  def process(input: String, output: String, cellSize: Double, timeSize: Double): Unit = {
    val sparkConf = new SparkConf()

      /**
        * enable the following line to make it work locally.
        * But beware: if it runs on the cluster with this line not uncommented the cluster uses only one node!
        */
      // .setMaster("local[1]")
      .setAppName(conf.getString("app.name"))

    val sc = new SparkContext(sparkConf)

    // For sanity's sake
    Logger.getLogger("org").setLevel(Level.ERROR)
    Logger.getLogger("akka").setLevel(Level.ERROR)
    Logger.getLogger(this.getClass).setLevel(Level.INFO)

    val taxiFile = sc.textFile(input)

    val taxiData = taxiFile
      // Remove header
      .filter(!_.startsWith("VendorID"))
      // Filter for the year 2015 with non-empty longitude and latitude
      .filter(_.split(",")(DropoffLatIdx) != "")
      .filter(_.split(",")(DropoffLonIdx) != "")
      // Filter for the NYC area
      .filter(_.split(",")(DropoffLatIdx).toDouble > DropoffLatMin)
      .filter(_.split(",")(DropoffLatIdx).toDouble < DropoffLatMax)
      .filter(_.split(",")(DropoffLonIdx).toDouble > DropoffLonMin)
      .filter(_.split(",")(DropoffLonIdx).toDouble < DropoffLonMax)
      // get only the relevant fields off of every csv line and map them to our (t, x, y) representation
      .map(line => parseCsvLine(line, cellSize, timeSize))
      // reduce by counting everything with the same key
      .reduceByKey(_ + _)

    val countRows = taxiData.count()
    Logger.getLogger(this.getClass).info(s"$countRows rows in total.")

    val outputData = taxiData
      // and sort by dropoff count
      .sortBy(_._2, ascending = false)
      // take top 50
      .take(50)

    writers.foreach(writer => writer.write(outputData, output))
    Logger.getLogger(this.getClass).info(s"Output has been written to $output/${conf.getString("output.filename")}")
    sc.stop()
  }

  def parseCsvLine(line: String, cellSize: Double, timeSize: Double): ((Int, Int, Int), Int) = {
    val fields = line.split(",")

    val cells = gridSlicer.getCellsForPoint((fields(DropoffLatIdx).toDouble, fields(DropoffLonIdx).toDouble), cellSize)

    ((timeSlicer.getSliceForTimestamp(fields(DropoffTimeIdx), timeSize), cells._1, cells._2), 1)
  }
}
