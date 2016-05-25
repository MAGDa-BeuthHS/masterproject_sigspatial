import utils.distance.Haversine
import utils.slicer.grid.SimpleGridSlicer
import utils.slicer.time.SimpleTimeSlicer

object Main extends App {

  val inFileMini: String = System.getProperty("user.dir") + "/src/main/resources/sample_data.csv"
  val inFile: String = System.getProperty("user.dir") + "/src/main/resources/sample_data_huge.csv"
  val inFileComplete: String = "/Users/hagen/Downloads/yellow_tripdata_2015-01.csv"

  val GridSlicer = new SimpleGridSlicer(new Haversine)
  val TimeSlicer = new SimpleTimeSlicer

  val sp = new SparkProcessor(TimeSlicer, GridSlicer)
  sp.process(inFileMini)

}