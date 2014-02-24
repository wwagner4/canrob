package clashcode.video

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

sealed trait CommonColor
case object Black extends CommonColor
case object White extends CommonColor

case class VideoImage(
  imgPath: String, // A path describing the location of the image
  size: Rec, // Size in pixel 
  center: Pos, // Position of the center of the image. (0, 0) would be the upper left corner
  scaleFactor: Double) // Scale factor relative to other used images. 

trait CommonGraphics {

  def drawImage(imgPath: String, pos: Pos, scale: Double)
  def setColor(c: CommonColor)
  def drawLine(fromx: Int, fromy: Int, tox: Int, toy: Int)
  def drawRect(p1x: Int, p1y: Int, p2x: Int, p2y: Int)
  def fillRect(p1x: Int, p1y: Int, p2x: Int, p2y: Int)
  def setFontSize(size: Double)
  def drawString(str: String, x: Int, y: Int)

}

sealed trait Stage {

  def paint(g: CommonGraphics, drawArea: () => DrawArea, params: StageParams): Unit

  // Utillity methods to be used in the implementation of paint

  def clear(g: CommonGraphics, drawArea: DrawArea): Unit = {
    g.setColor(White)
    val x = drawArea.offset.x
    val y = drawArea.offset.y
    val w = drawArea.area.w
    val h = drawArea.area.h
    g.fillRect(x, y, w, h)
  }

}

case class RobotView(pos: Pos, dir: Direction)

case class GameStage(robot: RobotView, cans: Set[Pos]) extends Stage {

  def paint(g: CommonGraphics, drawArea: () => DrawArea, params: StageParams): Unit = {

    // Calculate the current DrawArea
    val da = drawArea()
    val eda = EffectiveField.calc(da, params.widthHeightRatio, params.border)

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
    def paintVideoImage(vimg: VideoImage, pos: Pos): Unit = {
      val effPos: Pos = EffectiveOffset.calc(pos, params.fieldSize, eda)
      val fieldWidth = eda.area.w.toDouble / params.fieldSize
      val scale = (fieldWidth / vimg.size.w) * vimg.scaleFactor
      val imgx = (effPos.x - (vimg.center.x * scale)).toInt
      val imgy = (effPos.y - (vimg.center.y * scale)).toInt
      g.drawImage(vimg.imgPath, Pos(imgx, imgy), scale)
    }

    def paintRobot(pos: Pos, dir: Direction): Unit = {
      paintVideoImage(params.imgProvider.robots(dir), pos)
    }

    clear(g, da)
    val visibleCans = cans - robot.pos
    paintField
    for (canPos <- visibleCans) {
      paintVideoImage(params.imgProvider.can, canPos)
    }
    paintVideoImage(params.imgProvider.robots(robot.dir), robot.pos)
  }
}

case class Text(lines: List[String])

case class TextStage(text: Text) extends Stage {
  def paint(g: CommonGraphics, drawArea: () => DrawArea, params: StageParams): Unit = {

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

    val da = drawArea()
    clear(g, da)
    paintText(text, da)
  }
}

case class NumberedStage(nr: Int, stage: Stage)

trait Device {

  // Define how to paint a stage on that device
  def paintStage(stage: NumberedStage)

  def postPaintStage: Unit = {
    // Do nothing by default
  }

  def playEndless(stages: List[NumberedStage]): Unit = {
    assert(stages.nonEmpty, "Stages must not be empty")
    while (true) {
      stages.foreach(s => {
        paintStage(s)
        postPaintStage
      })
    }
  }

  def playOnes(stages: List[NumberedStage]): Unit = {
    assert(stages.nonEmpty, "Stages must not be empty")
    stages.foreach(s => {
      paintStage(s)
      postPaintStage
    })
  }

}

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



