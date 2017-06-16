import java.io.File

import com.typesafe.config.ConfigFactory
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{DataFrame, SQLContext, SaveMode}
import org.apache.spark.{SparkConf, SparkContext}
import utils.math.GiStar
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

  /**
    * Adds missing z and p values to rows in df.
    *
    * @param df without z and p values.
    * @return df with missing values.
    */
  private def calculateZandP(df: DataFrame, sqlc: SQLContext): DataFrame = {
    import sqlc.implicits._

    val colName: String = "n"
    val counts = df.select(count(colName), mean(colName), stddev(colName)).head()
    val c: Long = counts.getLong(0) // count
    val m: Double = counts.getDouble(1) // mean
    val stdDev: Double = counts.getDouble(2)
    val stdDevPow2: Double = stdDev * stdDev

    Logger.getLogger(this.getClass).info(s"Evaluating a total of $c rows.")
    Logger.getLogger(this.getClass).debug(s"Mean: $m")
    Logger.getLogger(this.getClass).debug(s"stdDev: $stdDev")
    Logger.getLogger(this.getClass).debug(s"stdDevPow2: $stdDevPow2")

    def udfCalcZ = udf((wLength: Int, wSum: Int) => GiStar.calcZ(wLength, wSum, c, m, stdDevPow2))
    def udfCalcP = udf((z: Double) => GiStar.calcP(z))

    val dfWithPandZ = df.as("a")
      .join(
        df.as("b"),
        $"b.t".geq($"a.t" - 1) && $"b.t".leq($"a.t" + 1)
          && $"b.y".geq($"a.y" - 1) && $"b.y".leq($"a.y" + 1)
          && $"b.x".geq($"a.x" - 1) && $"b.x".leq($"a.x" + 1)
      )
      .groupBy($"a.x", $"a.y", $"a.t", $"a.n").agg(count($"b.n").as("wLength"), sum($"b.n").as("wSum"))
      .withColumn("z", udfCalcZ($"wLength", $"wSum"))
      .withColumn("p", udfCalcP($"z"))

    dfWithPandZ
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
  private def transformCsvToDf(cellSize: Double, timeSize: Long, filename: String, sqlc: SQLContext): DataFrame = {
    val df = sqlc.read
      .format("com.databricks.spark.csv")
      .option("header", "true")
      // .option("inferSchema", "true")
      .load(filename)

    def timeUDF = udf((ts: String) => timeSlicer.getSlice(ts, timeSize))
    def lonUDF = udf((x: String) => gridSlicer.getLonCell(x.toDouble, cellSize))
    def latUDF = udf((y: String) => gridSlicer.getLatCell(y.toDouble, cellSize))

    val txyn = df
      .filter(df(DropoffLatHeader).geq(DropoffLatMin)
        && df(DropoffLatHeader).leq(DropoffLatMax)
        && df(DropoffLatHeader).isNotNull
        && df(DropoffLonHeader).geq(DropoffLonMin)
        && df(DropoffLonHeader).leq(DropoffLonMax)
        && df(DropoffLonHeader).isNotNull
      )
      .select(df(DropoffTimeHeader), df(DropoffLatHeader), df(DropoffLonHeader))
      .withColumn(DropoffTimeHeader, timeUDF(df(DropoffTimeHeader)))
      .withColumn(DropoffLatHeader, latUDF(df(DropoffLatHeader)))
      .withColumn(DropoffLonHeader, lonUDF(df(DropoffLonHeader)))
      .groupBy(DropoffTimeHeader, DropoffLatHeader, DropoffLonHeader)
      .count()
      .withColumnRenamed(DropoffTimeHeader, "t")
      .withColumnRenamed(DropoffLatHeader, "y")
      .withColumnRenamed(DropoffLonHeader, "x")
      .withColumnRenamed("count", "n")

    txyn
  }

  private def getFilenames(dir: String): String = {
    Logger.getLogger(this.getClass).debug(s"Looking for files in $dir")
    val filenames = new java.io.File(dir).listFiles.filter(_.getName.endsWith(".csv")).mkString(",")
    Logger.getLogger(this.getClass).debug(s"Found files: $filenames")
    filenames
  }

  def process(input: String, output: String, cellSize: Double, timeSize: Long): Unit = {
    val sparkConf = new SparkConf()

      /**
        * enable the following line to make it work locally.
        * But beware: if it runs on the cluster with this line not uncommented the cluster uses all available nodes!
        */
      //.setMaster("*")
      .setAppName(conf.getString("app.name"))

    val sc = new SparkContext(sparkConf)
    val sqlContext = new SQLContext(sc)
    import sqlContext.implicits._

    // For sanity's sake
    Logger.getLogger("org").setLevel(Level.ERROR)
    Logger.getLogger("akka").setLevel(Level.ERROR)
    Logger.getLogger(this.getClass).setLevel(Level.DEBUG)

    val formatDouble = udf((number: Double) => BigDecimal(number).setScale(4, BigDecimal.RoundingMode.HALF_UP).toDouble)
    val taxiDataFrame = transformCsvToDf(cellSize, timeSize, getFilenames(input), sqlContext)
    val results = calculateZandP(taxiDataFrame, sqlContext)
      .withColumnRenamed("x", "cell_x")
      .withColumnRenamed("y", "cell_y")
      .withColumnRenamed("t", "time_step")
      .withColumn("zscore", formatDouble($"z"))
      .withColumn("pvalue", formatDouble($"p"))
      .select("cell_x", "cell_y", "time_step", "zscore", "pvalue")
      .filter($"pvalue".gt(0.0) && $"pvalue".leq(0.05))
      .orderBy(desc("zscore"))
      //.limit(50)

    val out: String = new File(s"$output").getAbsolutePath
    Logger.getLogger(this.getClass).debug(s"Writing output to: $out")
    results.write
      .mode(SaveMode.Overwrite)
      .format("com.databricks.spark.csv")
      .option("header", "true")
      .save(out)

    sc.stop()
  }
}
