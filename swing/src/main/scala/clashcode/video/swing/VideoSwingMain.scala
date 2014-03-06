package clashcode.video.swing

import java.awt.Graphics2D
import clashcode.video.swing._
import scala.concurrent.duration._
import clashcode.video.lists._
import clashcode.video.StageParams
import clashcode.video.VideoCreator
import clashcode.video.ImageProvider_V01
import clashcode.video.ImageProvider_V02
import doctus.swing.EasyCanvas
import clashcode.video.GuiController
import scala.swing.MainFrame
import scala.swing.FlowPanel
import scala.swing.BorderPanel
import scala.swing.Button
import scala.swing.ComboBox
import javax.swing.ImageIcon
import clashcode.video.Video
import doctus.swing.SwingCanvas
import java.awt.Dimension
import doctus.swing.SwingSelect
import doctus.swing.SwingButton
import doctus.swing.SwingScheduler

object VideoSwingMain extends App {


  val canvas = new EasyCanvas()

  val comboBox = new ComboBox(List.empty[Video]) {
    import scala.swing.ListView.Renderer
    val max = 100
    def trim(value: String): String = {
      if (value.size < max) value
      else value.substring(0, max - 3) + "..."
    }
    renderer = Renderer(v => {
      if (v != null) trim(v.text.replace("\n", ""))
      else ""
    })
  }

  val startButton = new Button("Start")

  val mf = new MainFrame() {
    contents = new BorderPanel() {
      val compPanel = new FlowPanel(comboBox, startButton)
      add(compPanel, BorderPanel.Position.North)
      add(canvas, BorderPanel.Position.Center)
    }
    title = "Akka Workshop Reloaded"
    iconImage = new ImageIcon(getClass.getClassLoader().getResource("icon.png")).getImage
    //size = mf.toolkit.getScreenSize()
    size = new Dimension(800, 600)
  }


  val framesPerSecond = 20
  val params = StageParams(10, ImageProvider_V02, 0.8, 0.05)
  //val videos = AkkaWorkshopPresentationVideos.videos
  //val videos = AkkaWorkshopResultsVideos.all
  val videos = AkkaWorkshopResultsVideos.top10
  
  GuiController(SwingCanvas(canvas),
    SwingSelect[Video](comboBox),
    SwingButton(startButton),
    SwingScheduler(canvas),
    framesPerSecond,
    params,
    videos)

  mf.visible = true;

}

