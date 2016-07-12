import com.typesafe.config.ConfigFactory
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.functions.{count, mean, udf, _}
import org.apache.spark.sql.types._
import org.apache.spark.sql.{DataFrame, Row, SQLContext}
import org.apache.spark.{SparkConf, SparkContext}
import utils.math.GiStar
import utils.slicer.grid.GridSlicer
import utils.slicer.time.TimeSlicer
import utils.writer.Writer

import scala.util.Random

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

  /**
    * Adds missing z and p values to rows in df.
    *
    * @param df without z and p values.
    * @return df with missing values.
    */
  private def calculateMissingValues(df: DataFrame, sqlc: SQLContext): DataFrame = {
    /*
        val count: Long = df.count()
        val mean: Double = df.select(avg("count")).collect()(0).getDouble(0)
        */
    val colName: String = "count"
    val counts = df.select(count(colName), mean(colName), stddev(colName)).head()
    val c: Long = counts.getLong(0)
    val m: Double = counts.getDouble(1)
    val stdDev: Double = counts.getDouble(2)
    val sigma: Double = stdDev * stdDev

    Logger.getLogger(this.getClass).info(s"Evaluating a total of $c rows.")
    Logger.getLogger(this.getClass).debug(s"Mean: $m")
    Logger.getLogger(this.getClass).debug(s"stdDev: $stdDev")
    Logger.getLogger(this.getClass).debug(s"sigma: $sigma")

    def zAndP(neighbors: String) = {
      val w = neighbors.split(",").map(_.toInt).toList
      val z = GiStar.calcZ(w, c, m, sigma)
      val p = GiStar.calcP(z)
      Seq(z, p)
    }

    val schema = StructType(df.schema.fields ++ Array(StructField("zscore", DoubleType), StructField("pvalue", DoubleType)))
    val rows = df.rdd.map(r => Row.fromSeq(r.toSeq ++ zAndP(r.getString(4))))
    sqlc.createDataFrame(rows, schema)
  }

  /**
    * This method is used to get the information we need from all supplied csv files.
    * First we filter for NYC area, then take only the information we need from each csv line and finally reduce and
    * count them.
    *
    * @param cellSize cli argument
    * @param timeSize cli argument
    * @param filename cli argument
    * @param sqlc     The properly initialized SQLContext
    * @return A DataFrame containing tuples in this form: (t, x, y, count, w)
    */
  def transformCsvToDf(cellSize: Double, timeSize: Double, filename: String, sqlc: SQLContext): DataFrame = {
    val df = sqlc.read
      .format("com.databricks.spark.csv")
      .option("header", "true")
      // .option("inferSchema", "true")
      .load(filename)

    def tUDF = udf((ts: String) => timeSlicer.getSliceForTimestamp(ts, timeSize))
    def xUDF = udf((x: String) => gridSlicer.getLatCell(x.toDouble, cellSize))
    def yUDF = udf((y: String) => gridSlicer.getLonCell(y.toDouble, cellSize))
    def calcW = udf(() => List.fill(27)(Random.nextInt(100)).mkString(",")) // TODO

    val txyn = df.filter(df(DropoffLatHeader).isNotNull)
      .filter(df(DropoffLatHeader).geq(DropoffLatMin))
      .filter(df(DropoffLatHeader).leq(DropoffLatMax))
      .filter(df(DropoffLonHeader).isNotNull)
      .filter(df(DropoffLonHeader).geq(DropoffLonMin))
      .filter(df(DropoffLonHeader).leq(DropoffLonMax))
      .select(df(DropoffTimeHeader), df(DropoffLatHeader), df(DropoffLonHeader))
      .withColumn(DropoffTimeHeader, tUDF(df(DropoffTimeHeader)))
      .withColumn(DropoffLatHeader, xUDF(df(DropoffLatHeader)))
      .withColumn(DropoffLonHeader, yUDF(df(DropoffLonHeader)))
      .groupBy(DropoffTimeHeader, DropoffLatHeader, DropoffLonHeader)
      .count()
      .withColumn("w", calcW())

    txyn
  }

  private def getFilenames(dir: String): String = {
    Logger.getLogger(this.getClass).debug(s"Looking for files in $dir")
    val filenames = new java.io.File(dir).listFiles.filter(_.getName.endsWith(".csv")).mkString(",")
    Logger.getLogger(this.getClass).debug(s"Found files: $filenames")
    filenames
  }

  def process(input: String, output: String, cellSize: Double, timeSize: Double): Unit = {
    val sparkConf = new SparkConf()

      /**
        * enable the following line to make it work locally.
        * But beware: if it runs on the cluster with this line not uncommented the cluster uses only one node!
        */
      .setMaster("local[1]")
      .setAppName(conf.getString("app.name"))

    val sc = new SparkContext(sparkConf)
    val sqlContext = new SQLContext(sc)

    // For sanity's sake
    Logger.getLogger("org").setLevel(Level.ERROR)
    Logger.getLogger("akka").setLevel(Level.ERROR)
    Logger.getLogger(this.getClass).setLevel(Level.DEBUG)

    val taxiDataFrame = transformCsvToDf(cellSize, timeSize, input, sqlContext)
    val results = calculateMissingValues(taxiDataFrame, sqlContext)
    results.select(DropoffLatHeader, DropoffLonHeader, DropoffTimeHeader, "zscore", "pvalue")
      .orderBy(desc("pvalue"))
      .show(50)

    sc.stop()
  }
}
