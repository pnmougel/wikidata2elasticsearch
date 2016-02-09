package parser.model

import parser.model.enums.DataType.DataType
import parser.outmodel.{EsLocal, EsProperty}

import scala.collection.mutable

/**
 * Created by nico on 08/02/16.
 */


//time
//wikibase-property
//globe-coordinate
//quantity
//wikibase-item
//string
//monolingualtext
//commonsMedia
//url

case class Property(`type`: String, datatype: DataType, id: String,
                   claims: Map[String, Array[Claim]],
                    labels: Map[String, LocalizedString],
                     descriptions: Map[String, LocalizedString],
                     aliases: Map[String, Array[LocalizedString]]
                     ) {
  def toEs = {
    val localized = mutable.HashMap[String, EsLocal]()

    for((lang, label) <- labels) {
      localized(lang) = EsLocal(lang, Some(label.value), Array(), None)
    }
    for((lang, description) <- descriptions) {
      if(!localized.contains(lang)) {
        println("Warning: Existing lang for description but missing label")
      } else {
        localized(lang) = localized(lang).copy(description = Some(description.value))
      }
    }

    for((lang, aliasesLocal) <- aliases) {
      if(!localized.contains(lang)) {
        println("Warning: Existing lang for aliases but missing label")
      } else {
        localized(lang) = localized(lang).copy(aliases = aliasesLocal.map(_.value))
      }
    }

    EsProperty(id, datatype, localized.values.toArray)
  }
}
