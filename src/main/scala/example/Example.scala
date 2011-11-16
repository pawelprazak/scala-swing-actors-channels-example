package example

import scala.swing.event.ButtonClicked
import scala.swing._
import swing.Take


object Example extends SimpleSwingApplication {
  val backend = Backend()
  val frontend = Frontend(backend)

	lazy val ui = new BoxPanel(Orientation.Vertical) {
    val input = new TextArea(1, 50) with Take
		val sendButton = new Button {	text = "Send" }
    val x = new TextArea(1, 5) with Take
    val y = new TextArea(1, 5) with Take
    val computeButton = new Button { text = "Compute" }
		val output = new TextArea(12, 50) {
      editable = false
      override def append(t: String) { super.append(t :+ '\n')}
    }

    contents += new FlowPanel(input, sendButton)
    contents += new FlowPanel(x, y, computeButton)
    contents += output

    listenTo(frontend, sendButton, computeButton)

    reactions += {
      case ButtonClicked(`sendButton`) => {
        val text = input.take
        frontend call (Echo, text)
        frontend call (Reverse, text)
        frontend call (Upper, text)
        frontend call (Lower, text)
      }
      case ButtonClicked(`computeButton`) => {
        val (xv, yv) = (x.take.toInt, y.take.toInt)
        frontend call (Add, xv, yv)
        frontend call (Sub, xv, yv)
        frontend call (Mul, xv, yv)
        frontend call (Div, xv, yv)
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
}
