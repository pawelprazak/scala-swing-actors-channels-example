package example

import scala.PartialFunction
import scala.actors.{Channel, DaemonActor}


trait Daemon {
  /**
   * Inner daemon channel
   */
  protected object Channel {
    import scala.actors.!

    /**
    * Creates and registers a Channel in the Daemon, takes actor handler
    * @param handler Actor handler for this channel
    * @returns new channel
    */
    def apply[Msg](handler: PartialFunction[Msg, Unit]) = new Channel[Msg](daemon) {
      val C = this.asInstanceOf[Channel[Any]]
      val channelHandler: PartialFunction[Any, Unit] = {
        case C ! msg if handler.isDefinedAt(msg.asInstanceOf[Msg]) => handler(msg.asInstanceOf[Msg])
      }
      daemon add channelHandler
      println("Channel added")
    }
  }

  protected val daemon = new DaemonActor {
    private var nrOfHandlers: Int = 0
    def add(newHandler: PartialFunction[Any, Unit]) {
      handler = newHandler orElse handler
      nrOfHandlers += 1
    }
    def size = nrOfHandlers

    private var handler: PartialFunction[Any, Unit] = {
      case msg => throw new RuntimeException("Illegal message: " + msg)
    }

    def act() {
      println("Daemon started, size %s" format nrOfHandlers)
      loop {
        react(handler)
      }
    }
  }
  daemon.start()

  def size: Int = daemon.size

  println("Daemon created, size " + size)
}
