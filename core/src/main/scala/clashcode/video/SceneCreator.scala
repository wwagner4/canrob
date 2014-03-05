package clashcode.video

import scala.util.Random

import clashcode.robot.{Converter, FieldEvaluator, FieldFactory, FieldPos, FieldState}

case class FieldStep(from: FieldState, to: FieldState)
case class Path(path: List[FieldState], fieldSize: Int)

case object SceneCreator {

  def stringCodeToStages(strCode: String, gameSteps: Option[Int], seed: Long): List[Stage] = {

    val ran = new Random(seed)

    def stepsToStages(steps: List[FieldStep], preRobot: RobotView, fieldSize: Int): List[Stage] = steps match {
      case Nil => Nil
      case head :: tail => {
        val stages = PathUtil.stepToStages(head, preRobot, fieldSize, ran)
        val lastRobot = stages.last.robot
        stages ::: stepsToStages(tail, lastRobot, fieldSize)
      }
    }

    val path = PathUtil.strCodeToPath(strCode, gameSteps, ran)
    val steps = PathUtil.pathToSteps(path.path);
    assert(steps.nonEmpty, "There should be at least one step")
    val startField = steps(0).from
    val startRobot = RobotView(Pos(startField.robot.x * 2 + 1, startField.robot.y * 2 + 1), S)
    delayBeforeAfter(stepsToStages(steps, startRobot, path.fieldSize))
  }

  private def delayBeforeAfter(stages: List[Stage]): List[Stage] = {
    val beforeAfterDelayFrames: Int = 30
    if (stages.isEmpty) Nil
    else {
      val first = stages(0)
      val last = stages.last
      val head = List.fill(beforeAfterDelayFrames)(first)
      val tail = List.fill(beforeAfterDelayFrames)(last)
      head ::: stages ::: tail
    }
  }

}

case object DirectionUtil {
  def turnRight(actualDir: Direction): Direction = actualDir match {
    case N => NE
    case NE => E
    case E => SE
    case SE => S
    case S => SW
    case SW => W
    case W => NW
    case NW => N
  }
  def turnLeft(actualDir: Direction): Direction = actualDir match {
    case N => NW
    case NW => W
    case W => SW
    case SW => S
    case S => SE
    case SE => E
    case E => NE
    case NE => N
  }
  def diff(from: Direction, to: Direction): Int = {
    val nMap: Map[Direction, Int] = List(N -> 0, NE -> 1, E -> 2, SE -> 3, S -> 4, SW -> -3, W -> -2, NW -> -1).toMap
    val neMap: Map[Direction, Int] = List(N -> -1, NE -> 0, E -> 1, SE -> 2, S -> 3, SW -> 4, W -> -3, NW -> -2).toMap
    val eMap: Map[Direction, Int] = List(N -> -2, NE -> -1, E -> 0, SE -> 1, S -> 2, SW -> 3, W -> 4, NW -> -3).toMap
    val seMap: Map[Direction, Int] = List(N -> -3, NE -> -2, E -> -1, SE -> 0, S -> 1, SW -> 2, W -> 3, NW -> 4).toMap
    val sMap: Map[Direction, Int] = List(N -> 4, NE -> -3, E -> -2, SE -> -1, S -> 0, SW -> 1, W -> 2, NW -> 3).toMap
    val swMap: Map[Direction, Int] = List(N -> 3, NE -> 4, E -> -3, SE -> -2, S -> -1, SW -> 0, W -> 1, NW -> 2).toMap
    val wMap: Map[Direction, Int] = List(N -> 2, NE -> 3, E -> 4, SE -> -3, S -> -2, SW -> -1, W -> 0, NW -> 1).toMap
    val nwMap: Map[Direction, Int] = List(N -> 1, NE -> 2, E -> 3, SE -> 4, S -> -3, SW -> -2, W -> -1, NW -> 0).toMap
    val map: Map[Direction, Map[Direction, Int]] = List(N -> nMap, NE -> neMap, E -> eMap, SE -> seMap, S -> sMap, SW -> swMap, W -> wMap, NW -> nwMap).toMap
    map(from)(to)
  }

  def turnList(startDirection: Direction, times: Int): List[Direction] = {
    if (times == 0) Nil
    else if (times > 0) {
      val nextDir = turnRight(startDirection)
      nextDir :: turnList(nextDir, times - 1)
    } else {
      val nextDir = turnLeft(startDirection)
      nextDir :: turnList(nextDir, times + 1)
    }
  }
}

