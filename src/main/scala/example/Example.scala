package example

import swing.event.ButtonClicked
import swing._


object Example extends SimpleSwingApplication {
  val backend = Backend()
  val frontend = Frontend(backend)

	lazy val ui = new BoxPanel(Orientation.Vertical) {
    val input = new TextArea(1, 40) {
      def take: String = {
        val value = text
        text = ""
        value
      }
    }
		val button = new Button {	text = "Send" }
		val output = new TextArea(5, 40) {
      editable = false
      override def append(t: String) { super.append(t :+ '\n')}
    }

    contents += input
    contents += button
    contents += output

    listenTo(frontend, button)

    reactions += {
      case ButtonClicked(`button`) => {
        frontend call (Echo, input.take)
        frontend call (Reverse, input.take)
        frontend call (Upper, input.take)
        frontend call (Lower, input.take)
      }
      case Replied(value) => output.append(value)
    }
	}

  import javax.swing.UIManager
  UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName)

  def top = new MainFrame {
    title = "Swing Actor Backend Example"
    contents = ui
  }

  override def shutdown() { backend.stop() }
}
