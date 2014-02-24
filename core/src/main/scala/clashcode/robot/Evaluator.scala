/**
 * Originally created by Christian Papuschek. 2014 
 */
package clashcode.robot


import scala.util.Random

case class EvalResult(points: Int, path: List[FieldState], fieldWidth: Integer)

/**
 * The evaluator checks how well a robot performs and calculates its points (=fitness).
 *
 * The robot is evaluated on 200 different fields of size 10x10.
 * Those fields are always the same pseudo-randomly generated fields.
 */
object Evaluator {

  private val fieldSize = 10;

  lazy private val testFields = (0 until 200).map(seed => FieldFactory.createRandomField(new Random(seed), fieldSize))

  /** create deterministic random field from given random seed */

  /**
   * get points for given candidate
   * 20 trials on different fields,
   */
  def evaluate(decisions: IndexedSeq[Decision]): EvalResult = {
    val ran = new Random(0)
    var points = 0
    var path = List.empty[FieldState]

    // test on 20 fields
    testFields.foreach(testField => {
      val re = FieldEvaluator.evaluate(decisions, testField, ran)
      points += re.points
      path = re.path ::: path
    })
    EvalResult(points, path.reverse, fieldSize)
  }

}

object FieldEvaluator {

  def evaluate(decisions: IndexedSeq[Decision], testField: Field, ran: Random): EvalResult = {
    var points = 0
    var path = List.empty[FieldState]
    val game = new Game(testField, ran)
    // max 200 robot turns
    var turns = 0
    while (turns < 200 && game.itemCount > 0) {
      turns += 1
      val index = game.situationIndex
      val decision = decisions(index)
      val gs = game.act(decision)
      points += gs.points
      path = gs.state :: path
    }
    EvalResult(points, path.reverse, testField.fieldSize)
  }

}

object FieldFactory {
  def createRandomField(random: Random, fieldSize: Int): Field = {
    // 50% chance for a field to have an item
    val itemCount = fieldSize * fieldSize / 2

    var fieldItemCount = 0
    val items = Array.fill(fieldSize * fieldSize)(false)
    while (fieldItemCount < itemCount) {
      val index = random.nextInt(items.length)
      if (!items(index)) {
        items(index) = true
        fieldItemCount += 1
      }
    }
    Field(fieldSize, itemCount, items)
  }

}