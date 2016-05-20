import java.text.SimpleDateFormat

import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkConf, SparkContext}
import org.joda.time.{DateTime, Duration}

object App {

  /**
    * CSV Column Index for ''dropoff_latitude''
    */
  val DropoffLatIdx: Int = 10

  /**
    * CSV Column Index for ''dropoff_longitude''
    */
  val DropoffLonIdx: Int = 9

  /**
    * CSV Column Index for ''tpep_dropoff_datetime''
    */
  val DropoffTimeIdx: Int = 2

  /**
    * Latitude part of the minimum point for the NYC bounding box.
    */
  val DropoffLatMin: Double = 40.477399

  /**
    * Longitude part of the minimum point for the NYC bounding box.
    */
  val DropoffLatMax: Double = 40.917577

  /**
    * Latitude part of the maximum point for the NYC bounding box.
    */
  val DropoffLonMin: Double = -74.25909

  /**
    * Latitude part of the minimum point for the NYC bounding box.
    */
  val DropoffLonMax: Double = -73.70009

  /**
    * Complete path to the data file.
    */
  // val inFile: String = System.getProperty("user.dir") + "/src/main/resources/sample_data_huge.csv"
  val inFile: String = "/Users/hagen/Downloads/yellow_tripdata_2015-01.csv"

  /**
    * Grid cell size in kilometers
    */
  val GridCellSizeInKm: Double = 0.2

  val formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
  val zero = new DateTime("2015-01-01T00:00:00.000-00:00")

  val conf = new SparkConf()
    .setMaster("local[1]")
    .setAppName("masterproject_sigspatial")

  val sc = new SparkContext(conf)

  def main(args: Array[String]) {
    // For sanity's sake
    Logger.getLogger("org").setLevel(Level.ERROR)
    Logger.getLogger("akka").setLevel(Level.ERROR)

    // Create a RDD from a csv file
    val taxiFile = sc.textFile(inFile)

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
      .filter(_._2 > 250)
      // and sort by dropoff count
      //.sortBy(_._2)
      // take top 50
      //.take(50)
      .foreach(println)

    sc.stop()
  }

  def parseCsvLine(line: String): ((Long, Int, Int), Int) = {
    val fields = line.split(",")
    (
      (
        translateTimestamp(fields(DropoffTimeIdx)),
        translateLat(fields(DropoffLatIdx).toDouble, DropoffLatMin),
        translateLon(fields(DropoffLonIdx).toDouble, DropoffLonMin)
        ),
      1
      )
  }

  def translateLat(coord: Double, base: Double): Int = {
    translateCoord((coord, DropoffLonMin), (base, DropoffLonMin))
  }

  def translateLon(coord: Double, base: Double): Int = {
    translateCoord((DropoffLatMin, coord), (DropoffLatMin, base))
  }

  def translateCoord(coord: (Double, Double), base: (Double, Double)): Int = {
    // haversine distance in km, convert to meters, divide by grid cell count
    Math.floor(haversineDistance(base, coord) / GridCellSizeInKm).toInt
  }

  def translateTimestamp(timeInCsv: String, zero: DateTime = zero): Long = {
    val dt = new DateTime(formatter.parse(timeInCsv))
    (roundDateTime(dt, Duration.standardHours(2)).getMillis - zero.getMillis) / Duration.standardHours(2).getMillis
  }

  def roundDateTime(t: DateTime, d: Duration): DateTime = {
    t minus (t.getMillis - (t.getMillis.toDouble / d.getMillis).round * d.getMillis)
  }

  /**
    * source: https://davidkeen.com/blog/2013/10/calculating-distance-with-scalas-foldleft/
    */
  def haversineDistance(pointA: (Double, Double), pointB: (Double, Double)): Double = {
    val deltaLat = math.toRadians(pointB._1 - pointA._1)
    val deltaLong = math.toRadians(pointB._2 - pointA._2)
    val a = math.pow(math.sin(deltaLat / 2), 2) + math.cos(math.toRadians(pointA._1)) * math.cos(math.toRadians(pointB._1)) * math.pow(math.sin(deltaLong / 2), 2)
    val greatCircleDistance = 2 * math.atan2(math.sqrt(a), math.sqrt(1 - a))
    3958.761 * greatCircleDistance
  }
}