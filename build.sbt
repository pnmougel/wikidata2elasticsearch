name := "wikidata"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= {
  val akkaV = "2.3.9"
  Seq(
    // Akka
    "com.typesafe.akka"   %%  "akka-actor"    % akkaV,

    // Elasticsearch
    "com.sksamuel.elastic4s" %% "elastic4s-core" % "2.2.0",
    "com.sksamuel.elastic4s" % "elastic4s-jackson_2.11" % "2.2.0",

    // Slf4j
    "org.slf4j"    % "slf4j-simple"    % "1.7.1",
    "org.slf4j"    % "slf4j-api"    % "1.7.1",
    "org.slf4j"    % "log4j-over-slf4j"  % "1.7.1",

    // Apache compress
    "org.apache.commons" % "commons-compress" % "1.10",

    // Json4s
    "org.json4s" %% "json4s-jackson" % "3.3.0",
    "org.json4s" % "json4s-ext_2.10" % "3.3.0"
  )
}

lazy val download = taskKey[Unit]("Download wikidata")
fullRunTask(download, Compile, "tasks.Download")
addCommandAlias("dl", "download")

lazy val unzip = taskKey[Unit]("Uncompress wikidata file")
fullRunTask(unzip, Compile, "tasks.Unzip")

lazy val split = taskKey[Unit]("Split json file")
fullRunTask(split, Compile, "tasks.SplitJson")

lazy val index = taskKey[Unit]("Index wikidata items into elasticsearch")
fullRunTask(index, Compile, "tasks.IndexItems")