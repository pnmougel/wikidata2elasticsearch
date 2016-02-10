## Description

Import wikidata to an elasticsearch instance

## Install

You must have sbt, wget and elasticsearch >= 2 installed 

```
sudo apt-get install wget sbt
```

## Tasks

### Download a dump of wikidata

```
sbt download
```
### Uncompress the bzipped dump

```
sbt flatten
```

### Split the downloaded json file into parts

```
sbt split
```

This task is not required to perform the import into elasticsearch

### Index wikidata documents into elasticsearch

```
sbt index
```



## Configuration

The configuration file contains several settings and is located in `src/main/resources/application.conf`

## Elasticsearch data model

TODO