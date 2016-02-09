package parser.outmodel

import parser.model.enums.DataType.DataType

/**
 * Created by nico on 08/02/16.
 */
case class EsProperty(id: String, dataType: DataType, local: Array[EsLocal])
