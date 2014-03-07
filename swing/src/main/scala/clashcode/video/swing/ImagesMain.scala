package clashcode.video.swing

import java.awt.{ Graphics2D, RenderingHints }
import java.awt.image.BufferedImage
import java.io.File
import clashcode.video.{ DrawArea, ImageProvider_V01, NumberedStage, Rec, StageParams }
import javax.imageio.ImageIO
import doctus.core._
import doctus.swing._
import clashcode.video.Pos
import clashcode.video.lists.AkkaWorkshopResultsVideos
import clashcode.video.VideoCreator
import clashcode.video.Video

/**
 * Creates images in a directory that can be used to create videos afterward
 * by using appropriate tools.
 */
object ImagesMain extends App {

  val stageParams = StageParams(10, ImageProvider_V01, 0.6, 0.03)

  //val res = Rec(3840, 2160) // 2160p
  //val res = Rec(2560, 1440)
  //val res = Rec(1920, 1080)
  //val res = Rec(1600, 900)
  val res = Rec(640, 360)
  val imgFormat = "png" // jpg, png
  
  val video: Video = AkkaWorkshopResultsVideos.v001
  val stages: List[NumberedStage] = VideoCreator.create(List(video), 20)
  stages.foreach(s => paintStage(s))
  def paintStage(stage: NumberedStage): Unit = {
    val bi = new BufferedImage(res.w, res.h, BufferedImage.TYPE_INT_RGB)
    val da = DrawArea(Pos(0, 0), Rec(res.w, res.h))
    val g2 = bi.createGraphics();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    val cg = new SwingGraphics(g2)
    stage.stage.paint(cg, da, stageParams)

    val home = new File(System.getProperty("user.home"))
    val outDir = new File(home, "video")
    val mkdirsOK = if (outDir.exists()) true else outDir.mkdirs()
    if (mkdirsOK) {
      val nr: String = "%05d" format stage.nr
      val fileName = s"img$nr.$imgFormat"
      val file = new File(outDir, fileName)
      val writeOK = ImageIO.write(bi, imgFormat, file)
      if (!writeOK) throw new IllegalStateException(s"Error writing image $fileName")
      println(s"Wrote file $file")
    } else {
      throw new IllegalStateException(s"Error creating directory '$outDir'")
    }
  }
}