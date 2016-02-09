package tasks

import java.io.File

import org.json4s.DefaultFormats
import org.json4s.JsonAST.{JValue, JString}
import org.json4s.ext.EnumNameSerializer
import org.json4s.jackson.JsonMethods._
import parser.model.enums.{SnakType, DataType, Rank}

import scala.io.Source

/**
 * Created by nico on 09/02/16.
 */
class Indexer {
  implicit val formats = DefaultFormats + new EnumNameSerializer(Rank) + new EnumNameSerializer(DataType) + new EnumNameSerializer(SnakType)

  def getJs(file: File) : JValue = {
    val data = Source.fromFile(file).getLines().mkString("\n")
    parse(data) transformField {
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
  }
}
