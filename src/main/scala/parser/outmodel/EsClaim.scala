package parser.outmodel

import parser.model.{PropertyRef, Reference}

/**
 * Created by nico on 08/02/16.
 */
case class EsClaim(id: String,
                   propertyRef: PropertyRef,
                   rank: String, `type`: String,
                   qualifiersOrder: Array[String],
                   mainSnak: EsSnak,
                   qualifiers: Array[EsSnak],
                   references: Array[EsReference]) {
}
