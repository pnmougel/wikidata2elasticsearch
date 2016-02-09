package parser.model

import parser.model.datavalues.BaseDataValue
import parser.model.enums.DataType.DataType
import parser.model.enums.SnakType.SnakType
import parser.esmodel.EsSnak

/**
 * Created by nico on 08/02/16.
 */
case class Snak(snaktype: SnakType, property: String, datatype: DataType, datavalue: Option[BaseDataValue], hash: Option[String]) {
  def toEs() = {
    EsSnak(snaktype.toString, property, datatype.toString, datavalue.map(_.toEs), hash)
  }
}
