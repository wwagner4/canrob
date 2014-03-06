package clashcode.video

import scala.concurrent.duration.DurationDouble
import clashcode.video.lists.AkkaWorkshopResultsVideos
import doctus.core._

case class GuiController(
  canvas: DoctusCanvas,
  selectBox: DoctusSelect[Video],
  startButton: DoctusButton,
  scheduler: DoctusScheduler) {

  val framesPerSecond = 25
  val params = StageParams(10, ImageProvider_V01, 0.6, 0.07)
  val allVideos = AkkaWorkshopResultsVideos.all

  // Global state
  var index = 0
  var stagesOpt: Option[List[NumberedStage]] = None

  // Fill the select box
  allVideos.zipWithIndex.foreach {
    case (video, index) => {
      selectBox.addItem(index, video)
    }
  }

  // Register callback for start button
  startButton.onClick { () =>
    val video = selectBox.selectedItem
    index = 0
    stagesOpt = Some(VideoCreator.create(List(video), framesPerSecond))
  }

  val d1 = (1000.0 / framesPerSecond).toInt
  scheduler.start(() => canvas.repaint, d1)

  canvas.onRepaint(update)

  def update(cg: DoctusGraphics): Unit = {
    val da: DrawArea = DrawArea(Pos(0, 0), Rec(canvas.width, canvas.height))
    stagesOpt match {
      case Some(stages) => {
        val stage = stages(index)
        stage.stage.paint(cg, da, params)
        if (index >= stages.size - 1) stagesOpt = None
      }
      case None => {
        Intro.stage(index).paint(cg, da, params)
      }
    }
    index += 1
  }
}
