package parser

import java.io.File
import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.jackson.ElasticJackson
import org.json4s.JsonAST.JString

import org.json4s._
import org.json4s.ext.EnumNameSerializer
import org.json4s.jackson.JsonMethods._
import org.json4s.jackson
import org.json4s.DefaultFormats
import ElasticJackson.Implicits._
import parser.model.DataValuesStuff
import parser.model.enums.{DataType, SnakType, Rank}
import parser.outmodel.EsWikiDataItem

import scala.collection.mutable
import scala.io.Source

/**
 * Created by nico on 05/02/16.
 */
object Parse {
  implicit val formats = DefaultFormats + new EnumNameSerializer(Rank) + new EnumNameSerializer(DataType) + new EnumNameSerializer(SnakType)

  val minFirstLevel = 0
//  val maxFirstLevel = 30
  val maxFirstLevel = 0


  val minSecondLevel = 0
//  val maxSecondLevel = 1000
  val maxSecondLevel = 100

  val client = elasticsearch.ElasticSearch.client

  val basePath = "/home/nico/data/wikidata/parts"
  def parseRecur(file: File, level: Int = 0): Option[EsWikiDataItem] = {
    if(file.isDirectory) {
      val items = (for(child <- file.listFiles()) yield {
        parseRecur(child, level + 1)
      }).filter(_.isDefined).map(_.get)

      val indexOperations = items.map { item =>
        index into "wikidata" / "items" source item id item.id
      }

      client.execute { bulk (indexOperations)}

      None
    } else if(file.exists() && file.getName.endsWith(".json")) {
      Some(handleFile(file))
    } else {
      None
    }
  }

  val rankValues = mutable.HashSet[String]()
  val typeValues = mutable.HashSet[String]()

  val claimsFields = Set("mainsnak", "type", "qualifiers", "qualifiers-order", "id", "rank", "references")
  def checkClaims(js: JObject) = {
//    val fields = js.values.keySet
//    for(f <- fields) {
//      if(!claimsFields.contains(f)) {
//        println("Optional field: " + f)
//      }
//    }
//    typeValues.add(js.values("type").toString)
//    rankValues.add(js.values("rank").toString)

    checkSnak((js \ "mainsnak").asInstanceOf[JObject])
  }

  val snaktypeValues = mutable.HashSet[String]()
  val datatypeValues = mutable.HashSet[String]()

  val snakFields = Set("snaktype", "property", "datavalue", "datatype")
  def checkSnak(js: JObject) = {
    val fields = js.values.keySet
    for(f <- fields) {
      if(!snakFields.contains(f)) {
        println("Optional field: " + f)
      }
    }
    snaktypeValues.add(js.values("snaktype").toString)
    datatypeValues.add(js.values("datatype").toString)
  }

  def handleFile(file: File): EsWikiDataItem = {
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

//    val j = (parse(data) \ "claims").asInstanceOf[JObject]
//    for((p, v) <- j.obj) {
//      for(x <- v.asInstanceOf[JArray].arr) {
//        checkClaims(x.asInstanceOf[JObject])
//      }
//
//    }
    val item = js.extract[parser.model.WikiDataItem]
    item.toEs
//    val esItem = item.toEs
//    println(item.claims("P508")(0).references(0))



//    val client = elasticsearch.ElasticSearch.client
//    client.execute(deleteIndex("wikidata")).await
//    Thread.sleep(400)

//    println(esItem)

//    client.execute(index into "wikidata" / "items" source esItem id esItem.id)
//    println(esItem)

//    println(item.labels("en"))
//    System.exit(0)

//    println(file.getName)
//    parse(data) match {
//      case json: JObject => {
//        checkRoot(json)
//      }
//      case _ => {
//        println("Error: the file does not contains a json object")
//      }
//    }
  }

  def checkRoot(js : JObject) = {
    val values = js.values

    expectedProperties(js, Set("type", "id", "labels", "descriptions", "aliases", "claims", "sitelinks"))
    checkLocalizedString((js \ "labels").asInstanceOf[JObject])
    checkLocalizedString((js \ "descriptions").asInstanceOf[JObject])
    if(values("type") != "item") {
      println(s"Error: unexpected type ${values("type")}")
    }
    if(!values("id").isInstanceOf[String]) {
      println("Error: id field is not a string")
    }
  }

  def checkLocalizedString(labelsJs : JObject) = {
    for((lang, value) <- labelsJs.obj) {
      val langValue = value.asInstanceOf[JObject]
      expectedProperties(langValue, Set("language", "value"))
      val langStr = (langValue \ "language").asInstanceOf[JString].values
      if(langStr != lang) {
        println(s"Error: Key language (${lang}) different from value (${langStr})")
      }
    }
  }

  def expectedProperties(js: JObject, properties: Set[String]) = {
//    val intersect = properties.intersect(js.values.keySet)
//    if(intersect.size != properties.size) {
//
//    }
    val values = js.values
    if(values.size != properties.size) {
      val addedKeys = values.keySet.filterNot(k => properties.contains(k))
      println("Warning: Found new fields")
      println(addedKeys.mkString(","))
    }
    for((key, value) <- js.values) {
      if(!properties.contains(key)) {
        println("Warning: Found new field: " + key)
      }
    }
  }

  def main(args: Array[String]) {
    client.execute(deleteIndex("wikidata")).await
//    val q1 = new File(s"${basePath}/Q/0/0/Q1.json")
//    handleFile(q1)

    for(level1 <- minFirstLevel to maxFirstLevel) {
      println(s"Doing level ${level1}")
      for(level2 <- minSecondLevel to maxSecondLevel) {
        println(s"\tDoing level ${level2}")
        parseRecur(new File(s"${basePath}/Q/${level1}/${level2}"))
      }
    }
//    println(DataValuesStuff.typeToFields.mkString("\n"))

    // println(typeValues.mkString(","))
    // statement
    // println(rankValues.mkString(","))
    // preferred,deprecated,normal

    // println(snaktypeValues.mkString(","))
    // value,novalue,somevalue
    // println(datatypeValues.mkString(","))
    // time,wikibase-property,globe-coordinate,quantity,wikibase-item,string,monolingualtext,url,commonsMedia

  }
}
