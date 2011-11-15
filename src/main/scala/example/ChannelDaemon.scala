package example

import scala.PartialFunction
import actors.{DaemonActor, Channel}

case object Die

trait ChannelDaemon[Msg, Msg2] {
  protected def actions: PartialFunction[Msg, Unit]
  protected def actions2: PartialFunction[Msg2, Unit]
  protected var channel: Channel[Msg] = null
  protected var channel2: Channel[Msg2] = null
  protected val receiver = new DaemonActor {
    def act() {
      println("Daemon started")
      import scala.actors.!
      val C = channel.asInstanceOf[Channel[Any]]
      val C2 = channel2.asInstanceOf[Channel[Any]]
      loop {
        react {
          case C ! msg if actions.isDefinedAt(msg.asInstanceOf[Msg]) => actions(msg.asInstanceOf[Msg])
          case C2 ! msg if actions2.isDefinedAt(msg.asInstanceOf[Msg2]) => actions2(msg.asInstanceOf[Msg2])
          case Die => {
            println("Backend: Die")
            exit("Die")
          }
          case msg => throw new RuntimeException("Illegal message: " + msg)
        }
      }
    }
  }
  receiver.start()
  channel = new Channel(receiver)
  channel2 = new Channel(receiver)
}