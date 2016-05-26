import org.apache.hadoop.mapred.InvalidInputException
import org.apache.log4j.Logger
import utils.distance.Haversine
import utils.slicer.grid.SimpleGridSlicer
import utils.slicer.time.SimpleTimeSlicer

/**
  * Example call:
  * ./bin/spark-submit [spark properties] --class [submission class] [submission jar] [path to input] [path to output] [cell size in degrees] [time step size in days]
  */
object Main extends App {

  val inFileMini: String = System.getProperty("user.dir") + "/src/main/resources/sample_data.csv"
  val inFile: String = System.getProperty("user.dir") + "/src/main/resources/sample_data_huge.csv"
  val inFileComplete: String = "/Users/hagen/Downloads/yellow_tripdata_2015-01.csv"

  val GridSlicer = new SimpleGridSlicer(new Haversine)
  val TimeSlicer = new SimpleTimeSlicer

  val sp = new SparkProcessor(TimeSlicer, GridSlicer)

  try {
    sp.process(args(0))
  } catch {
    case e: InvalidInputException => Logger.getLogger(Main.getClass).error(e.getMessage)
  }

}