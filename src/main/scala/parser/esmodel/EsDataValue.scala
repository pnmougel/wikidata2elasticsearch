package parser.esmodel

import parser.model.datavalues.{TimeValue, Quantity, MonolingualText, GlobeCoordinate}

/**
 * Created by nico on 09/02/16.
 */
case class EsDataValue(globeCoordinate: Option[GlobeCoordinate] = None,
                       monolingualText: Option[MonolingualText] = None,
                       quantity: Option[Quantity] = None,
                       string: Option[String] = None,
                       wikiItemId: Option[BigInt] = None,
                       wikiPropertyId: Option[BigInt] = None,
                       time: Option[TimeValue] = None)
