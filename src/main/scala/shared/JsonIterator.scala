package shared

import java.io.File
import scala.io.Source

/**
 * Created by nico on 09/02/16.
 */
class JsonIterator(file: File, maxJson: Int = -1, showProgress: Boolean = true) {
  def forEach(f: (String, Int) => Unit): Unit = {
    val startTime = System.currentTimeMillis()
    var i = 0
    var done = 0L
    val fileSize = file.length()

    for (line <- Source.fromFile(file).getLines()) {
      val length = line.size
      done += length

      if (i % 500 == 0 && showProgress) {
        val curTime = System.currentTimeMillis()
        val remainingBytes = fileSize - done
        val secElapsed = (curTime - startTime).toDouble / 1000
        val bytesPerSec = done.toDouble / secElapsed
        val donePercentage = 100 * done.toDouble / fileSize
        val secondsRemaining = remainingBytes / bytesPerSec
        val timeRemaining = if(secondsRemaining > 100) {
          (secondsRemaining / 60, "min.")
        } else {
          (secondsRemaining, "sec.")
        }

        print(f"\rDoing line ${i} (${donePercentage}%.2f" + "%)" + f" at ${bytesPerSec / (1024 * 1024)}%.2f Mb/sec. \t Approx ${timeRemaining._1}%.0f ${timeRemaining._2} remaining")
      }
      if(maxJson != -1 && maxJson < i) {
        return
      }
      i += 1


      if (length != 1 && line != "[" && line != "]") {
        val lastChar = line.charAt(length - 1)
        val content = if (lastChar == ',') line.substring(0, length - 1) else line
        f(content, i)
      }
    }
    if(showProgress) {
      println("")
    }
  }
}
