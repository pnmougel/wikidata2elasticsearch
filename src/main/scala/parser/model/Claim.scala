package parser.model

import parser.model.enums.Rank.Rank
import parser.esmodel.{EsSnak, EsClaim}
import tasks.IndexProperty

/**
 * Created by nico on 08/02/16.
 */
case class Claim(id: String,
                 rank: Rank, `type`: String,
                 qualifiersOrder: Array[String],
                 mainsnak: Snak,
                 qualifiers: Option[Map[String, Array[Snak]]],
                 references: Array[Reference]) {
  def toEs(propertyId: String) = {
    val esQualifiers = if(qualifiers.isDefined) {
      (for(qualifier <- qualifiers.get; snak <- qualifier._2) yield {
        snak.toEs()
      }).toArray
    } else { Array[EsSnak]() }

    val esReferences = references.map(_.toEs())
    EsClaim(id, propertyId, rank.toString, `type`, qualifiersOrder, mainsnak.toEs(), esQualifiers, esReferences)
  }
}
