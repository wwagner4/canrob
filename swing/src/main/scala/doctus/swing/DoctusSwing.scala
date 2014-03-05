package doctus.swing

import java.awt.{Color, Graphics2D}
import java.awt.geom.AffineTransform

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.future
import scala.swing.{Action, Button, ComboBox, Panel}

import doctus.core._
import javax.swing.{DefaultComboBoxModel, ImageIcon, JComboBox}

class EasyCanvas extends Panel {

  var paintOpt: Option[(DoctusGraphics) => Unit] = None

  override def paint(g: Graphics2D): Unit = {
    val commonGraphics = SwingGraphics(g)
    paintOpt.foreach(f => f(commonGraphics))
  }
}

case class SwingGraphics(graphics: Graphics2D) extends DoctusGraphics {

  def drawImage(imgPath: String, x: Int, y: Int, scale: Double): Unit = {
    val imgr = getClass().getClassLoader().getResource(imgPath)
    assert(imgr != null, s"Found no resource for ${imgPath}")
    val icon = new ImageIcon(imgr)
    val trans = AffineTransform.getTranslateInstance(x, y)
    trans.concatenate(AffineTransform.getScaleInstance(scale, scale))
    graphics.drawImage(icon.getImage(), trans, null)
  }
  def drawLine(fromx: Int, fromy: Int, tox: Int, toy: Int): Unit = {
    graphics.drawLine(fromx, fromy, tox, toy)
  }
  def drawRect(x: Int, y: Int, w: Int, h: Int): Unit = {
    graphics.drawRect(x, y, w, h)
  }
  def fillRect(x: Int, y: Int, w: Int, h: Int): Unit = {
    graphics.fillRect(x, y, w, h)
  }
  def drawString(str: String, x: Int, y: Int): Unit = {
    graphics.drawString(str, x, y)
  }
  def setColor(c: DoctusColor): Unit = {
    c match {
      case Black => graphics.setColor(Color.BLACK)
      case White => graphics.setColor(Color.WHITE)
    }
  }
  def setFontSize(size: Double): Unit = {
    val font = graphics.getFont()
    graphics.setFont(font.deriveFont(size.toFloat))
  }

}

case class SwingCanvas(canvas: EasyCanvas) extends CommonCanvas {
  def onRepaint(f: (DoctusGraphics) => Unit) = {
    canvas.paintOpt = Some(f)
  }
  def repaint = canvas.repaint
  def width = canvas.size.getWidth().toInt
  def height = canvas.size.getHeight.toInt
}

case class SwingSelect[T](comboBox: ComboBox[T]) extends CommonSelect[T] {
  import javax.swing._
  import scala.swing.ListView

  val model = new DefaultComboBoxModel[T]()
  val peer: JComboBox[T] = comboBox.peer.asInstanceOf[JComboBox[T]]
  peer.setModel(model)

  def addItem(index: Int, item: T): Unit = model.addElement(item)
  def selectedItem: T = peer.getSelectedItem().asInstanceOf[T]
}

case class SwingButton(button: Button) extends CommonButton {
  def click(f: () => Unit): Unit = {
    button.action = new Action(button.text) {
      def apply = f()
    }
  }
}

case class SwingScheduler(canvas: EasyCanvas) extends CommonScheduler {
  import scala.concurrent._
  import scala.concurrent.ExecutionContext.Implicits.global
  def start(f: () => Unit, duration: Duration) = {
    future {
      while (true) {
        f()
        canvas.repaint
        Thread.sleep(duration.toMillis)
      }
    }
  }
}

