package doctus.core

import scala.concurrent.duration.Duration

sealed trait DoctusColor
case object Black extends DoctusColor
case object White extends DoctusColor

trait DoctusGraphics {

  def setColor(c: DoctusColor)
  def setFontSize(size: Double)

  def drawLine(x1: Int, y1: Int, x2: Int, y2: Int)
  def drawRect(x1: Int, y1: Int, x2: Int, y2: Int)
  def fillRect(x1: Int, y1: Int, x2: Int, y2: Int)
  def drawImage(imgPath: String, x: Int, y: Int, scale: Double)
  def drawString(str: String, x: Int, y: Int)

}

trait DoctusCanvas {
  def onRepaint(f: (DoctusGraphics) => Unit): Unit
  def repaint: Unit
  def width: Int
  def height: Int
}

trait DoctusSelect[T] {
  def addItem(index: Int, item: T): Unit
  def selectedItem: T
}

trait DoctusButton {
  def onClick(f: () => Unit): Unit
}

trait DoctusScheduler {
  /**
   * duration: in milliseconds
   */
  def start(f: () => Unit, duration: Int)
}

