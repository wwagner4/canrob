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

case class SwingDevice(framesPerSecond: Int, params: StageParams) {

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

  val compPanel = new FlowPanel(comboBox, startButton)

  val content = new BorderPanel() {
    add(compPanel, BorderPanel.Position.North)
    add(canvas, BorderPanel.Position.Center)
  }

  GuiController(SwingCanvas(canvas),
    SwingSelect[Video](comboBox),
    SwingButton(startButton),
    SwingScheduler(canvas))

  val mf = new MainFrame()
  mf.contents = content
  mf.title = "Akka Workshop Reloaded"
  mf.iconImage = new ImageIcon(getClass.getClassLoader().getResource("icon.png")).getImage
  //mf.size = mf.toolkit.getScreenSize()
  mf.size = new Dimension(800, 600)
  mf.visible = true;

}

