package example

import scala.swing.Publisher
import scala.swing.event.Event
import scala.swing.Swing.onEDT

case class Replied(value: String) extends Event

trait Frontend extends Publisher {
  def call(method: Method, string: String)
  def call(operator: Operator, x: Int,  y: Int)
}

object Frontend {
  def apply(_backend: Backend): Frontend = new Frontend with Daemon {
    private val backend = _backend
    import Backend.Reply

    val replies = Channel[Reply] {
      case s: Reply => onEDT { publish(Replied(s)) }
    }

    def call(method: Method, string: String) {
      backend.requests ! (method, string, replies)
    }

    def call(operator: Operator, x: Int,  y: Int) {
      backend.operations ! (operator, x, y, replies)
    }

    println("Frontend created, size " + size)
  }
}
