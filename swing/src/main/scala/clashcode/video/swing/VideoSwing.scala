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

case class SwingDevice(framesPerSecond: Int, params: StageParams) {

  var awtgOpt: Option[Graphics2D] = None

  val canvas = new Panel {

    def determineCalcArea: DrawArea = {
      DrawArea(Pos(0, 0), Rec(this.size.width, this.size.height))
    }
    override def paint(awtg: Graphics2D): Unit = {
      awtgOpt = Some(awtg)
    }
  }

  val comboBox = new ComboBox(List[String]())

  val startButton = new Button("Start")

  val compPanel = new FlowPanel(comboBox, startButton)

  val content = new BorderPanel() {
    add(compPanel, BorderPanel.Position.North)
    add(canvas, BorderPanel.Position.Center)
  }

  val ccanvas: CommonCanvas = new CommonCanvas {
    def width = canvas.size.getWidth().toInt
    def height = canvas.size.getHeight.toInt
  }
  def cgraphics = () => awtgOpt.map(awtg => ImageAwtGraphics(awtg))

  val cselectBox = new CommonSelect[Video] {
    def addItem(index: Int, item: Video): Unit = ???
    def selectedItem: Video = ???
  }

  val cstartButton = new CommonButton {
    def click(f: () => Unit): Unit = {
      startButton.action = new Action("") {
        def apply = f()
      }
    }
  }

  val cschedular: CommonSchedular = new CommonSchedular {
    def start(f: () => Unit, duration: Duration) = ???
  }

  GuiController(ccanvas, cgraphics, cselectBox: CommonSelect[Video],
    cstartButton, cschedular)

  val mf = new MainFrame()
  mf.contents = content
  mf.title = "Akka Workshop Reloaded"
  mf.iconImage = new ImageIcon(getClass.getClassLoader().getResource("icon.png")).getImage
  //mf.size = mf.toolkit.getScreenSize()
  mf.size = new Dimension(800, 600)
  mf.visible = true;

}

case class ImageAwtGraphics(graphics: Graphics2D) extends CommonGraphics {

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

