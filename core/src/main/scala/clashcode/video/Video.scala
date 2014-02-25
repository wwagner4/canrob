package clashcode.video
import scala.concurrent.duration._
import javax.sound.midi.Sequence

case class Video(
    text: String, // Text displayed before the video starts 
    textDuration: Duration, // Duration the text is displayed
    code: String, // Code of the robot to be displayed
    gameSteps: Option[Int], // Number of robot steps to be displayed. None means all steps
    seed: Long) // Seed for random populating the feeld with cans

case object VideoCreator {
  
  def create(videos: List[Video], framesPerSecond: Int): List[NumberedStage] = {

    def createOne(video: Video): List[Stage] = {
      val txt = video.text.split("\n").toList
      val dur = video.textDuration.toMillis.toDouble / 1000
      val framesCount = math.max((framesPerSecond * dur).toInt, 1)
      val txtStages = List.fill(framesCount)(TextStage(Text(txt)))
      val gameStages = SceneCreator.stringCodeToStages(video.code, video.gameSteps, video.seed)
      txtStages ::: gameStages
    }

    def createStages(videos: List[Video], framesPerSecond: Int): List[Stage] = {
      videos match {
        case Nil => Nil
        case head :: tail => {
          val headStages = createOne(head)
          assert(headStages.nonEmpty, "Every video must create at least one stage")
          headStages ::: createStages(tail, framesPerSecond)
        }
      }
    }
    
    val stages = createStages(videos, framesPerSecond)
    stages.zip(Stream.from(0, 1)).map { case (stage, nr) => NumberedStage(nr, stage) }
  }

}