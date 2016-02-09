package parser

import java.io.File

import org.json4s.DefaultFormats
import org.json4s.JsonAST.{JString, JObject}
import org.json4s.ext.EnumNameSerializer
import org.json4s.jackson.JsonMethods._
//import parser.model.datavalues.DataValueSerializer
import parser.model.{PropertyRef, DataValuesStuff, Property}
import parser.model.enums.{SnakType, DataType, Rank}

import scala.collection.mutable
import scala.io.Source

/**
 * Created by nico on 08/02/16.
 */
object ParseProperty {
  implicit val formats = DefaultFormats + new EnumNameSerializer(Rank) + new EnumNameSerializer(DataType) + new EnumNameSerializer(SnakType)// + new DataValueSerializer

  val propertiesPath = "/home/nico/data/wikidata/parts/P"

  val propertyIdToRef = mutable.HashMap[String, PropertyRef]()
  val propertyIdToProperty = mutable.HashMap[String, Property]()

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
//    println(file.getName)
    val data = Source.fromFile(file).getLines().mkString("\n")
    val js = parse(data) transformField {
      case ("datatype", JString("time")) => ("datatype", JString("Time"))
      case ("datatype", JString("wikibase-property")) => ("datatype", JString("WikibaseProperty"))
      case ("datatype", JString("globe-coordinate")) => ("datatype", JString("GlobeCoordinate"))
      case ("datatype", JString("quantity")) => ("datatype", JString("Quantity"))
      case ("datatype", JString("wikibase-item")) => ("datatype", JString("WikibaseItem"))
      case ("datatype", JString("string")) => ("datatype", JString("Text"))
      case ("datatype", JString("monolingualtext")) => ("datatype", JString("MonolingualText"))
      case ("datatype", JString("commonsMedia")) => ("datatype", JString("CommonsMedia"))
      case ("datatype", JString("url")) => ("datatype", JString("Url"))
    } transformField {
      case ("snaktype", JString("value")) => ("snaktype", JString("HasValue"))
      case ("snaktype", JString("novalue")) => ("snaktype", JString("NoValue"))
      case ("snaktype", JString("somevalue")) => ("snaktype", JString("SomeValue"))
    } transformField {
      case ("rank", JString("preferred")) => ("rank", JString("Preferred"))
      case ("rank", JString("deprecated")) => ("rank", JString("Deprecated"))
      case ("rank", JString("normal")) => ("rank", JString("Normal"))
    }
    val item = js.extract[Property]
    propertyIdToProperty(item.id) = item
    val defaultLabel = item.labels.get("en").getOrElse(item.labels.head._2)
    propertyIdToRef(item.id) = PropertyRef(item.id, defaultLabel.value)


//    for(claims <- item.claims; claim <- claims._2) {
//      for(valll <- claim.mainsnak.datavalue) {
//        println(valll)
//      }
//      DataType.withName(claim.mainsnak.datatype)
//      allDataTypes.add(claim.mainsnak.datatype)
//    }
//    allDataTypes.add(item.datatype)
//    println(item.datatype)
  }
  allProperties(new File(propertiesPath))

//  globecoordinate -> Set(altitude, precision, latitude, longitude, globe)
//  wikibase-entityid -> Set(entity-type, numeric-id)
//  monolingualtext -> Set(text, language)
//  string -> Set(string)
//  quantity -> Set(amount, unit, upperBound, lowerBound)
//  time -> Set(precision, timezone, before, calendarmodel, after, time)

  def main(args: Array[String]) {
    allProperties(new File(propertiesPath))
    // println(allDataTypes.mkString("\n"))
//    println(allDataValueTypes.mkString("\n"))
//    println(DataValuesStuff.valuesClass.mkString("\n"))
    println(DataValuesStuff.typeToFields.mkString("\n"))

  }
}
