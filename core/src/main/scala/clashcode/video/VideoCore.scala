package clashcode.video

import doctus.core._



case class Pos(x: Int, y: Int)

case class Rec(w: Int, h: Int)

case class DrawArea(offset: Pos, area: Rec)

sealed trait Direction

case object N extends Direction
case object NE extends Direction
case object E extends Direction
case object SE extends Direction
case object S extends Direction
case object SW extends Direction
case object W extends Direction
case object NW extends Direction

trait ImageProvider {

  def robots: Map[Direction, VideoImage]
  def can: VideoImage

}

case class StageParams(
  fieldSize: Int,
  imgProvider: ImageProvider,
  widthHeightRatio: Double, border: Double)

case class VideoImage(
  imgPath: String, // A path describing the location of the image
  size: Rec, // Size in pixel 
  center: Pos, // Position of the center of the image. (0, 0) would be the upper left corner
  scaleFactor: Double) // Scale factor relative to other used images. 

sealed trait Stage {

  // Paints a complete frame of the video
  def paint(g: CommonGraphics, drawArea: DrawArea, params: StageParams): Unit

  // Utillity methods to be used in the implementation of paint

  def clear(g: CommonGraphics, drawArea: DrawArea): Unit = {
    g.setColor(White)
    val x = drawArea.offset.x
    val y = drawArea.offset.y
    val w = drawArea.area.w
    val h = drawArea.area.h
    g.fillRect(x, y, w, h)
  }

  protected def paintVideoImage(g: CommonGraphics, vimg: VideoImage, pos: Pos, eda: DrawArea, params: StageParams): Unit = {
    val effPos: Pos = EffectiveOffset.calc(pos, params.fieldSize, eda)
    val fieldWidth = eda.area.w.toDouble / params.fieldSize
    val scale = (fieldWidth / vimg.size.w) * vimg.scaleFactor
    val imgx = (effPos.x - (vimg.center.x * scale)).toInt
    val imgy = (effPos.y - (vimg.center.y * scale)).toInt
    g.drawImage(vimg.imgPath, imgx, imgy, scale)
  }

}

case class RobotView(pos: Pos, dir: Direction)

case class GameStage(robot: RobotView, cans: Set[Pos]) extends Stage {

  def paint(g: CommonGraphics, drawArea: DrawArea, params: StageParams): Unit = {

    // Calculate the current DrawArea. Draw area is a lambda because it might change 
    // during showing the video. E.g. the swing device is resizeable
    // Effective Field is the field without borders
    val eda = EffectiveField.calc(drawArea, params.widthHeightRatio, params.border)

    def paintField: Unit = {
      g.setColor(Black)
      def paintRaster: Unit = {
        val fw = eda.area.w / (params.fieldSize)
        val fh = eda.area.h / (params.fieldSize)
        (0 to (params.fieldSize) - 1).foreach(i => {
          val d = i * fw
          g.drawLine(eda.offset.x + d, eda.offset.y, eda.offset.x + d, eda.offset.y + eda.area.h)
        })
        (0 to (params.fieldSize) - 1).foreach(i => {
          val d = i * fh
          g.drawLine(eda.offset.x, eda.offset.y + d, eda.offset.x + eda.area.w, eda.offset.y + d)
        })
      }
      //paintRaster
      g.drawRect(eda.offset.x, eda.offset.y, eda.area.w, eda.area.h)
    }
    def paintRobot(pos: Pos, dir: Direction): Unit = {
      paintVideoImage(g, params.imgProvider.robots(dir), pos, eda, params)
    }

    // Paint one frame
    clear(g, drawArea)
    val visibleCans = cans - robot.pos
    paintField
    for (canPos <- visibleCans) {
      paintVideoImage(g, params.imgProvider.can, canPos, eda, params)
    }
    paintVideoImage(g, params.imgProvider.robots(robot.dir), robot.pos, eda, params)
  }
}

case object Intro {
  
  def stage(index: Int): Stage = {
    IntroStage(index)
  }
  
}

case class IntroStage(index: Int) extends Stage {

  def paint(g: CommonGraphics, drawArea: DrawArea, params: StageParams): Unit = {

    // Effective Field is the field without borders
    val eda = EffectiveField.calc(drawArea, params.widthHeightRatio, params.border)

    def direction: Direction = {
      index % 8 match {
        case 0 => N
        case 1 => NE
        case 2 => E
        case 3 => SE
        case 4 => S
        case 5 => SW
        case 6 => W
        case 7 => NW
      }
    }
    
    // Paint one frame
    clear(g, drawArea)
    paintVideoImage(g, params.imgProvider.robots(direction), Pos(params.fieldSize, params.fieldSize), eda, params)
  }
}

case class Text(lines: List[String])

case class TextStage(text: Text) extends Stage {
  def paint(g: CommonGraphics, drawArea: DrawArea, params: StageParams): Unit = {

    def paintText(text: Text, drawArea: DrawArea) = {
      g.setColor(Black)
      val fontSize = drawArea.area.h.toFloat / 20
      g.setFontSize(fontSize)
      val lines = text.lines
      for (i <- 0 until lines.size) {
        if (i == 1) {
          val fontSize = drawArea.area.h.toFloat / 40
          g.setFontSize(fontSize)
        }
        val y = (10 + fontSize * (i + 1)).toInt
        g.drawString(lines(i), 30, y)
      }
    }
    clear(g, drawArea)
    paintText(text, drawArea)
  }
}

case class NumberedStage(nr: Int, stage: Stage)

object ImageProvider_V02 extends ImageProvider {

  lazy val robots: Map[Direction, VideoImage] = {
    val imgNames = List(
      (S, "img/robots/r00.png"),
      (SE, "img/robots/r01.png"),
      (E, "img/robots/r02.png"),
      (NE, "img/robots/r03.png"),
      (N, "img/robots/r04.png"),
      (NW, "img/robots/r05.png"),
      (W, "img/robots/r06.png"),
      (SW, "img/robots/r07.png"))
    imgNames.map { case (key, name) => (key, VideoImage(name, Rec(300, 300), Pos(150, 160), 2.0)) }.toMap
  }

  lazy val can: VideoImage = {
    VideoImage("img/cans/can.png", Rec(300, 300), Pos(150, 150), 2.0)
  }
}

object ImageProvider_V01 extends ImageProvider {

  lazy val robots: Map[Direction, VideoImage] = {
    val imgNames = List(
      (S, "img01/robots/r00.png"),
      (SE, "img01/robots/r01.png"),
      (E, "img01/robots/r02.png"),
      (NE, "img01/robots/r03.png"),
      (N, "img01/robots/r04.png"),
      (NW, "img01/robots/r05.png"),
      (W, "img01/robots/r06.png"),
      (SW, "img01/robots/r07.png"))
    imgNames.map { case (key, name) => (key, VideoImage(name, Rec(600, 600), Pos(300, 400), 2.4)) }.toMap
  }

  lazy val can: VideoImage = {
    VideoImage("img01/cans/can.png", Rec(300, 300), Pos(150, 180), 1.6)
  }
}



