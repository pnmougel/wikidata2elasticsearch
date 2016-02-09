package parser.outmodel

import parser.model.DataValue
import parser.model.datavalues._
import parser.model.enums.DataType._
import parser.model.enums.SnakType._

/**
 * Created by nico on 08/02/16.
 */
case class EsSnak(snakType: String,
                  property: String,
                  dataType: String,
                  dataValue: Option[EsDataValue],
                  hash: Option[String])
