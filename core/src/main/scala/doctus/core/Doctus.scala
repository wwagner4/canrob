package doctus.core

sealed trait DoctusColor
case object Black extends DoctusColor
case object White extends DoctusColor

/**
 * Some basic drawing functions  
 */
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
  /**
   * Defines a paint function f that gets triggert whenever
   * repaint is called
   */
  def onRepaint(f: (DoctusGraphics) => Unit): Unit
  /**
   * Creates a DoctusGraphics from the underlayin system and calls onRepaint.
   */
  def repaint: Unit
  
  def width: Int
  def height: Int
}

trait DoctusSelect[T] {
  def addItem(index: Int, item: T): Unit
  def selectedItem: T
}

trait DoctusButton {
  /**
   * Defines a function that gets called whenever the button is clicked
   */
  def onClick(f: () => Unit): Unit
}

trait DoctusScheduler {
  /**
   * Calls the function f every 'duration' milliseconds
   */
  def start(f: () => Unit, duration: Int)
}

