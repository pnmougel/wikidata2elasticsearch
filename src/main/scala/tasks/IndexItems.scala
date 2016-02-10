package tasks

import java.io.File

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.IndexDefinition
import com.sksamuel.elastic4s.jackson.ElasticJackson.Implicits._
import org.json4s._
import org.json4s.ext.EnumNameSerializer
import org.json4s.jackson.JsonMethods._
import parser.model.WikiDataItem
import parser.model.enums.{DataType, Rank, SnakType}
import shared.{InitModel, Conf, ElasticSearch, JsonIterator}

/**
 * Created by nico on 05/02/16.
 */
object IndexItems {
  val indexName = Conf.getString("elasticsearch.index")
  implicit val formats = DefaultFormats + new EnumNameSerializer(Rank) + new EnumNameSerializer(DataType) + new EnumNameSerializer(SnakType)
  val client = ElasticSearch.client

  var queryBuffer = Vector[IndexDefinition]()
  def addToQueryBuffer(indexQuery: IndexDefinition) = {
    queryBuffer :+= indexQuery
    if(!Conf.getBoolean("elasticsearch.testOnly") && queryBuffer.size >= Conf.getInt("elasticsearch.bulkSize")) {
      client.execute(bulk(queryBuffer))
      queryBuffer = Vector[IndexDefinition]()
    }
  }

  def transformJs(data: String) : JValue = {
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

  def handleData(data: String, line: Option[Int] = None) = {
    val js = transformJs(data)
    js.extractOpt[WikiDataItem] match {
      case Some(item) =>
        if(item.isItem) {
          val esItem = item.toEsItem
          val indexQuery = index into indexName / "items" source esItem id esItem.id
          addToQueryBuffer(indexQuery)
        } else if(item.isProperty) {
          val esItem = item.toEsProperty
          val indexQuery = index into indexName / "prop" source esItem id esItem.id
          addToQueryBuffer(indexQuery)
        }
      case None =>
        println(s"\nError: unable to parse line ${line.getOrElse("")}")
        println(data)
    }
  }

  def main(args: Array[String]) {
    InitModel.createIndex

    val jsFile = new File(s"${Conf.getString("data.path")}/${Conf.getString("dump.file")}")
    if(jsFile.exists()) {
      new JsonIterator(jsFile).forEach { case (data, i) =>
        handleData(data, Some(i))
      }
      if(queryBuffer.nonEmpty) {
        client.execute(bulk(queryBuffer))
      }
    } else {
      println(s"Error: missing file '${jsFile.getAbsolutePath}'")
    }
  }
}
