package example

import scala.PartialFunction
import actors.{Exit, Channel, DaemonActor}


case object Die

trait Daemon {
  /**
   * Inner daemon channel
   */
  protected object Channel {
    import scala.actors.!

    /**
    * Creates and registers a Channel in the Daemon, takes actor actions
    * @param actions Actor actions for this channel
    * @returns new channel
    */
    def apply[Msg](actions: PartialFunction[Msg, Unit]) = new Channel[Msg](actor) {
      val C = this.asInstanceOf[Channel[Any]]
      val channelReactions: PartialFunction[Any, Unit] = {
        case C ! msg if actions.isDefinedAt(msg.asInstanceOf[Msg]) => actions(msg.asInstanceOf[Msg])
      }
      actor += channelReactions
      println("Channel added")
    }
  }

  private val actor = new DaemonActor {
    private var reactions: Seq[PartialFunction[Any, Unit]] = Seq()
    def +=(newReactions: PartialFunction[Any, Unit]) {
      reactions = reactions :+ newReactions
    }

    def act() {
      println("Daemon started, %s actions" format reactions.size)
      require(!reactions.isEmpty)
      loop {
        react { reactions reduce {_ orElse _} orElse {
          case Exit(dead, reason) => {
            println("Daemon died because of: %s" format(dead, reason))
          }
          case Die => {
            println("Backend: Die")
            exit("Die")
          }
          case msg => throw new RuntimeException("Illegal message: " + msg)
      }}}
    }
  }
  actor.start()

  def stop() { actor ! Die }
  println("Daemon created")
}
