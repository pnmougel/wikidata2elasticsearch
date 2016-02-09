package tasks

import java.io.File
import shared.Conf
import scala.sys.process._

/**
 * Created by nico on 09/02/16.
 */
object Flatten {
  def main(args: Array[String]) {
    val dataPath = s"${Conf.getString("data.path")}/${Conf.getString("dump.file")}.bz2"
    val outDir = new File(dataPath)
    if(!outDir.exists()) {
      println(s"Error: file ${dataPath} is missing. Have you run 'sbt download' first ?")
      System.exit(0)
    }
    println("Uncompressing file. It will take a long time...")
    s"bzip2 -d -f ${dataPath}" !!
  }
}
