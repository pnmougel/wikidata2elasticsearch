package tasks

import java.io.File

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.jackson.ElasticJackson
import com.sksamuel.elastic4s.jackson.ElasticJackson.Implicits._
import org.json4s._
import parser.esmodel.EsWikiDataItem
import shared.{Conf, ElasticSearch}

/**
 * Created by nico on 05/02/16.
 */
object IndexItems extends Indexer {
//  implicit val formats = DefaultFormats + new EnumNameSerializer(Rank) + new EnumNameSerializer(DataType) + new EnumNameSerializer(SnakType)

  val nbFiles = 100
  var curNbFile = 0
  val client = ElasticSearch.client

  def parseRecur(file: File, level: Int = 0): Option[EsWikiDataItem] = {
    if(curNbFile < nbFiles || nbFiles < 0) {
      if(file.isDirectory) {
        val indexOperations = (for(child <- file.listFiles()) yield {
          parseRecur(child, level + 1)
        }).filter(_.isDefined).map { item =>
          index into "wikidata" / "items" source item.get id item.get.id
        }
        client.execute { bulk (indexOperations)}
        None
      } else if(file.exists() && file.getName.endsWith(".json")) {
        curNbFile += 1
        Some(handleFile(file))
      } else {
        None
      }
    } else {
      None
    }
  }

  def handleFile(file: File): EsWikiDataItem = {
    getJs(file).extract[parser.model.WikiDataItem].toEs
  }

  def main(args: Array[String]) {
    client.execute(deleteIndex("wikidata")).await
    parseRecur(new File(s"${Conf.getString("data.parts")}/Q/"))
  }
}
