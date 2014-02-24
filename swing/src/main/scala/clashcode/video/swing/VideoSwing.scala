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

case class SwingDevice(framesPerSecond: Int, params: StageParams)
  extends Device {

  var _stage: Option[NumberedStage] = None

  def paintStage(stage: NumberedStage) = {
    _stage = Some(stage)
    panel.repaint
  }

  override def postPaintStage: Unit = {
    Thread.sleep((1000.0 / framesPerSecond).toInt);
  }

  val panel = new Panel {

    def determineCalcArea: DrawArea = {
      DrawArea(Pos(0, 0), Rec(this.size.width, this.size.height))
    }
    override def paint(awtg: Graphics2D): Unit = {
      val cg: CommonGraphics = new ImageAwtGraphics(awtg)
      _stage match {
        case Some(s) => {
          s.stage.paint(cg, () => determineCalcArea, params)
        }
        case None => // Nothing to be done
      }
    }
  }

  val mf = new MainFrame()
  mf.contents = panel
  mf.title = "Akka Workshop Reloaded"
  mf.iconImage = new ImageIcon(getClass.getClassLoader().getResource("icon.png")).getImage
  mf.size = mf.toolkit.getScreenSize()
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

