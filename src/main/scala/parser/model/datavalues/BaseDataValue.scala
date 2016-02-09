package parser.model.datavalues

import parser.outmodel.EsDataValue

import scala.collection.immutable.HashMap.HashTrieMap
import scala.collection.immutable.Map.{Map4, Map2}

/**
 * Created by nico on 08/02/16.
 */
//case class BaseDataValue(globeCoordinate: Option[GlobeCoordinate] = None,
//                         monolingualText: Option[MonolingualText] = None,
//                         quantity: Option[Quantity] = None,
//                         string: Option[String] = None,
//                         wikiItemId: Option[BigInt] = None,
//                         wikiPropertyId: Option[BigInt] = None,
//                         time: Option[TimeValue] = None) {
//  def toEs = EsDataValue(globeCoordinate, monolingualText, quantity, string, wikiItemId, wikiPropertyId, time)
//}

case class BaseDataValue(value: Any, `type`: String) {
  lazy val jsMap = value match {
    case d: String => {
      Map("string" -> d)
    }
    case f: Map2[String, Any] => {
      f
    }
    case x: Map4[String, Any] => {
      x
    }
    case x: HashTrieMap[String, Any] => {
      x
    }
    case _ => {
      println("Unable to map data fields")
      System.exit(0)
      Map[String, Any]()
    }
  }

  def globeCoordinate = {
    if(`type` == "globecoordinate") {
      val altitude = jsMap.get("altitude") match {
        case Some(x: BigInt) => Some(x.toDouble)
        case Some(x: Double) => Some(x)
        case Some(_) => None
      }
      val precision = jsMap.get("precision") match {
        case Some(x: BigInt) => Some(x.toDouble)
        case Some(x: Double) => Some(x)
        case Some(_) => None
      }
      val latitude = jsMap.get("latitude") match {
        case Some(x: BigInt) => Some(x.toDouble)
        case Some(x: Double) => Some(x)
        case Some(_) => None
      }
      val longitude = jsMap.get("longitude") match {
        case Some(x: BigInt) => Some(x.toDouble)
        case Some(x: Double) => Some(x)
        case Some(_) => None
      }
      val globe = jsMap("globe").asInstanceOf[String]
      Some(GlobeCoordinate(altitude, precision, latitude, longitude, globe))
    } else {
      None
    }
  }

  def monolingualText = {
    if(`type` == "monolingualtext") {
      val language = jsMap("language").asInstanceOf[String]
      val text = jsMap("text").asInstanceOf[String]
      Some(MonolingualText(text, language))
    } else {
      None
    }
  }

  def quantity = {
    if(`type` == "quantity") {
      val amount = jsMap("amount").asInstanceOf[String]
      val unit = jsMap("unit").asInstanceOf[String]
      val upperBound = jsMap("upperBound").asInstanceOf[String]
      val lowerBound = jsMap("lowerBound").asInstanceOf[String]
      Some(Quantity(amount, unit, upperBound, lowerBound))
    } else {
      None
    }
  }

  def string = {
    if(`type` == "string") {
      val string = jsMap("string").asInstanceOf[String]
      Some(string)
    } else {
      None
    }
  }


  def wikiItemId = {
    if(`type` == "wikibase-entityid" && jsMap("entity-type").toString == "item") {
      Some(jsMap("numeric-id").asInstanceOf[BigInt])
    } else {
      None
    }
  }


  def wikiPropertyId = {
    if(`type` == "wikibase-entityid" && jsMap("entity-type").toString == "property") {
      Some(jsMap("numeric-id").asInstanceOf[BigInt])
    } else {
      None
    }
  }


  def time = {
    if(`type` == "time") {
//      case class TimeValue(precision: BigInt, timezone: BigInt, before: BigInt, calendarmodel: String, after: BigInt, time: String)
      val precision = jsMap("precision").asInstanceOf[BigInt]
      val timezone = jsMap("timezone").asInstanceOf[BigInt]
      val before = jsMap("before").asInstanceOf[BigInt]
      val calendarmodel = jsMap("calendarmodel").asInstanceOf[String]
      val after = jsMap("after").asInstanceOf[BigInt]
      val time = jsMap("time").asInstanceOf[String]
      Some(TimeValue(precision, timezone, before, calendarmodel, after, time))
    } else {
      None
    }
  }

  def toEs = {
    EsDataValue(globeCoordinate, monolingualText, quantity, string, wikiItemId, wikiPropertyId, time)
  }
}