package tasks

import java.io.File

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.IndexDefinition
import com.sksamuel.elastic4s.jackson.ElasticJackson.Implicits._
import org.json4s._
import org.json4s.ext.EnumNameSerializer
import org.json4s.jackson.JsonMethods._
import parser.model.enums.{DataType, Rank, SnakType}
import shared.{Conf, ElasticSearch, JsonIterator}

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by nico on 05/02/16.
 */
object IndexItems {
  implicit val formats = DefaultFormats + new EnumNameSerializer(Rank) + new EnumNameSerializer(DataType) + new EnumNameSerializer(SnakType)
  val client = ElasticSearch.client

  var queryBuffer = Vector[IndexDefinition]()
  def addToQueryBuffer(indexQuery: IndexDefinition) = {
    queryBuffer :+= indexQuery
    if(queryBuffer.size >= Conf.getInt("elasticsearch.bulkSize")) {
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

  def main(args: Array[String]) {
    val indexName = Conf.getString("elasticsearch.index")

    // Delete index if it exists
    client.execute(indexExists(Seq(indexName))).map { f =>
      if(f.isExists && Conf.getBoolean("elasticsearch.deleteIndexIfExists")) {
        client.execute(deleteIndex(indexName)).await
        Thread.sleep(1000)
      }
    }.await


    val jsFile = new File(s"${Conf.getString("data.path")}/${Conf.getString("dump.file")}")
    if(jsFile.exists()) {
      new JsonIterator(jsFile).forEach { data =>
        val js = transformJs(data)
        val item = js.extract[parser.model.WikiDataItem]
        if(item.isItem) {
          val esItem = item.toEsItem
          val indexQuery = index into indexName / "items" source esItem id esItem.id
          addToQueryBuffer(indexQuery)
        } else if(item.isProperty) {
          val esItem = item.toEsProperty
          val indexQuery = index into indexName / "properties" source esItem id esItem.id
          addToQueryBuffer(indexQuery)
        }
      }
      if(!queryBuffer.isEmpty) {
        client.execute(bulk(queryBuffer))
      }
    } else {
      println(s"Error: missing file '${jsFile.getAbsolutePath}'")
    }
  }
}
