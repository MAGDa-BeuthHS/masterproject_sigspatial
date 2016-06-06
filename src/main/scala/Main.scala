import java.io.File

import com.typesafe.config.ConfigFactory
import org.apache.hadoop.mapred.InvalidInputException
import org.apache.log4j.{Level, Logger}
import utils.slicer.grid.SimpleGridSlicer
import utils.slicer.time.SimpleTimeSlicer

/**
  * Example call:
  * ./bin/spark-submit [spark properties] --class [submission class] [submission jar] [path to input] [path to output] [cell size in degrees] [time step size in days]
  */
object Main extends App {

  val conf = ConfigFactory.load()
  val GridSlicer = new SimpleGridSlicer
  val TimeSlicer = new SimpleTimeSlicer
  val logger = Logger.getLogger(Main.getClass)
  logger.setLevel(Level.DEBUG)

  val sp = new SparkProcessor(TimeSlicer, GridSlicer)

  private def printUsageAndError(msg: String) = {
    logger.error(msg)
    logger.error("This is how you do it:")
    logger.error("./bin/spark-submit [spark properties] --class [submission class] [submission jar] [path to input] [path to output] [cell size in degrees] [time step size in days]")
  }

  try {
    if (args.length != 4) throw new IllegalArgumentException("Number of parameters is completely wrong, man!")

    val input: String = args(0)
    val output: String = args(1)
    val cellSize: Double = args(2).toDouble
    val timeSize: Int = args(3).toInt

    if (!new File(input).canWrite) throw new IllegalArgumentException("Output directory is not writable!")
    if (cellSize <= 0) throw new IllegalArgumentException("cellSize must be > 0")
    if (timeSize <= 0) throw new IllegalArgumentException("timeSize must be > 0")

    sp.process(input, output, cellSize, timeSize)

  } catch {
    case e: InvalidInputException => logger.error(e.getMessage)
    case e: IllegalArgumentException => printUsageAndError(e.getMessage)
  }
}
