import org.scalatest.{FlatSpec, Matchers}

/**
  * Right now this is just a placeholder while I'm trying to figure out how to properly traverse our huge dataset.
  */
class KernelSpec extends FlatSpec with Matchers {

  private def getIndices(t: Int, min: Int, max: Int): (Int, Int) = {
    val indices = List(t - 1, t, t + 1).filter(_ >= min).filter(_ <= max)
    (indices.min, indices.max)
  }

  private def getNeighbors(timeSlice: Int, maxTimeSlice: Int, xSlice: Int, maxXSlice: Int, ySlice: Int, maxYSlice: Int): List[(Int, Int)] = {
    val timeSlices = getIndices(timeSlice, 0, maxTimeSlice)
    val xSlices = getIndices(xSlice, 0, maxXSlice)
    val ySlices = getIndices(ySlice, 0, maxYSlice)

    List(timeSlices, xSlices, ySlices)
  }

  "Kernel" should "properly set up all datapoints" in {
    val timeSlices = 0 to 9
    val xSlices = 0 to 9
    val ySlices = 0 to 9

    timeSlices.foreach(timeSlice => {
      ySlices.foreach(ySlice => {
        xSlices.foreach(xSlice => {
          val w = getNeighbors(timeSlice, timeSlices.max, xSlice, xSlices.max, ySlice, ySlices.max)
          w.length should be < 28
        })
      })
    })
  }
}
