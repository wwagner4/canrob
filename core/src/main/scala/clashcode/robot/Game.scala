/**
 * Originally created by Christian Papuschek. 2014 
 */
package clashcode.robot

import scala.util.Random
import scala.collection.mutable

case class FieldPos(x: Int, y: Int)
case class FieldState(robot: FieldPos, items: List[FieldPos])
case class GameState(points: Int, state: FieldState)

/** represents a field with items which the robot has to collect */
case class Field(fieldSize: Int, itemCount: Int, items: Seq[Boolean])

/** represents a single game in which the robot has to collect all items on the given field */
class Game(field: Field, random: Random) {

  val items = mutable.ArraySeq(field.items: _*)
  var itemCount = field.itemCount
  var x = 0;
  var y = 0;

  /** get value of cell on position */
  private def cell(x: Int, y: Int): Cell.Value = {
    if (y < 0 || x < 0 || x >= field.fieldSize || y >= field.fieldSize)
      Cell.WALL
    else if (items(y * field.fieldSize + x))
      Cell.STUFF
    else
      Cell.EMPTY
  }

  /** returns the current situation of the robot, represented by a unique index */
  def situationIndex: Int = {
    Situations.getIndex(Situations.getSituation(cell(x, y - 1), cell(x + 1, y), cell(x, y + 1), cell(x - 1, y), cell(x, y)))
  }

  /** pick up item if possible, return points */
  private def pickUp(): GameState = {
    val index = y * field.fieldSize + x
    if (items(index)) {
      itemCount -= 1
      items(index) = false
      GameState(10, FieldState(FieldPos(x, y), ItemConverter.convertItems(items.toList, field.fieldSize))) // success: gain points
    } else
      GameState(-1, FieldState(FieldPos(x, y), ItemConverter.convertItems(items.toList, field.fieldSize))) // lost points
  }


  /** move robot if possible */
  private def move(dx: Int, dy: Int): GameState = {
    val nextX = x + dx
    val nextY = y + dy
    if (cell(nextX, nextY) != Cell.WALL) {
      x = nextX
      y = nextY
      GameState(0, FieldState(FieldPos(x, y), ItemConverter.convertItems(items.toList, field.fieldSize))) // move successful
    } else
      GameState(-5, FieldState(FieldPos(x, y), ItemConverter.convertItems(items.toList, field.fieldSize))) // lost points
  }

  /** act as robot, returns points gained */
  def act(decision: Decision): GameState = {
    decision match {
      case Move(dx, dy) => move(dx, dy)
      case MoveRandom =>
        val randomMove = Decisions.all(random.nextInt(4)).asInstanceOf[Move]
        move(randomMove.x, randomMove.y)
      case Stay => GameState(0, FieldState(FieldPos(x, y), ItemConverter.convertItems(items.toList, field.fieldSize)))
      case PickUp => pickUp()
    }
  }

}

case object ItemConverter {
  def convertItems(items: List[Boolean], fieldSize: Int): List[FieldPos] = {
    val re = for (
      y <- (0 until fieldSize);
      x <- (0 until fieldSize)
    ) yield {
      val index = y * fieldSize + x
      if (items(index)) Some(FieldPos(x, y))
      else None
    }
    val re1 = re.flatten
    re1.toList
  }
  
}