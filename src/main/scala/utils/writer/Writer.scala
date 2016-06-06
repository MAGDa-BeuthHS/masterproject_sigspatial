package utils.writer

trait Writer extends Serializable{
  def write(data: Array[((Int, Int, Int), Int)], output: String)
}
