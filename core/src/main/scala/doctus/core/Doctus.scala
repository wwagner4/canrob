package doctus.core

import scala.concurrent.duration.Duration

sealed trait DoctusColor
case object Black extends DoctusColor
case object White extends DoctusColor

trait DoctusGraphics {

  def setColor(c: DoctusColor)
  def setFontSize(size: Double)

  def drawLine(fromx: Int, fromy: Int, tox: Int, toy: Int)
  def drawRect(p1x: Int, p1y: Int, p2x: Int, p2y: Int)
  def fillRect(p1x: Int, p1y: Int, p2x: Int, p2y: Int)
  def drawImage(imgPath: String, x: Int, y: Int, scale: Double)
  def drawString(str: String, x: Int, y: Int)

}

trait CommonCanvas {
  def onRepaint(f: (DoctusGraphics) => Unit): Unit
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

