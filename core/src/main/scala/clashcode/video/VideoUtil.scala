package clashcode.video

object EffectiveField {

  def calc(outer: DrawArea, widthHeightRatio: Double, relBorder: Double): DrawArea = {
    val border = (math.max(outer.area.w, outer.area.h) * relBorder).toInt
    require(widthHeightRatio <= 1.0)
    val outerRatio = (outer.area.h).toDouble / (outer.area.w)
    if (outerRatio <= widthHeightRatio) {
      val h = outer.area.h - 2 * border
      val w = (h / widthHeightRatio).toInt
      val xborder = ((outer.area.w - w) / 2.0).toInt
      val x = outer.offset.x + xborder
	  val y = outer.offset.y + border
      DrawArea(Pos(x, y), Rec(w, h))
    } else {
      val w = outer.area.w - 2 * border
      val h = (w * widthHeightRatio).toInt
      val yborder = ((outer.area.h - h) / 2.0).toInt
      val x = outer.offset.x + border
      val y = outer.offset.y + yborder
      DrawArea(Pos(x, y), Rec(w, h))
    }

  }

}

object EffectiveOffset {
  def calc (pos: Pos, fieldSize: Int, field: DrawArea): Pos = {
    val fw = field.area.w.toDouble / (fieldSize * 2)
    val x = field.offset.x + (fw * pos.x).toInt
    val fh = field.area.h.toDouble / (fieldSize * 2)
    val y = field.offset.y + (fh * pos.y).toInt
    Pos(x, y)
  }
}