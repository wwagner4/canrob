package clashcode.video

import scala.io.Codec

case class ResultEntry(fitness: Int, name: String, code: String) 

object VideoEngine extends App {

  val fileName = "workshop-erge.txt"
  println(s"Reading File '$fileName'")
  //val results: List[ResultEntry] = readFile(fileName).sortBy(_.fitness).reverse.take(100)
  //for (i <- 0 until results.size) printVideo(i + 1, results(i))
  //for (i <- 0 until results.size) printResultList(i + 1, results(i))
  val varList = for (i <- 0 to 100) yield "v%03d" format i
  println(varList.mkString(","))

  private def printVideo(index: Int, result: ResultEntry): Unit = {
    val id = "%03d" format index
    val s = s"""  val v${id} = Video("${result.fitness}\\n\\n${result.name}",
    5.second,
    "${result.code}",
    None,
    1L)
"""
    println(s)
  }


  private def printResultList(index: Int, result: ResultEntry): Unit = {
    val line = "%4d %14d %-80s %s" format (index, result.fitness, result.name, result.code)
    println(line)
  }

  private def readFile(filename: String): List[ResultEntry] = {
    import scala.io.Source
    import java.io.File
    val linesList = Source.fromFile(new File(filename))(Codec.UTF8).getLines().toSet
    val set = linesList.map(parseLine(_))
    set.toList
  }

  private def parseLine(line: String): ResultEntry = {
    val lineItems = line.split("\\s+").filter(_ != "")
    assert(lineItems.size == 3, s"Size of ${lineItems.toList.mkString("##")} must be 3")
    ResultEntry(lineItems(0).trim.toInt, lineItems(1).trim, lineItems(2).trim)
  }
}