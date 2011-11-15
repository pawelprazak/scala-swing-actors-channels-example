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
  def stop()
}

object Backend {
  type Operation = (Operator, Int,  Int)
  type Request = (Method, String, OutputChannel[String])

  def apply(): Backend = new Backend with ChannelDaemon[Request, Operation] {
    def requests = channel
    def operations = channel2
    def actions = {
      case (Echo, string, chan) => chan ! string
      case (Reverse, string, chan) => chan ! string.reverse
      case (Upper, string, chan) => chan ! string.toUpperCase
      case (Lower, string, chan) => chan ! string.toLowerCase
    }

    def actions2 = {
      case (Add, x, y) => println(x + y)
      case (Sub, x, y) => println(x - y)
      case (Mul, x, y) => println(x * y)
      case (Div, x, y) => println(x / y)
    }

    def stop() { receiver ! Die }
  }
}