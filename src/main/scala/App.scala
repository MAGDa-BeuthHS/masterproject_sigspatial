import java.text.SimpleDateFormat

import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkConf, SparkContext}
import org.joda.time.{DateTime, Duration}
import utils.distance.Haversine
import utils.slicer.grid.SimpleGridSlicer
import utils.slicer.time.SimpleTimeSlicer

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
  val inFile: String = System.getProperty("user.dir") + "/src/main/resources/sample_data_huge.csv"
  // val inFile: String = "/Users/hagen/Downloads/yellow_tripdata_2015-01.csv"

  val conf = new SparkConf()
    .setMaster("local[1]")
    .setAppName("masterproject_sigspatial")

  val sc = new SparkContext(conf)

  val GridSlicer = new SimpleGridSlicer(new Haversine)
  val TimeSlicer = new SimpleTimeSlicer

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
      //.filter(_._2 > 250)
      // and sort by dropoff count
      .sortBy(_._2)
      // take top 50
      //.take(50)
      .foreach(println)

    sc.stop()
  }

  def parseCsvLine(line: String): ((Int, Int, Int), Int) = {
    val fields = line.split(",")
    (
      (
        TimeSlicer.getSliceForTimestamp(fields(DropoffTimeIdx)),
        GridSlicer.getCellForLat((fields(DropoffLatIdx).toDouble, DropoffLonMin)),
        GridSlicer.getCellForLon((DropoffLatMin, fields(DropoffLonIdx).toDouble))
        ),
      1
      )
  }

}