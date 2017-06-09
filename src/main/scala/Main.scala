import java.io.File

import com.typesafe.config.ConfigFactory
import org.apache.hadoop.mapred.InvalidInputException
import org.apache.log4j.{Level, Logger}
import org.joda.time.DateTime
import utils.slicer.grid.SimpleGridSlicer
import utils.slicer.time.SimpleTimeSlicer
import utils.writer.{CsvWriter, TerminalWriter}

/**
  * Example call:
  * ./bin/spark-submit [spark properties] --class [submission class] [submission jar] [path to input] [path to output] [cell size in degrees] [time step size in millis]
  */
object Main extends App {

  val conf = ConfigFactory.load()
  val gridslicer = new SimpleGridSlicer
  val timeslicer = new SimpleTimeSlicer
  val logger = Logger.getLogger(Main.getClass)
  logger.setLevel(Level.DEBUG)

  val sp = new SparkProcessor(timeslicer, gridslicer, Seq(new CsvWriter, new TerminalWriter))

  private def printUsageAndError(msg: String) = {
    logger.error(msg)
    logger.error("This is how you do it:")
    logger.error("./bin/spark-submit [spark properties] --class [submission class] [submission jar] [path to input] [path to output] [cell size in degrees] [time step size in millis]")
  }

  try {
    logger.info(s"Started at: ${new DateTime().toString()}")
    logger.debug(s"${args.length} arguments supplied:")
    args.foreach(logger.debug)
    if (args.length != 4) throw new IllegalArgumentException("Number of parameters is completely wrong, man!")

    val input: String = args(0)
    val output: String = args(1)
    val cellSize: Double = args(2).toDouble
    val timeSize: Long = args(3).toLong

    val inputDir: File = new File(input)
    if (!inputDir.isDirectory && !inputDir.canRead) throw new IllegalArgumentException(s"Input directory ${inputDir.getAbsolutePath} is not readable!")

    val outputDir: File = new File(output)
    if (!outputDir.isDirectory && outputDir.canWrite) throw new IllegalArgumentException(s"Output directory ${outputDir.getAbsolutePath} is not writable!")

    if (cellSize <= 0) throw new IllegalArgumentException("cellSize must be > 0")
    if (timeSize <= 0) throw new IllegalArgumentException("timeSize must be > 0")

    sp.process(input, output, cellSize, timeSize)

    logger.info(s"Finished at: ${new DateTime().toString()}")

  } catch {
    case e: InvalidInputException => logger.error(e.getMessage)
    case e: IllegalArgumentException => printUsageAndError(e.getMessage)
  }
}
