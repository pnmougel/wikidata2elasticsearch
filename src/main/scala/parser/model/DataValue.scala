package parser.model

import org.json4s.CustomSerializer
import org.json4s.JsonAST._
import parser.ParseProperty
import parser.model.datavalues._

import scala.collection.immutable.HashMap.HashTrieMap
import scala.collection.immutable.Map.{Map4, Map2}
import scala.collection.mutable

/**
 * Created by nico on 08/02/16.
 */



case class DataValue(value: Any, `type`: String) {
  ParseProperty.allDataValueTypes.add(`type`)

  val dataFields : Set[String] = value match {
    case d: String => {
      Set("string")
    }
    case f: Map2[String, Any] => {
      f.keySet
    }
    case x: Map4[String, Any] => {
      x.keySet
    }
    case x: HashTrieMap[String, Any] => {
      x.keySet
    }
    case _ => {
      println("Unable to map data fields")
      System.exit(0)
      Set[String]()
    }
  }

  if(DataValuesStuff.typeToFields.contains(`type`)) {
    val expectedTypes = DataValuesStuff.typeToFields(`type`)
    for(t1 <- expectedTypes) {
      if(!dataFields.contains(t1)) {
        println(s"Field ${t1} not contained sometimes for type ${`type`}")
      }
    }
    for(t1 <- dataFields) {
      if(!expectedTypes.contains(t1)) {
        println(s"Field ${t1} not contained sometimes for type ${`type`}")
      }
    }
  } else {
    DataValuesStuff.typeToFields(`type`) = dataFields
  }

  DataValuesStuff.valuesClass.add(value.getClass.getName)
}

object DataValuesStuff {
  val valuesClass = mutable.HashSet[String]()

  val typeToFields = mutable.HashMap[String, Set[String]]()
}