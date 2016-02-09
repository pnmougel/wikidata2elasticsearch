import java.io.{RandomAccessFile, PrintWriter}
import java.nio.channels.FileChannel

import scala.io.Source

/**
 * Created by nico on 19/01/16.
 */
object SplitJson {
   val inFile = "/home/nico/data/wikidata/latest-all.json"
//  val inFile = "latest-all.json"
//  val outPath = "parts"
  val outPath = "/home/nico/data/wikidata/parts"

  def main(args: Array[String]) {
//    println(args(0))
//    val outDir = new java.io.File(outPath)
//    if(!outDir.exists()) {
//      outDir.mkdirs()
//    }

    var i = 0
    for(line <- Source.fromFile(inFile).getLines()) {
      if(i % 100000 == 0) {
        println(i)
      }
      i += 1

      val length = line.size
      if(length != 1 && line != "[" && line != "]") {
        val lastChar = line.charAt(length - 1)
        val content = if (lastChar == ',') line.substring(0, length - 1) else line
        val startAt = content.indexOf("\"id\":\"")
        if (startAt == -1) {
          println("Error: Unable to find id")
        } else {
          val start = content.substring(startAt + 6)
          val endAt = start.indexOf("\"")
          if (endAt == -1) {
            println("Error: Unable to find id")
          } else {
            val id = start.substring(0, endAt)
            val idParts = id.splitAt(1)
            val kind = idParts._1
            val num = idParts._2.toInt
            val firstLevel = num / 1000000
            val secondLevel = (num - (1000000 * firstLevel)) / 1000

            val outDirPath = outPath + "/" + kind + "/" + firstLevel + "/" + secondLevel + "/"
            val outDir = new java.io.File(outDirPath)
            if(!outDir.exists()) {
              outDir.mkdirs()
            }

            val outFile = new java.io.File(outDirPath + id + ".json")
            if (outFile.exists()) {
              println("Duplicate id " + id)
            } else {
              val buffer = content.getBytes
              val rwChannel = new RandomAccessFile(outDirPath + id + ".json", "rw").getChannel()
              val wrBuf = rwChannel.map(FileChannel.MapMode.READ_WRITE, 0, buffer.length)
              wrBuf.put(buffer)
              rwChannel.close()
            }
          }
        }
      }
    }
  }
}
//SplitJson.main(args)