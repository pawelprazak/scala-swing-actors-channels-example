package example

import swing.Publisher
import swing.event.Event
import swing.Swing.onEDT

case class Replied(value: String) extends Event

trait Frontend extends Publisher {
  def call(method: Method, string: String)
  def call(operator: Operator, x: Int,  y: Int)
}

object Frontend {
  def apply(_backend: Backend): Frontend = new Frontend with ChannelDaemon[String, Int] {
    private val backend = _backend

    protected def actions = {
      case s: String => onEDT { publish(Replied(s)) }
    }

    protected def actions2 = {
      case i: Int => onEDT { publish(Replied(i.toString)) }
    }

    def call(method: Method, string: String) {
      backend.requests ! (method, string, channel)
    }

    def call(operator: Operator, x: Int,  y: Int) {
      backend.operations ! (operator, x, y)
    }
  }
}
