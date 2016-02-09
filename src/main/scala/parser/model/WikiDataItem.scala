package parser.model

import parser.esmodel.EsWikiDataItem

/**
 * Created by nico on 08/02/16.
 */
case class WikiDataItem(`type`: String, id: String,
                labels: Map[String, LocalizedString],
                descriptions: Map[String, LocalizedString],
                aliases: Map[String, Array[LocalizedString]],
                claims: Map[String, Array[Claim]],
                sitelinks: Map[String, SiteLink]) extends WithLocalLabel(labels, descriptions, aliases) {
  def toEs = {
    val localizedLabels = toLocalLabelsArray
    val siteLinksEs = sitelinks.values.toArray
    val claimsEs = for((propertyId, claimsForProperty) <- claims; claim <- claimsForProperty) yield { claim.toEs(propertyId) }
    EsWikiDataItem(id, localizedLabels, claimsEs.toArray, siteLinksEs)
  }
}
