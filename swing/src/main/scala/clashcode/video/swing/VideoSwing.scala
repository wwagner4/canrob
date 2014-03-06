package clashcode.video.swing

import doctus.swing.EasyCanvas
import clashcode.video.StageParams
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
import clashcode.video.ImageProvider_V02
import clashcode.video.lists.AkkaWorkshopResultsVideos
import clashcode.video.GuiParams
import clashcode.video.GuiComponents

case class SwingDevice(framesPerSecond: Int, params: StageParams) {

  // Define the actual swing components 	
  val _canvas = new EasyCanvas()
  val _startButton = new Button("Start")
  val _comboBox = new ComboBox(List.empty[Video]) {
    // Define the rendering of the contents of the combo box
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

  // Define the main frame and layout of the components
  val mf = new MainFrame()
  mf.contents = new BorderPanel() {
    val compPanel = new FlowPanel(_comboBox, _startButton)
    add(compPanel, BorderPanel.Position.North)
    add(_canvas, BorderPanel.Position.Center)
  }
  mf.title = "Akka Workshop Reloaded"
  mf.iconImage = new ImageIcon(getClass.getClassLoader().getResource("icon.png")).getImage
  //mf.size = mf.toolkit.getScreenSize()
  mf.size = new Dimension(800, 600)
  mf.visible = true;

  // Create the necessary wrapper components
  val c = new GuiComponents {
    def canvas = SwingCanvas(_canvas)
    def selectBox = SwingSelect[Video](_comboBox)
    def startButton = SwingButton(_startButton)
    def scheduler = SwingScheduler(_canvas)
  }

  // Define some parameters
  val p = new GuiParams {
    def framesPerSecond = 10
    def stageParams = StageParams(10, ImageProvider_V02, 0.6, 0.07)
    def videos = AkkaWorkshopResultsVideos.all
  }

  // Start the GUI-controller
  GuiController(c, p)

}

