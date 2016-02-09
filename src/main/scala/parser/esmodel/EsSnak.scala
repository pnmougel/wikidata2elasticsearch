package parser.esmodel

/**
 * Created by nico on 08/02/16.
 */
case class EsSnak(snakType: String,
                  propertyId: String,
                  dataType: String,
                  dataValue: Option[EsDataValue],
                  hash: Option[String])
