package clashcode.video.doctus

import scala.concurrent.duration.Duration

case class Pos(x: Int, y: Int)


sealed trait CommonColor
case object Black extends CommonColor
case object White extends CommonColor

trait CommonGraphics {

  def setColor(c: CommonColor)
  def setFontSize(size: Double)

  def drawLine(fromx: Int, fromy: Int, tox: Int, toy: Int)
  def drawRect(p1x: Int, p1y: Int, p2x: Int, p2y: Int)
  def fillRect(p1x: Int, p1y: Int, p2x: Int, p2y: Int)
  def drawImage(imgPath: String, pos: Pos, scale: Double)
  def drawString(str: String, x: Int, y: Int)

}



trait CommonCanvas {
  def onRepaint(f: (CommonGraphics) => Unit): Unit
  def repaint: Unit
  def width: Int
  def height: Int
}

trait CommonSelect[T] {
  def addItem(index: Int, item: T): Unit
  def selectedItem: T
}

trait CommonButton {
  def click(f: () => Unit): Unit
}

trait CommonScheduler {
  def start(f: () => Unit, duration: Duration)
}

