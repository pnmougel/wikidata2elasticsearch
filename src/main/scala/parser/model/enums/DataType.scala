package parser.model.enums

/**
 * Created by nico on 08/02/16.
 */
object DataType extends Enumeration {
  type DataType = Value
  val Time, WikibaseProperty, GlobeCoordinate, Quantity, WikibaseItem, Text, MonolingualText, CommonsMedia, Url = Value
}
