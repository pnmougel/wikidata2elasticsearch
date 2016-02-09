package parser.model

import parser.esmodel.{EsReference, EsSnak}

/**
 * Created by nico on 08/02/16.
 */
case class Reference(hash: String, snaks: Map[String, Array[Snak]], `snaks-order`: Array[String]) {
  def toEs() = {
    var esSnaks = Vector[EsSnak]()
    for(snakOrder <- `snaks-order`) {
      esSnaks ++= snaks(snakOrder).toVector.map(_.toEs())
    }
    EsReference(hash, esSnaks.toArray)
  }
}
