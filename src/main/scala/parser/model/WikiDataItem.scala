package parser.model

import parser.outmodel.{EsLocal, EsWikiDataItem}

import scala.collection.mutable

/**
 * Created by nico on 08/02/16.
 */
case class WikiDataItem(`type`: String, id: String,
                labels: Map[String, LocalizedString],
                descriptions: Map[String, LocalizedString],
                aliases: Map[String, Array[LocalizedString]],
                claims: Map[String, Array[Claim]],
                sitelinks: Map[String, SiteLink]) {
  def toEs = {
    val localized = mutable.HashMap[String, EsLocal]()

    for((lang, label) <- labels) {
      localized(lang) = EsLocal(lang, Some(label.value), Array(), None)
    }
    for((lang, description) <- descriptions) {
      if(!localized.contains(lang)) {
        localized(lang) = EsLocal(lang, None, Array(), Some(description.value))
//        println("Warning: Existing lang for description but missing label")
      } else {
        localized(lang) = localized(lang).copy(description = Some(description.value))
      }
    }

    for((lang, aliasesLocal) <- aliases) {
      if(!localized.contains(lang)) {
        localized(lang) = EsLocal(lang, None, aliasesLocal.map(_.value), None)
//        println("Warning: Existing lang for aliases but missing label")
      } else {
        localized(lang) = localized(lang).copy(aliases = aliasesLocal.map(_.value))
      }
    }

    val siteLinksEs = sitelinks.values.toArray

    val claimsEs = for((propertyId, claimsForProperty) <- claims; claim <- claimsForProperty) yield { claim.toEs(propertyId) }

    EsWikiDataItem(id, localized.values.toArray, claimsEs.toArray, siteLinksEs)
  }
}
