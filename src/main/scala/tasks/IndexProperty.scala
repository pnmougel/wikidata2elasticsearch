package tasks

import java.io.File

import shared.Conf

//import parser.model.datavalues.DataValueSerializer
import parser.model.Property

import scala.collection.mutable

/**
 * Created by nico on 08/02/16.
 */
object IndexProperty extends Indexer {
//  val propertyIdToRef = mutable.HashMap[String, PropertyRef]()
//  val propertyIdToProperty = mutable.HashMap[String, Property]()

  def allProperties(path: File): Unit = {
    if(path.isDirectory) {
      for(f <- path.listFiles()) {
        allProperties(f)
      }
    } else if(path.isFile && path.getName.endsWith(".json")) {
      handleFile(path)
    }
  }

  val allDataTypes = mutable.HashSet[String]()
  val allDataValueTypes = mutable.HashSet[String]()

  def handleFile(file: File) = {
    val item = getJs(file).extract[Property]
//    propertyIdToProperty(item.id) = item
//    val defaultLabel = item.labels.get("en").getOrElse(item.labels.head._2)
//    propertyIdToRef(item.id) = PropertyRef(item.id, defaultLabel.value)
  }
  allProperties(new File(s"${Conf.getString("data.parts")}/P"))

}
