package doctus.scalajs

import doctus.core._
import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.jquery.JQuery
import scala.concurrent.duration.Duration


case class ScalajsGraphics(ctx: CanvasRenderingContext2D) extends DoctusGraphics {

  def drawImage(imgPath: String, x: Int, y: Int, scale: Double): Unit = {
    val image = dom.document.createElement("img").asInstanceOf[dom.HTMLImageElement]
    image.src = "src/main/resources/" + imgPath
    image.alt = "an image"
    ctx.scale(scale, scale)
    try {
      // Might throw a resource not found exception from time to time
      ctx.drawImage(image, x / scale, y / scale)
    } finally {
      ctx.scale(1 / scale, 1 / scale)
    }
  }
  def drawLine(fromx: Int, fromy: Int, tox: Int, toy: Int): Unit = {
    ctx.beginPath();
    ctx.moveTo(fromx, fromy);
    ctx.lineTo(tox, toy);
    ctx.closePath();
    ctx.stroke();
  }
  def fillRect(p1x: Int, p1y: Int, p2x: Int, p2y: Int): Unit = {
    ctx.fillRect(p1x, p1y, p2x, p2y)
  }
  def drawRect(p1x: Int, p1y: Int, p2x: Int, p2y: Int): Unit = {
    ctx.strokeRect(p1x, p1y, p2x, p2y)
  }
  def drawString(str: String, x: Int, y: Int): Unit = {
    ctx.fillText(str, x, y)
  }
  def setColor(c: DoctusColor): Unit = {
    c match {
      case Black => ctx.fillStyle = "black"
      case White => ctx.fillStyle = "white"
    }
  }
  def setFontSize(size: Double): Unit = {
    ctx.font = s"${size.ceil.toInt}px sans-serif"
  }

}


case class ScalajsCanvas(canvas: HTMLCanvasElement) extends DoctusCanvas {
  def onRepaint(f: (DoctusGraphics) => Unit) = ???
  def repaint = ???
  def width = canvas.clientWidth.toInt
  def height = canvas.clientHeight.toInt
}

case class SwingSelect[T](selectBox: JQuery) extends DoctusSelect[T] {
  def addItem(index: Int, item: T): Unit = ???
  def selectedItem: T = ???
}

case class ScalajsButton(startButton: JQuery) extends DoctusButton {
  def onClick(f: () => Unit): Unit = ???
}

case class ScalajsScheduler(canvas: HTMLCanvasElement) extends DoctusScheduler {
  def start(f: () => Unit, duration: Duration) = ???
}


