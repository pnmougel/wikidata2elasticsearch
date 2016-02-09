=== Description ===

Import wikidata to an elasticsearch instance

=== Install ===

You must have sbt, wget and elasticsearch >= 2 installed 

=== Tasks ===

sbt download

Download a dump of wikidata

sbt flatten

Uncompress the bzipped dump

sbt split

Split the downloaded json file into parts



=== Configuration ===

The configuration file contains several settings and is located in src/main/resources/application.conf

=== Elasticsearch data model ===

TODO