case object PathUtil {

  def pathToSteps(path: List[FieldState]): List[FieldStep] = {
    path match {
      case Nil => throw new IllegalStateException("path must contain at least two positions")
      case a :: Nil => Nil
      case a :: b :: r => FieldStep(a, b) :: pathToSteps(b :: r)
    }
  }

  def strCodeToPath(strCode: String, gameSteps: Option[Int], ran: Random): Path = {
    val code: Array[Byte] = strCode.map(c => (c - 48).toByte).toArray
    val decisions = Converter.toDecisions(code)
    val f = FieldFactory.createRandomField(ran, 10)
    val re = FieldEvaluator.evaluate(decisions, f, ran)
    val stateList = gameSteps match {
      case None => re.path
      case Some(len) => re.path.take(len)
    }
    Path(stateList, re.fieldWidth)
  }

  def mapPos(in: List[FieldPos]): Set[Pos] = in.map(p => Pos(p.x * 2 + 1, p.y * 2 + 1)).toSet

  def stepToStages(step: FieldStep, robot: RobotView, fieldSize: Int, ran: Random): List[GameStage] = {
    def turn(nextDir: Direction): List[GameStage] = {
      val prevDir = robot.dir
      val diff = DirectionUtil.diff(prevDir, nextDir)
      val tl = DirectionUtil.turnList(robot.dir, diff)
      tl.map(d => GameStage(RobotView(robot.pos, d), mapPos(step.from.items)))
    }
    def move(nextDir: Direction): List[GameStage] = nextDir match {
      case N => List(
        GameStage(RobotView(Pos(robot.pos.x, robot.pos.y - 1), nextDir), mapPos(step.from.items)),
        GameStage(RobotView(Pos(robot.pos.x, robot.pos.y - 2), nextDir), mapPos(step.to.items)))
      case E => List(
        GameStage(RobotView(Pos(robot.pos.x + 1, robot.pos.y), nextDir), mapPos(step.from.items)),
        GameStage(RobotView(Pos(robot.pos.x + 2, robot.pos.y), nextDir), mapPos(step.to.items)))
      case S => List(
        GameStage(RobotView(Pos(robot.pos.x, robot.pos.y + 1), nextDir), mapPos(step.from.items)),
        GameStage(RobotView(Pos(robot.pos.x, robot.pos.y + 2), nextDir), mapPos(step.to.items)))
      case W => List(
        GameStage(RobotView(Pos(robot.pos.x - 1, robot.pos.y), nextDir), mapPos(step.from.items)),
        GameStage(RobotView(Pos(robot.pos.x - 2, robot.pos.y), nextDir), mapPos(step.to.items)))
      case _ => throw new IllegalArgumentException(s"Robot can only move N, E, S or W. Not $nextDir")
    }
    if (step.from.robot.x == step.to.robot.x && step.from.robot.y == step.to.robot.y) {
      // Robot did not move
      val nextDir = if (ran.nextBoolean) DirectionUtil.turnRight(robot.dir)
      else DirectionUtil.turnLeft(robot.dir)
      val nextRobot = RobotView(robot.pos, nextDir)
      List(GameStage(robot, mapPos(step.to.items)))
    } else {
      // Robot moved
      val ndir: Direction = nextDirection(step, fieldSize)
      val turns = turn(ndir)
      val moves = move(ndir)
      turns ::: moves
    }
  }

  def nextDirection(step: FieldStep, fieldSize: Int): Direction = {
    def assertInBounds(value: Int): Unit = {
      if (value < 0 || value >= fieldSize) throw new IllegalArgumentException(s"Value $value is out of bound for field size $fieldSize. $step")
    }
    assertInBounds(step.from.robot.x)
    assertInBounds(step.from.robot.y)
    assertInBounds(step.to.robot.x)
    assertInBounds(step.to.robot.y)
    if (step.from.robot.x == step.to.robot.x) {
      if (step.from.robot.y > step.to.robot.y) {
        if (step.from.robot.y - step.to.robot.y > 1) throw new IllegalArgumentException(s"The step in y direction is greater than one. $step")
        else N
      } else if (step.from.robot.y < step.to.robot.y) {
        if (step.to.robot.y - step.from.robot.y > 1) throw new IllegalArgumentException(s"The step in y direction is greater than one. $step")
        else S
      } else throw new IllegalArgumentException(s"No movement. $step")
    } else if (step.from.robot.y == step.to.robot.y) {
      if (step.from.robot.x > step.to.robot.x) {
        if (step.from.robot.x - step.to.robot.x > 1) throw new IllegalArgumentException(s"The step in x direction is greater than one. $step")
        else W
      } else if (step.from.robot.x < step.to.robot.x) {
        if (step.to.robot.x - step.from.robot.x > 1) throw new IllegalArgumentException(s"The step in x direction is greater than one. $step")
        else E
      } else throw new IllegalArgumentException(s"No movement. $step")
    } else throw new IllegalArgumentException(s"Moved along two axes. $step")
  }
}
  
