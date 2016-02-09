package shared

import java.util.concurrent.TimeUnit

import com.typesafe.config._

/**
 * Created by nico on 09/02/16.
 */
object Conf extends Config {
  val conf = ConfigFactory.load()

  def getNumber(path: String) = conf.getNumber(path)
  def getNumberList(path: String) = conf.getNumberList(path)

  def getString(path: String) = conf.getString(path)
  def getStringList(path: String) = conf.getStringList(path)

  def getAnyRef(path: String) = conf.getAnyRef(path)
  def getAnyRefList(path: String) = conf.getAnyRefList(path)

  def getBoolean(path: String) = conf.getBoolean(path)
  def getBooleanList(path: String) = conf.getBooleanList(path)

  def getBytes(path: String) = conf.getBytes(path)
  def getBytesList(path: String) = conf.getBytesList(path)

  def getConfig(path: String) = conf.getConfig(path)
  def getConfigList(path: String) = conf.getConfigList(path)

  def getDouble(path: String) = conf.getDouble(path)
  def getDoubleList(path: String) = conf.getDoubleList(path)

  def getDuration(path: String, unit: TimeUnit) = conf.getDuration(path, unit)
  def getDurationList(path: String, unit: TimeUnit) = conf.getDurationList(path, unit)

  def getInt(path: String) = conf.getInt(path)
  def getIntList(path: String) = conf.getIntList(path)

  def getList(path: String) = conf.getList(path)

  def getLong(path: String) = conf.getLong(path)
  def getLongList(path: String) = conf.getLongList(path)

  def getObject(path: String) = conf.getObject(path)
  def getObjectList(path: String) = conf.getObjectList(path)

  def getValue(path: String) = conf.getValue(path)

  @deprecated def getMilliseconds(path: String) = conf.getMilliseconds(path)
  @deprecated def getMillisecondsList(path: String) = conf.getMillisecondsList(path)

  @deprecated def getNanoseconds(path: String) = conf.getNanoseconds(path)
  @deprecated def getNanosecondsList(path: String) = conf.getNanosecondsList(path)

  def atKey(key: String) = conf.atKey(key)
  def atPath(path: String) = conf.atPath(path)

  def entrySet = conf.entrySet()
  def isEmpty = conf.isEmpty
  def hasPath(path: String) = conf.hasPath(path)
  def checkValid(reference: Config, restrictToPaths: String*) = conf.checkValid(reference, restrictToPaths:_*)

  def origin = conf.origin()
  def root = conf.root()

  def withFallback(other: ConfigMergeable) = conf.withFallback(other)
  def withOnlyPath(path: String) = conf.withOnlyPath(path)
  def withoutPath(path: String) = conf.withoutPath(path)
  def withValue(path: String, value: ConfigValue) = conf.withValue(path, value)

  def isResolved = conf.isResolved
  def resolveWith(source: Config) = conf.resolveWith(source)
  def resolveWith(source: Config, options: ConfigResolveOptions) = conf.resolveWith(source, options)
  def resolve() = conf.resolve()
  def resolve(options: ConfigResolveOptions) = conf.resolve(options)
}
