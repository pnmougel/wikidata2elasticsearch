package parser.esmodel

import parser.model.SiteLink

/**
 * Created by nico on 08/02/16.
 */
case class EsWikiDataItem(id: String,
                          labels: Array[EsLocal],
                          claims: Array[EsClaim],
                          siteLinks: Array[SiteLink]) {
}