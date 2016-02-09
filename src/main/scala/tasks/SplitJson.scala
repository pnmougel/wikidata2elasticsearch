package tasks

import java.io.{File, RandomAccessFile}
import java.nio.channels.FileChannel

import shared.Conf

import scala.io.Source

/**
 * Created by nico on 19/01/16.
 */
object SplitJson {
  val inFilePath = s"${Conf.getString("data.path")}/${Conf.getString("dump.file")}"
  val inFile = new File(inFilePath)
  val outPath = Conf.getString("data.parts")

  var done = 0L
  val fileSize = inFile.length()


  def main(args: Array[String]) {
    val startTime = System.currentTimeMillis()

    var i = 1
    for (line <- Source.fromFile(inFile).getLines()) {

      val length = line.size
      done += length

      if (i % 10000 == 0) {
        val curTime = System.currentTimeMillis()
        val remainingBytes = fileSize - done
        val secElapsed = (curTime - startTime).toDouble / 1000
        val bytesPerSec = done.toDouble / secElapsed
        val donePercentage = 100 * done.toDouble / fileSize
        val minutesRemaining = (remainingBytes / bytesPerSec)

        println(f"Done ${donePercentage}%.0f" + "%" + f" \tat ${bytesPerSec / (1024 * 1024)}%.2f Mb/sec. \tApprox ${minutesRemaining}%.0f sec remaining")
      }
      i += 1


      if (length != 1 && line != "[" && line != "]") {
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
            if (!outDir.exists()) {
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