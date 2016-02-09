package parser.model

import parser.esmodel.EsLocal

import scala.collection.mutable

/**
 * Created by nico on 09/02/16.
 */
class WithLocalLabel(labels: Map[String, LocalizedString],
                     descriptions: Map[String, LocalizedString],
                     aliases: Map[String, Array[LocalizedString]]) {
  def toLocalLabelsArray: Array[EsLocal] = {
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
}
