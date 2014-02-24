package clashcode.video

import org.scalatest.FunSuite
import clashcode.robot.Evaluator
import clashcode.robot.Converter
import clashcode.robot.FieldPos
import clashcode.video._
import scala.util.Random
import clashcode.robot.FieldState

class SceneCreatorSuite extends FunSuite {

  val dummyCans = Set.empty[Pos]
  val fieldSize = 10
  
  test("Create path from result with original Evaluator") {
    def strCodeToPath(strCode: String): List[FieldState] = {
      val code: Array[Byte] = strCode.map(c => (c - 48).toByte).toArray
      val decisions = Converter.toDecisions(code)
      Evaluator.evaluate(decisions).path
    }
    val strCode = "03530311022335213110315511111120251141140200400110522540004423424544141444444444142541204404414145445445424454151340002434334143"
    val path = strCodeToPath(strCode)
    assert(path.length === 40000)
  }

  test("Create path from result with PathUtil") {
    val strCode = "03530311022335213110315511111120251141140200400110522540004423424544141444444444142541204404414145445445424454151340002434334143"
    val path = PathUtil.strCodeToPath(strCode, None, new Random(200))
    assert(path.path.length === 200)
  }

  test("String code to scenes") {
    val strCode = "03530311022335213110315511111120251141140200400110522540004423424544141444444444142541204404414145445445424454151340002434334143"
    val stages = SceneCreator.stringCodeToStages(strCode, None, 24234L)
    assert(stages.size > 100)
  }

  val items = List.empty[FieldPos]

  test("Split path to steps") {
    val strCode = "03530311022335213110315511111120251141140200400110522540004423424544141444444444142541204404414145445445424454151340002434334143"
    val path = PathUtil.strCodeToPath(strCode, None, new Random(200)).path.take(4)
    val steps: List[FieldStep] = PathUtil.pathToSteps(path)
    assert(steps.size === 3)
  }


  test("step [0 0] [0 1] robot N") {
    val step = FieldStep(FieldState(FieldPos(0, 0), items), FieldState(FieldPos(0, 1), items))
    val robot = RobotView(Pos(1, 1), N)
    val stages: List[Stage] = PathUtil.stepToStages(step, robot, fieldSize, new Random(2))
    val expectedStages = List(
      GameStage(RobotView(Pos(1, 1), NE), dummyCans),
      GameStage(RobotView(Pos(1, 1), E), dummyCans),
      GameStage(RobotView(Pos(1, 1), SE), dummyCans),
      GameStage(RobotView(Pos(1, 1), S), dummyCans),
      GameStage(RobotView(Pos(1, 2), S), dummyCans),
      GameStage(RobotView(Pos(1, 3), S), dummyCans))
    assert(stages === expectedStages)
  }

  test("step [0 0] [1 0] robot N") {
    val step = FieldStep(FieldState(FieldPos(0, 0), items), FieldState(FieldPos(1, 0), items))
    val robot = RobotView(Pos(1, 1), N)
    val stages: List[Stage] = PathUtil.stepToStages(step, robot, fieldSize, new Random(123))
    val expectedStages = List(
      GameStage(RobotView(Pos(1, 1), NE), dummyCans),
      GameStage(RobotView(Pos(1, 1), E), dummyCans),
      GameStage(RobotView(Pos(2, 1), E), dummyCans),
      GameStage(RobotView(Pos(3, 1), E), dummyCans))
    assert(stages === expectedStages)
  }

  test("step [0 1] [0 0] robot W") {
    val step = FieldStep(FieldState(FieldPos(0, 1), items), FieldState(FieldPos(0, 0), items))
    val robot = RobotView(Pos(1, 3), W)
    val stages: List[Stage] = PathUtil.stepToStages(step, robot, fieldSize, new Random(123))
    val expectedStages = List(
      GameStage(RobotView(Pos(1, 3), NW), dummyCans),
      GameStage(RobotView(Pos(1, 3), N), dummyCans),
      GameStage(RobotView(Pos(1, 2), N), dummyCans),
      GameStage(RobotView(Pos(1, 1), N), dummyCans))
    assert(stages === expectedStages)
  }

  {
    case class NextDirResult(fromx: Int, fromy: Int, tox: Int, toy: Int, expectedDir: Direction)
    val validSteps = List(
      NextDirResult(0, 0, 0, 1, S),
      NextDirResult(0, 0, 1, 0, E),
      NextDirResult(0, 1, 0, 0, N),
      NextDirResult(1, 1, 0, 1, W),
      NextDirResult(1, 1, 1, 0, N),
      NextDirResult(1, 1, 1, 2, S),
      NextDirResult(2, 1, 1, 1, W))

    for (ndr <- validSteps) {
      test(s"next direction valid step $ndr") {
        val s = FieldStep(FieldState(FieldPos(ndr.fromx, ndr.fromy), items), FieldState(FieldPos(ndr.tox, ndr.toy), items))
        val nd = PathUtil.nextDirection(s, fieldSize)
        assert(nd === ndr.expectedDir)
      }
    }
  }
  {
    case class NextDirResult(fromx: Int, fromy: Int, tox: Int, toy: Int)
    val invalidSteps = List(
      NextDirResult(0, 0, 0, 0),
      NextDirResult(0, 0, 1, 1),
      NextDirResult(1, 1, 1, 3),
      NextDirResult(0, 1, -1, 1),
      NextDirResult(1, 1, 4, 2),
      NextDirResult(2, 2, 2, 4))

    for (ndr <- invalidSteps) {
      test(s"next direction valid step $ndr") {
        val s = FieldStep(FieldState(FieldPos(ndr.fromx, ndr.fromy), items), FieldState(FieldPos(ndr.tox, ndr.toy), items))
        intercept[IllegalArgumentException] {
          PathUtil.nextDirection(s, fieldSize)
        }
      }
    }
  }
  {
    case class DiffResult(from: Direction, to: Direction, expected: Int)
    val situations = List(
      DiffResult(N, NE, 1),
      DiffResult(N, E, 2),
      DiffResult(N, S, 4),
      DiffResult(N, SW, -3),
      DiffResult(E, E, 0),
      DiffResult(N, N, 0),
      DiffResult(S, S, 0),
      DiffResult(W, W, 0),
      DiffResult(W, N, 2),
      DiffResult(SW, N, 3),
      DiffResult(S, N, 4),
      DiffResult(SE, N, -3),
      DiffResult(NE, N, -1),
      DiffResult(E, W, 4),
      DiffResult(E, S, 2),
      DiffResult(E, N, -2))
    for (s <- situations) {
      test(s"Direction diff $s") {
        val d = DirectionUtil.diff(s.from, s.to)
        assert(d === s.expected)
      }
    }
  }
  {
    case class TurnListResult(startDir: Direction, times: Int, expected: List[Direction])
    val situations = List(
      TurnListResult(N, 1, List(NE)),
      TurnListResult(N, 0, Nil),
      TurnListResult(N, 2, List(NE, E)),
      TurnListResult(W, 2, List(NW, N)),
      TurnListResult(N, -1, List(NW)))
    for (s <- situations) {
      test(s"Turn list $s") {
        val l = DirectionUtil.turnList(s.startDir, s.times)
        assert(l === s.expected)
      }
    }
  }

}


