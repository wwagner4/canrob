package clashcode.scalajs

import scala.scalajs.js.Any.{ fromDouble, fromFunction0, fromInt, fromString, stringOps }
import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.jquery.{ JQuery, jQuery }
import clashcode.video._
import clashcode.video.lists.AkkaWorkshopResultsVideos
import org.scalajs.dom.HTMLDivElement
import doctus.core._
import doctus.scalajs._
import scala.scalajs.js.annotation.JSExport

@JSExport
object VideoScalajsMain {

  // Comes here on every refresh (update)
  @JSExport
  def main(): Unit = {
    println("-- Entered VideoScalajsMain")
    // GUI Components form the HTML-Page
    val center: HTMLDivElement = dom.document.getElementById("centerDiv").asInstanceOf[HTMLDivElement]
    val canvas: HTMLCanvasElement = dom.document.getElementById("canvas").asInstanceOf[HTMLCanvasElement]
    val selectBoxElem = dom.document.getElementById("selectBox")
    val selectBox: JQuery = jQuery(selectBoxElem.asInstanceOf[HTMLSelectElement])
    val startButtonElem = dom.document.getElementById("startButton")
    val startButton: JQuery = jQuery(startButtonElem.asInstanceOf[HTMLButtonElement])

    canvas.width = center.clientWidth
    canvas.height = (canvas.width * 0.7).toInt

    // Some configuration
    val framesPerSecond = 15
    val params = StageParams(10, ImageProvider_V01, 0.7, 0.07)
    val allVideos = AkkaWorkshopResultsVideos.all

    // Wrap the javascript components
    val dcanvas: DoctusCanvas = DoctusCanvasScalajs(canvas)
    val dselectBox: DoctusSelect[Video] = DoctusSelectScalajs[Video](selectBox, (v: Video) => v.text)
    val dstartButton: DoctusButton = DoctusButtonScalajs(startButton)
    val dscheduler: DoctusScheduler = DoctusSchedulerScalajs
    
    // Start the platform independent controller
    GuiController(dcanvas, dselectBox, dstartButton, dscheduler, framesPerSecond, params, allVideos)

  }

}

