package parser.model.enums

/**
 * Created by nico on 08/02/16.
 */
object DataValueType extends Enumeration {
  type DataValueType = Value
  val Time, WikibaseEntityId, GlobeCoordinate, Quantity, Text, MonolingualText = Value

}
