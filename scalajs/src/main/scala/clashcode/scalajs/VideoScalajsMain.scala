package clashcode.scalajs

import scala.scalajs.js
import scala.scalajs.js.Any.{ fromDouble, fromFunction0, fromInt, fromString, stringOps }
import scala.scalajs.js.Number.toDouble
import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.jquery.{ JQuery, jQuery }
import clashcode.video._
import clashcode.video.lists.AkkaWorkshopResultsVideos
import org.scalajs.dom.HTMLDivElement
import doctus.core._
import doctus.scalajs._

object VideoScalajsMain {

  // Comes here on every refresh (update)
  def main(): Unit = {

    // GUI Components form the HTML-Page
    val _center: HTMLDivElement = dom.document.getElementById("centerDiv").asInstanceOf[HTMLDivElement]
    val _canvas: HTMLCanvasElement = dom.document.getElementById("canvas").asInstanceOf[HTMLCanvasElement]
    val _selectBoxElem = dom.document.getElementById("selectBox")
    val _selectBox: JQuery = jQuery(_selectBoxElem.asInstanceOf[HTMLSelectElement])
    val _startButtonElem = dom.document.getElementById("startButton")
    val _startButton: JQuery = jQuery(_startButtonElem.asInstanceOf[HTMLButtonElement])

    // Adjust the canvas according to surrounding tags
    _canvas.width = _center.clientWidth
    _canvas.height = _canvas.width * 0.6

    // Wrap the platform specific components 
    val c = new GuiComponents {
      def canvas = ScalajsCanvas(_canvas)
      def selectBox = ScalajsSelect[Video](_selectBox, (v: Video) => v.text)
      def startButton = ScalajsButton(_startButton)
      def scheduler = ScalajsScheduler(_canvas)
    }

    // Define some parameters 
    val p = new GuiParams {
      def framesPerSecond = 15
      def stageParams = StageParams(10, ImageProvider_V01, 0.6, 0.07)
      def videos = AkkaWorkshopResultsVideos.all
    }

    // Start the platform independent GUI-controller
    GuiController(c, p)

  }

}

