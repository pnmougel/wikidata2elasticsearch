package parser.model

import parser.model.enums.DataType.DataType
import parser.esmodel.{EsLocal, EsProperty}

import scala.collection.mutable

/**
 * Created by nico on 08/02/16.
 */

case class Property(`type`: String, datatype: DataType, id: String,
                    claims: Map[String, Array[Claim]],
                    labels: Map[String, LocalizedString],
                    descriptions: Map[String, LocalizedString],
                    aliases: Map[String, Array[LocalizedString]]
                     ) extends WithLocalLabel(labels, descriptions, aliases) {
  def toEs = {
    val localizedLabels = toLocalLabelsArray
    EsProperty(id, datatype, localizedLabels)
  }
}
