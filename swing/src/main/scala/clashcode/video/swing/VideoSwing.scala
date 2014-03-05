package clashcode.video.swing

import java.awt.Graphics2D
import scala.swing.MainFrame
import java.awt.Dimension
import scala.swing.Panel
import java.awt.Color
import clashcode.video._
import javax.imageio.ImageIO
import sun.java2d.pipe.BufferedBufImgOps
import java.awt.geom.AffineTransform
import java.awt.image.AffineTransformOp
import java.awt.image.BufferedImage
import scala.util.Random
import javax.swing.ImageIcon
import scala.swing.BorderPanel
import scala.swing.FlowPanel
import scala.swing.ComboBox
import scala.swing.Button
import scala.swing.Action
import scala.concurrent.duration.Duration
import javax.swing.SwingUtilities
import clashcode.video.doctus._

class EasyCanvas extends Panel {

  var paintOpt: Option[(CommonGraphics) => Unit] = None

  override def paint(g: Graphics2D): Unit = {
    val commonGraphics = SwingGraphics(g)
    paintOpt.foreach(f => f(commonGraphics))
  }
}

case class SwingGraphics(graphics: Graphics2D) extends CommonGraphics {

  def drawImage(imgPath: String, pos: Pos, scale: Double): Unit = {
    val imgr = getClass().getClassLoader().getResource(imgPath)
    assert(imgr != null, s"Found no resource for ${imgPath}")
    val icon = new ImageIcon(imgr)
    val trans = AffineTransform.getTranslateInstance(pos.x, pos.y)
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
  def setColor(c: CommonColor): Unit = {
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
  def onRepaint(f: (CommonGraphics) => Unit) = {
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

case class SwingDevice(framesPerSecond: Int, params: StageParams) {

  val canvas = new EasyCanvas()

  val comboBox = new ComboBox(List.empty[Video]) {
    import scala.swing.ListView.Renderer
    val max = 100
    def trim(value: String): String = {
      if (value.size < max) value
      else value.substring(0, max - 3) + "..."
    }
    renderer = Renderer(v => {
      if (v != null) trim(v.text.replace("\n", ""))
      else ""
    })
  }

  val startButton = new Button("Start")

  val compPanel = new FlowPanel(comboBox, startButton)

  val content = new BorderPanel() {
    add(compPanel, BorderPanel.Position.North)
    add(canvas, BorderPanel.Position.Center)
  }

  GuiController(SwingCanvas(canvas),
    SwingSelect[Video](comboBox),
    SwingButton(startButton),
    SwingScheduler(canvas))

  val mf = new MainFrame()
  mf.contents = content
  mf.title = "Akka Workshop Reloaded"
  mf.iconImage = new ImageIcon(getClass.getClassLoader().getResource("icon.png")).getImage
  //mf.size = mf.toolkit.getScreenSize()
  mf.size = new Dimension(800, 600)
  mf.visible = true;

}

