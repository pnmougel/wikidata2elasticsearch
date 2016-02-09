package shared

import java.io.File

import scala.io.Source

/**
 * Created by nico on 09/02/16.
 */
class JsonIterator(file: File, maxJson: Int = -1, showProgress: Boolean = true) {
  def forEach(f: String => Unit): Unit = {
    val startTime = System.currentTimeMillis()
    var i = 0
    var done = 0L
    val fileSize = file.length()

    for (line <- Source.fromFile(file).getLines()) {
      val length = line.size
      done += length

      if (i % 100 == 0 && showProgress) {
        val curTime = System.currentTimeMillis()
        val remainingBytes = fileSize - done
        val secElapsed = (curTime - startTime).toDouble / 1000
        val bytesPerSec = done.toDouble / secElapsed
        val donePercentage = 100 * done.toDouble / fileSize
        val minutesRemaining = remainingBytes / bytesPerSec

        print(f"\rDone ${donePercentage}%.0f" + "%" + f" \tat ${bytesPerSec / (1024 * 1024)}%.2f Mb/sec. \tApprox ${minutesRemaining}%.0f sec remaining")
      }
      if(maxJson != -1 && maxJson < i) {
        return
      }
      i += 1


      if (length != 1 && line != "[" && line != "]") {
        val lastChar = line.charAt(length - 1)
        val content = if (lastChar == ',') line.substring(0, length - 1) else line
        f(content)
      }
    }
    if(showProgress) {
      println("")
    }
  }
}
