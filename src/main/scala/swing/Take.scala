package swing

trait Take {
  def text: String
  def text_=(s: String)
  def take: String = {
    val value = text
    text = ""
      value
    }
}
