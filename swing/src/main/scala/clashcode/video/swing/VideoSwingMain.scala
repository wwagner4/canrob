package clashcode.video.swing

import java.awt.Graphics2D
import clashcode.video.swing._
import scala.concurrent.duration._
import clashcode.video.lists._
import clashcode.video.StageParams
import clashcode.video.VideoCreator
import clashcode.video.Device
import clashcode.video.ImageProvider_V01
import clashcode.video.ImageProvider_V02

object VideoSwingMain extends App {

  val framesPerSecond = 20

  //val vl = AkkaWorkshopPresentationVideos.videos
  //val vl = AkkaWorkshopWinnerVideos.winner
  //val vl = AkkaWorkshopWinnerVideos.next
  //val vl = AkkaWorkshopWinnerVideos.noPhilip
  val vl = List(AkkaWorkshopResultsVideos.v001)
  //val vl = List(AkkaWorkshopResultsVideos.v001)

  val params = StageParams(10, ImageProvider_V01, 0.8, 0.05)
  val stages = VideoCreator.create(vl, framesPerSecond)

  val device: Device = new SwingDevice(framesPerSecond, params)
  //val device: Device = new ImagesDevice

  device.playOnes(stages)
  //device.playEndless(stages)

}

