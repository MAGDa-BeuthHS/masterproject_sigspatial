package utils.writer

import java.io.PrintWriter

import com.typesafe.config.ConfigFactory

class CsvWriter extends Writer {
  val conf = ConfigFactory.load()

  override def write(data: Array[((Int, Int, Int), Int)], output: String): Unit = {
    val fqfn = s"$output/${conf.getString("output.filename")}"
    new PrintWriter(fqfn) {
      write(s"${conf.getString("output.header")}\n")
      data.foreach(line => write(s"${line._1._2},${line._1._3},${line._1._1},0,0\n"))
      close
    }
  }
}
