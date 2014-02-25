package clashcode.scalajs

import scala.scalajs.js
import scala.scalajs.js.Any.{ fromDouble, fromFunction0, fromInt, fromString, stringOps }
import scala.scalajs.js.Number.toDouble

import org.scalajs.dom
import org.scalajs.dom.{ CanvasRenderingContext2D, HTMLButtonElement, HTMLCanvasElement, HTMLSelectElement }
import org.scalajs.jquery.{ JQuery, jQuery }

import clashcode.video._
import clashcode.video.lists.AkkaWorkshopResultsVideos

object VideoScalajsMain {

  // Comes here on every refresh (update)
  def main(): Unit = {
    // Some configuration
    val framesPerSecond = 15
    val params = StageParams(10, ImageProvider_V01, 0.7, 0.05)
    val videos = AkkaWorkshopResultsVideos.all

    // GUI Components form the HTML-Page
    val canvas: HTMLCanvasElement = dom.document.getElementById("canvas").asInstanceOf[HTMLCanvasElement]
    val selectBoxElem = dom.document.getElementById("selectBox")
    val selectBox: JQuery = jQuery(selectBoxElem.asInstanceOf[HTMLSelectElement])
    val startButtonElem = dom.document.getElementById("startButton")
    val startButton: JQuery = jQuery(startButtonElem.asInstanceOf[HTMLButtonElement])
    
    // Global state
    var index = 0
    var stagesOpt: Option[List[NumberedStage]] = None

    // Fill the select box
    videos.zipWithIndex.foreach {
      case (video, index) => {
        val value = index
        val posi = "%5d." format (index + 1)
        val label = s"$posi ${video.text}"
        val opt = (jQuery("<option/>")).attr("value", value).html(label)
        selectBox.append(opt)
      }
    }

    // Register callback for start button
    startButton.click { () =>
      val videoIndex = selectBox.value().asInstanceOf[js.String]
      val video = videos(videoIndex.toInt)
      index = 0
      stagesOpt = Some(VideoCreator.create(List(video), framesPerSecond))
    }

    // Create common graphics for painting stages
    val cg: CommonGraphics = {
      val ctx: CanvasRenderingContext2D = canvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
      ScalajsGraphics(ctx)
    }
    // Create DrawArea for painting stages
    val da: DrawArea = DrawArea(Pos(0, 0), Rec(canvas.width.toInt, canvas.height.toInt))

    dom.setInterval(() => update, (1000.0 / framesPerSecond).ceil.toInt)

    def update: Unit = {
      stagesOpt foreach (stages => {
        val stage = stages(index)
        // This is where the magic takes place
        stage.stage.paint(cg, () => da, params)
        index += 1
        if (index >= stages.size) stagesOpt = None
      })
    }

  }

}

case class ScalajsGraphics(ctx: CanvasRenderingContext2D) extends CommonGraphics {

  def drawImage(imgPath: String, pos: Pos, scale: Double): Unit = {
    val image = dom.document.createElement("img").asInstanceOf[dom.HTMLImageElement]
    image.src = "src/main/resources/" + imgPath
    image.alt = "an image"
    ctx.scale(scale, scale)
    try {
      // Might throw a resource not found exception from time to time
      ctx.drawImage(image, pos.x / scale, pos.y / scale)
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
  def setColor(c: CommonColor): Unit = {
    c match {
      case Black => ctx.fillStyle = "black"
      case White => ctx.fillStyle = "white"
    }
  }
  def setFontSize(size: Double): Unit = {
    ctx.font = s"${size.ceil.toInt}px sans-serif"
  }

}
