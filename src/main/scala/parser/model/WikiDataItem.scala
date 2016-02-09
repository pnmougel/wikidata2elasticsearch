package parser.model

import parser.esmodel.{EsProperty, EsLocal, EsWikiDataItem}
import parser.model.enums.DataType.DataType

import scala.collection.mutable

/**
 * Created by nico on 08/02/16.
 */

case class WikiDataItem(`type`: String, datatype: Option[DataType], id: String,
                labels: Map[String, LocalizedString],
                descriptions: Map[String, LocalizedString],
                aliases: Map[String, Array[LocalizedString]],
                claims: Map[String, Array[Claim]],
                sitelinks: Option[Map[String, SiteLink]]) {

  def localizedLabels: Array[EsLocal] = {
    val localized = mutable.HashMap[String, EsLocal]()
    for((lang, label) <- labels) {
      localized(lang) = EsLocal(lang, Some(label.value), Array(), None)
    }
    for((lang, description) <- descriptions) {
      if(!localized.contains(lang)) {
        localized(lang) = EsLocal(lang, None, Array(), Some(description.value))
      } else {
        localized(lang) = localized(lang).copy(description = Some(description.value))
      }
    }
    for((lang, aliasesLocal) <- aliases) {
      if(!localized.contains(lang)) {
        localized(lang) = EsLocal(lang, None, aliasesLocal.map(_.value), None)
      } else {
        localized(lang) = localized(lang).copy(aliases = aliasesLocal.map(_.value))
      }
    }
    localized.values.toArray
  }

  def isItem : Boolean = id.startsWith("Q")
  def isProperty : Boolean = id.startsWith("P")


  def toEsItem = {
    val siteLinksEs = sitelinks.get.values.toArray
    val claimsEs = for((propertyId, claimsForProperty) <- claims; claim <- claimsForProperty) yield { claim.toEs(propertyId) }
    EsWikiDataItem(id, localizedLabels, claimsEs.toArray, siteLinksEs)
  }

  def toEsProperty = {
    val claimsEs = for((propertyId, claimsForProperty) <- claims; claim <- claimsForProperty) yield { claim.toEs(propertyId) }
    EsProperty(id, claimsEs.toArray, datatype, localizedLabels)
  }
}
