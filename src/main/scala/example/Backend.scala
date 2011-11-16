package example

import actors.{Channel, OutputChannel}


abstract class Method
case object Echo extends Method
case object Reverse extends Method
case object Upper extends Method
case object Lower extends Method

abstract class Operator
case object Add extends Operator
case object Sub extends Operator
case object Mul extends Operator
case object Div extends Operator


trait Backend {
  def requests: Channel[Backend.Request]
  def operations: Channel[Backend.Operation]
}

object Backend {
  type Operation = (Operator, Int,  Int, OutputChannel[Reply])
  type Request = (Method, String, OutputChannel[Reply])
  type Reply = String

  def apply(): Backend = new Backend with Daemon {
    val requests = Channel[Request] {
      case (Echo, string, chan) => chan ! string
      case (Reverse, string, chan) => chan ! string.reverse
      case (Upper, string, chan) => chan ! string.toUpperCase
      case (Lower, string, chan) => chan ! string.toLowerCase
    }

    val operations = Channel[Operation] {
      case (Add, x, y, chan) => chan ! (x + y).toString
      case (Sub, x, y, chan) => chan ! (x - y).toString
      case (Mul, x, y, chan) => chan ! (x * y).toString
      case (Div, x, y, chan) => chan ! (x / y).toString
    }
    println("Backend created, size " + size)
  }
}
