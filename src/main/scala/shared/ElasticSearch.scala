package shared

import com.sksamuel.elastic4s.{ElasticClient, ElasticsearchClientUri}

/**
 * Created by nico on 14/10/15.
 */
object ElasticSearch {
  val client = ElasticClient.transport(ElasticsearchClientUri(
    Conf.getString("elasticsearch.interface"),
    Conf.getInt("elasticsearch.port")))
}
