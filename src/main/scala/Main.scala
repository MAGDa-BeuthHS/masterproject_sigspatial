import org.apache.hadoop.mapred.InvalidInputException
import org.apache.log4j.Logger
import utils.slicer.grid.SimpleGridSlicer
import utils.slicer.time.SimpleTimeSlicer

/**
  * Example call:
  * ./bin/spark-submit [spark properties] --class [submission class] [submission jar] [path to input] [path to output] [cell size in degrees] [time step size in days]
  */
object Main extends App {

  val GridSlicer = new SimpleGridSlicer
  val TimeSlicer = new SimpleTimeSlicer

  val sp = new SparkProcessor(TimeSlicer, GridSlicer)

  try {
    sp.process(args(0))
  } catch {
    case e: InvalidInputException => Logger.getLogger(Main.getClass).error(e.getMessage)
  }

}