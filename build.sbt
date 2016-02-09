name := "wikidata"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= {
  Seq(
    // Elasticsearch
    "com.sksamuel.elastic4s" %% "elastic4s-core" % "2.2.0",
    "com.sksamuel.elastic4s" % "elastic4s-jackson_2.11" % "2.2.0",


    // Json4s
    "org.json4s" %% "json4s-jackson" % "3.3.0",
    "org.json4s" % "json4s-ext_2.10" % "3.3.0"
  )
}
