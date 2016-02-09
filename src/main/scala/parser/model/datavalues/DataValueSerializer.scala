package parser.model.datavalues

import org.json4s.CustomSerializer
import org.json4s.JsonAST._

/**
 * Created by nico on 08/02/16.
 */
/*
class DataValueSerializer extends CustomSerializer[BaseDataValue](format => (
  {
    case JObject(JField("value", JObject(j)) :: JField("type", JString("wikibase-entityid")) :: Nil) => {
      val valuesSet = j.toMap
      val entityType = valuesSet("entity-type").asInstanceOf[JString]
      if(entityType.s == "item") {
        BaseDataValue(wikiItemId = Some(valuesSet("numeric-id").asInstanceOf[JInt].num))
      } else {
        BaseDataValue(wikiPropertyId = Some(valuesSet("numeric-id").asInstanceOf[JInt].num))
//        WikibasePropertyId(valuesSet("numeric-id").asInstanceOf[JInt].num)
      }
    }
    case JObject(JField("value", JObject(j)) :: JField("type", JString("globecoordinate")) :: Nil) => {
      val valuesSet = j.toMap
      val altitude = valuesSet("altitude") match {
        case JNull => None
        case v: JDouble => Some(v.num)
      }
      val precision = valuesSet("precision") match {
        case JNull => None
        case v: JDouble => Some(v.num)
      }
      val latitude = valuesSet("latitude") match {
        case JNull => None
        case v: JDouble => Some(v.num)
      }
      val longitude = valuesSet("longitude") match {
        case JNull => None
        case v: JDouble => Some(v.num)
      }
      val globe = valuesSet("globe").asInstanceOf[JString].s
      BaseDataValue(globeCoordinate = Some(GlobeCoordinate(altitude, precision, latitude, longitude, globe)))
    }
    case JObject(JField("value", JObject(j)) :: JField("type", JString("monolingualtext")) :: Nil) => {
      val valuesSet = j.toMap
      val language = valuesSet("language").asInstanceOf[JString].s
      val text = valuesSet("text").asInstanceOf[JString].s
      BaseDataValue(monolingualText = Some(MonolingualText(text, language)))
    }
    case JObject(JField("value", JString(j)) :: JField("type", JString("string")) :: Nil) => {
      BaseDataValue(string = Some(j))
    }
    case JObject(JField("value", JObject(j)) :: JField("type", JString("quantity")) :: Nil) => {
      val valuesSet = j.toMap
      val amount = valuesSet("amount").asInstanceOf[JString].s
      val unit = valuesSet("unit").asInstanceOf[JString].s
      val upperBound = valuesSet("upperBound").asInstanceOf[JString].s
      val lowerBound = valuesSet("lowerBound").asInstanceOf[JString].s
      BaseDataValue(quantity = Some(Quantity(amount, unit, upperBound, lowerBound)))
    }
    case JObject(JField("value", JObject(j)) :: JField("type", JString("time")) :: Nil) => {
      val valuesSet = j.toMap
      val precision = valuesSet("precision").asInstanceOf[JInt].num
      val timezone = valuesSet("timezone").asInstanceOf[JInt].num
      val before = valuesSet("before").asInstanceOf[JInt].num
      val calendarmodel = valuesSet("calendarmodel").asInstanceOf[JString].s
      val after = valuesSet("after").asInstanceOf[JInt].num
      val time = valuesSet("time").asInstanceOf[JString].s

      BaseDataValue(time = Some(TimeValue(precision, timezone, before, calendarmodel, after, time)))
    }
    case JObject(x) => {
      println("Found unmapped object")
      println(x)
      BaseDataValue()
    }
    case JNothing => {
      println("Found nothing object")
      BaseDataValue()
    }
    case _ => {
      println(format.getClass)
      println("Unable to map datavalue")
      System.exit(0)
      BaseDataValue()
    }
  },
  {
    case x: GlobeCoordinate => {
      val fields = List(JField("type", JString("GlobeCoordinate"))) ++
        x.altitude.map( v =>  JField("altitude", JDouble(v))) ++
        x.longitude.map( v =>  JField("longitude", JDouble(v))) ++
        x.precision.map( v =>  JField("precision", JDouble(v))) ++
        List(JField("globe", JString(x.globe))) ++
        x.latitude.map( v =>  JField("latitude", JDouble(v)))
      JObject(fields : _*)
    }
    case x: MonolingualText => {
      val fields = List(
        JField("type", JString("MonolingualText")),
        JField("text", JString(x.text)),
        JField("language", JString(x.language)))
      JObject(fields : _*)
    }
    case x: Quantity => {
      val fields = List(
        JField("type", JString("Quantity")),
        JField("amount", JString(x.amount)),
        JField("lowerBound", JString(x.lowerBound)),
        JField("upperBound", JString(x.upperBound)),
        JField("unit", JString(x.unit)))
      JObject(fields : _*)
    }
    case x: StringValue => {
      val fields = List(
        JField("type", JString("String")),
        JField("value", JString(x.value)))
      JObject(fields : _*)
    }
    case x: TimeValue => {
      val fields = List(
        JField("type", JString("String")),
        JField("value", JInt(x.before)),
        JField("value", JInt(x.after)),
        JField("value", JString(x.time)),
        JField("value", JString(x.calendarmodel)),
        JField("value", JInt(x.precision)),
        JField("value", JInt(x.timezone)))
      JObject(fields : _*)
    }
    case x: WikibaseItemId => {
      val fields = List(
        JField("type", JString("WikibaseItemId")),
        JField("value", JInt(x.id)))
      JObject(fields : _*)
    }
    case x: WikibasePropertyId => {
      val fields = List(
        JField("type", JString("WikibasePropertyId")),
        JField("value", JInt(x.id)))
      JObject(fields : _*)
    }
    case _ => {
      println("what is that I have ??")
      JObject()
    }
  }
  ))
  */