package shared

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s._
import com.sksamuel.elastic4s.mappings._
import com.sksamuel.elastic4s.mappings.FieldType._
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by nico on 09/02/16.
 */
object InitModel {
  val client = ElasticSearch.client

  val idMapping = "id" typed StringType index "not_analyzed"

  val dataTypeMapping = "dataType" typed StringType index "not_analyzed"

  val labelsMapping = "labels".nested(
    "lang" typed StringType index "not_analyzed",
    "name" typed StringType fields("raw" typed StringType index "not_analyzed"),
    "aliases" typed StringType fields("raw" typed StringType index "not_analyzed")
  )

  val timeMapping = "time".nested(
    "precision" typed IntegerType,
    "timezone" typed IntegerType,
    "before" typed IntegerType,
    "after" typed IntegerType,
    "calendarmodel" typed StringType index "not_analyzed",
    "time" typed StringType index "not_analyzed"
  )

  val monolingualTextMapping = "monolingualText".nested(
    "text" typed StringType index "not_analyzed",
    "language" typed StringType index "not_analyzed"
  )

  val globeCoordinateMapping = "globeCoordinate".nested(
    "altitude" typed DoubleType,
    "precision" typed DoubleType,
    "latitude" typed DoubleType,
    "longitude" typed DoubleType,
    "globe" typed StringType index "not_analyzed"
  )

  val quantityMapping = "quantity".nested(
    "amount" typed StringType index "not_analyzed",
    "unit" typed StringType index "not_analyzed",
    "upperBound" typed StringType index "not_analyzed",
    "lowerBound" typed StringType index "not_analyzed"
  )

  val dataValueMapping = "dataValue".nested(
    "wikiItemId" typed IntegerType,
    "wikiPropertyId" typed IntegerType,
    "string" typed StringType index "not_analyzed",
    timeMapping,
    monolingualTextMapping,
    globeCoordinateMapping,
    quantityMapping
  )

  val snakMapping = List(
    "snakType" typed StringType index "not_analyzed",
    "propertyId" typed StringType index "not_analyzed",
    "dataType" typed StringType index "not_analyzed",
    "hash" typed StringType index "not_analyzed",
    dataValueMapping
  )

  val referencesMapping = "references".nested(
    "hash" typed StringType index "not_analyzed",
    "snaks".nested(snakMapping :_*)
  )

  val claimsMapping = "claims".nested(
    "id" typed StringType index "not_analyzed",
    "propertyId" typed StringType index "not_analyzed",
    "rank" typed StringType index "not_analyzed",
    "type" typed StringType index "not_analyzed",
    "qualifiersOrder" typed StringType index "not_analyzed",
    "mainSnak".nested(snakMapping :_*),
    "qualifiers".nested(snakMapping :_*),
    referencesMapping
  )

  val siteLinkMapping = "siteLinks".nested(
    "site" typed StringType index "not_analyzed",
    "title" typed StringType index "not_analyzed",
    "badges" typed StringType index "not_analyzed"
  )

  val indexName = Conf.getString("elasticsearch.index")

  def createIndex: Unit = {
    client.execute(indexExists(Seq(indexName))).map { f =>

      if(f.isExists && Conf.getBoolean("elasticsearch.deleteIndexIfExists")) {
        client.execute(deleteIndex(indexName)).await
      }
      if(!f.isExists || Conf.getBoolean("elasticsearch.deleteIndexIfExists")) {
        client.execute {
          create.index(indexName).mappings(Seq(
            mapping("items").fields(
              idMapping,
              labelsMapping,
              siteLinkMapping,
              claimsMapping
            ),
            mapping("prop").fields(
              idMapping,
              dataTypeMapping,
              labelsMapping,
              claimsMapping
            )
          ): _*)
        }.await
      }
    }.await
  }
}
