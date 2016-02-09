package parser.esmodel

/**
 * Created by nico on 08/02/16.
 */
case class EsClaim(id: String,
                   propertyId: String,
                   rank: String, `type`: String,
                   qualifiersOrder: Array[String],
                   mainSnak: EsSnak,
                   qualifiers: Array[EsSnak],
                   references: Array[EsReference]) {
}
