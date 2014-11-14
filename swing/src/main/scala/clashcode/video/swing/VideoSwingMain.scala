package clashcode.video.swing

import java.awt.BorderLayout
import java.awt.Component
import java.awt.Dimension
import java.awt.FlowLayout
import clashcode.video.GuiController
import clashcode.video.ImageProvider_V01
import clashcode.video.StageParams
import clashcode.video.Video
import clashcode.video.lists._
import clashcode.video.swing._
import doctus.swing._
import javax.swing.DefaultListCellRenderer
import javax.swing.ImageIcon
import javax.swing.JButton
import javax.swing.JComboBox
import javax.swing.JFrame
import javax.swing.JList
import javax.swing.JPanel
import javax.swing.ListCellRenderer
import javax.swing.JComponent
import javax.swing.JLabel

object VideoSwingMain extends App {

  val canvas = new DoctusPanel()
  val comboBox = new JComboBox[Video]() {

    object TaskCellRenderer extends ListCellRenderer[Video] {
      val peerRenderer: ListCellRenderer[Video] = (new DefaultListCellRenderer).asInstanceOf[ListCellRenderer[Video]]
      override def getListCellRendererComponent(
        list: JList[_ <: Video], video: Video, index: Int,
        isSelected: Boolean, cellHasFocus: Boolean): Component = {
        val component = peerRenderer.getListCellRendererComponent(
          list, video, index, isSelected, cellHasFocus).asInstanceOf[JLabel]
        component.setText(video.text)
        component
      }
    }
    setRenderer(TaskCellRenderer)
  }
  val startButton = new JButton("Start")

  // Define the layout of the components
  val compPanel = new JPanel()
  compPanel.setLayout(new FlowLayout())
  compPanel.add(comboBox)
  compPanel.add(startButton)

  val contents = new JPanel()
  contents.setLayout(new BorderLayout())
  contents.add(compPanel, BorderLayout.NORTH)
  contents.add(canvas, BorderLayout.CENTER)

  val framesPerSecond = 10
  val params = StageParams(10, ImageProvider_V01, 0.8, 0.05)

  //val videos = AkkaWorkshopPresentationVideos.videos
  val videos = AkkaWorkshopResultsVideos.all
  //val videos = AkkaWorkshopResultsVideos.top10

  GuiController(DoctusCanvasSwing(canvas),
    DoctusSelectSwing[Video](comboBox),
    DoctusClickableSwing(startButton),
    DoctusSchedulerSwing,
    framesPerSecond,
    params,
    videos)

  val mf = new JFrame();
  mf.setContentPane(contents)
  mf.setTitle("Akka Workshop Reloaded")
  mf.setIconImage(new ImageIcon(getClass.getClassLoader().getResource("icon.png")).getImage())
  mf.setSize(new Dimension(800, 600))
  mf.setVisible(true)

}

