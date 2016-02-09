package parser.model.enums

/**
 * Created by nico on 08/02/16.
 */
object Rank extends Enumeration{
  type Rank = Value
  val Preferred, Deprecated, Normal = Value
}
