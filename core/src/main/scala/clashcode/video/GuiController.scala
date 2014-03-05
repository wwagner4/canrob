package clashcode.video

import clashcode.video.lists.AkkaWorkshopResultsVideos
import scala.concurrent.duration.Duration
import scala.concurrent.duration._

trait CommonCanvas {
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

trait CommonSchedular {
  def start(f: () => Unit, duration: Duration)
}

case class GuiController(canvas: CommonCanvas, graphics: () => Option[CommonGraphics], selectBox: CommonSelect[Video],
  startButton: CommonButton, schedular: CommonSchedular) {

  val framesPerSecond = 15
  val params = StageParams(10, ImageProvider_V01, 0.9, 0.07)
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
  startButton.click { () =>
    val video = selectBox.selectedItem
    index = 0
    stagesOpt = Some(VideoCreator.create(List(video), framesPerSecond))
  }

  schedular.start(() => update, (1000.0 / framesPerSecond).millis)

  def update: Unit = {
    val da: DrawArea = DrawArea(Pos(0, 0), Rec(canvas.width, canvas.height))
    graphics().foreach(cg => {
      stagesOpt match {
        case Some(stages) => {
          val stage = stages(index)
          stage.stage.paint(cg, () => da, params)
          if (index >= stages.size) stagesOpt = None
        }
        case None => {
          Intro.stage(index).paint(cg, () => da, params)
        }
      }
      index += 1
    })
  }
}
