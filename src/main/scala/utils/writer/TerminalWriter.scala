package utils.writer

import org.apache.log4j.{Level, Logger}

class TerminalWriter extends Writer{
  override def write(data: Array[((Int, Int, Int), Int)], output: String): Unit = {
    val l = Logger.getLogger(this.getClass)
    l.setLevel(Level.INFO)

    data.foreach(l.info)
  }
}
