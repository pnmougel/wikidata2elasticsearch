package tasks

import java.io.File
import shared.{Bunzip, Conf}

/**
 * Created by nico on 09/02/16.
 */
object Unzip {
  def main(args: Array[String]) {
    val dataPath = s"${Conf.getString("data.path")}/${Conf.getString("dump.file")}.bz2"
    val outDir = new File(dataPath)
    if(!outDir.exists()) {
      println(s"Error: file ${dataPath} is missing. Have you run 'sbt download' first ?")
      System.exit(0)
    }
    Bunzip.unZip(outDir)
  }
}
