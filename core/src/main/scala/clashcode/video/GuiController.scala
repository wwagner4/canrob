package clashcode.video

import scala.concurrent.duration.DurationDouble
import clashcode.video.lists.AkkaWorkshopResultsVideos
import doctus.core._

trait GuiComponents {
  def canvas: DoctusCanvas
  def selectBox: DoctusSelect[Video]
  def startButton: DoctusButton
  def scheduler: DoctusScheduler
}

trait GuiParams {
  def framesPerSecond: Int
  def stageParams: StageParams
  def videos: List[Video]
}

/**
 * Implementation of the platform independent GUI-controller 
 */
case class GuiController(c: GuiComponents, p: GuiParams) {

  // Global state
  var index = 0
  var stagesOpt: Option[List[NumberedStage]] = None

  // Fill the select box
  p.videos.zipWithIndex.foreach {
    case (video, index) => {
      c.selectBox.addItem(index, video)
    }
  }

  // Define callback for start button
  c.startButton.onClick { () =>
    val video = c.selectBox.selectedItem
    index = 0
    stagesOpt = Some(VideoCreator.create(List(video), p.framesPerSecond))
  }

  // Define callback for repaint
  c.canvas.onRepaint { (cg: DoctusGraphics) =>
    val da: DrawArea = DrawArea(Pos(0, 0), Rec(c.canvas.width, c.canvas.height))
    stagesOpt match {
      case Some(stages) => {
        val stage = stages(index)
        stage.stage.paint(cg, da, p.stageParams)
        if (index >= stages.size - 1) stagesOpt = None
      }
      case None => {
        Intro.stage(index).paint(cg, da, p.stageParams)
      }
    }
    index += 1
  }

  // Start the scheduler
  c.scheduler.start(() => c.canvas.repaint, (1000.0 / p.framesPerSecond).toInt)

}
