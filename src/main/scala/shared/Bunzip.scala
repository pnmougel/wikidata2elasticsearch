package shared

import java.io.{File, BufferedInputStream, FileInputStream, FileOutputStream}

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream

/**
 * Created by nico on 10/02/16.
 */
object Bunzip {
  val bufferSize = 4096
  val progressBarSize = 50

  val fileSizeUnits = Array("Kb", "Mb", "Gb", "Tb")

  def getFileSizeWithUnit(size: Double): String = {
    var i = 0
    var curSize = size / 1024
    while(curSize > 1024 && i < fileSizeUnits.length) {
      i += 1
      curSize = curSize / 1024
    }
    f"${curSize}%.2f ${fileSizeUnits(i)}"
  }

  def unZip(inFile: File, showProgress: Boolean = true) = {
    if(showProgress) {
      println(s"Unzip ${inFile.getAbsolutePath}")
    }
    val fileSize = inFile.length()

    val fis = new FileInputStream(inFile)
    val channel = fis.getChannel
    val in = new BufferedInputStream(fis)
    val absolutePath = inFile.getAbsolutePath
    val outFile = new File(inFile.getAbsolutePath.substring(0, absolutePath.length - 4))
    if(outFile.exists()) {
      outFile.delete()
    }
    val out = new FileOutputStream(outFile)

    val bzIn = new BZip2CompressorInputStream(in)
    val buffer = new Array[Byte](bufferSize)
    var n = 0
    var totalUncompressed = 0L

    val startTime = System.currentTimeMillis()
    var i = 0

    while (n != -1) {
      i += 1
      n = bzIn.read(buffer)
      totalUncompressed += n
      out.write(buffer, 0, n)

      if(i % 1000 == 0) {
        val done = channel.position()
        val curTime = System.currentTimeMillis()
        val secElapsed = (curTime - startTime).toDouble / 1000
        val bytesPerSec = done.toDouble / secElapsed
        val remainingBytes = fileSize - done
        val secondsRemaining = remainingBytes / bytesPerSec
        val minRemaining = (secondsRemaining / 60).toInt
        val secRemaining = (secondsRemaining - (minRemaining * 60)).toInt
        val secRemainingStr = if(secRemaining < 10) "0" + secRemaining else secRemaining.toString
        val percentRead = done.toDouble / fileSize

        val expectedFileSize = (bzIn.getBytesRead.toDouble / percentRead)

        val nbPlus = (progressBarSize * percentRead).toInt
        val progressBar = s"[${"=" * nbPlus}>${" " * (progressBarSize - nbPlus - 1)}]"
        print(f"\r${progressBar} ${100 * percentRead}%.2f" + "%" + s" \t ${minRemaining}m${secRemainingStr}s remaining \t Expected file size: " + getFileSizeWithUnit(expectedFileSize))
      }
    }
    out.close()
    bzIn.close()

    if(showProgress) {
      println("")
    }
  }
}
