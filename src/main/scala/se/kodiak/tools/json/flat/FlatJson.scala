package se.kodiak.tools.json.flat

import org.json4s.Formats
import org.json4s.JsonAST.{JNull, _}
import org.json4s.native.JsonMethods.parse
import org.json4s.native.Serialization.write

import scala.collection.mutable

trait FlatJson {

	implicit def formats:Formats

	def flattern(json:String):Map[String, Any] = {
		flatter("", parse(json))
	}

	def toJson(data:Map[String, Any]):String = {
		val tree = treeify(data)
		write(tree)
	}

	protected def flatter(prefix:String, data:JValue):Map[String, Any] = {
		data match {
			case JObject(children) => flatterObject(prefix, children)
			case _ => Map()
		}
	}

	private def flatterObject(prefix:String, data:List[(String, JValue)]):Map[String, Any] = {
		val ret = mutable.Map[String, Any]()
		data.foreach(kv => {
			val key = if (prefix.isEmpty) {
				kv._1
			} else {
				s"$prefix.${kv._1}"
			}

			kv._2 match {
				case JBool(b) => ret.put(key, b)
				case JDecimal(d) => ret.put(key, d)
				case JInt(i) => ret.put(key, i)
				case JLong(l) => ret.put(key, l)
				case JString(s) => ret.put(key, s)
				case JDouble(d) => ret.put(key, d)
				case JSet(v) => ret ++= flatterArray(key, v.toList)
				case JArray(a) => ret ++= flatterArray(key, a)
				case JObject(c) => ret ++= flatterObject(key, c)
				case JNull => ret.put(key, null)
			}
		})
		ret.toMap
	}

	private def flatterArray(prefix:String, data:List[JValue]):Map[String, Any] = {
		val ret = mutable.Map[String, Any]()
		ret.put(prefix, data.map({
			case JObject(children) => flatterObject(prefix, children)
			case JBool(b) => b
			case JDecimal(d) => d
			case JInt(i) => i
			case JLong(l) => l
			case JString(s) => s
			case JDouble(d) => d
			case JSet(v) => v
			case JArray(a) => a
		}))

		ret.toMap
	}

	/*
		Turn Map(a.b.c -> 1) to Map(a -> Map(b -> Map(c -> 1)))
	 */
	protected def treeify(data:Map[String, Any]):Map[String, Any] = {
		val level = mutable.Map[String, Any]()

		data.foreach(kv => {
			val key = kv._1

			val keys = if (key.contains(".")) {
				key.split("\\.")
			} else {
				Array(key)
			}

			if (keys.length > 1) {
				val thisLevel = if (level.contains(keys.head)) {
					level(keys.head).asInstanceOf[mutable.Map[String, Any]]
				} else {
					mutable.Map[String, Any]()
				}
				val child = treeifyChildren(keys.tail, kv._2, thisLevel)
				level.put(keys.head, child)
			} else {
				level.put(keys.head, kv._2)
			}
		})

		level.toMap
	}

	def treeifyChildren(keys:Array[String], value:Any, level:mutable.Map[String, Any]):mutable.Map[String, Any] = {
		level.put(keys.head, if (keys.tail.nonEmpty) {
			val thisLevel = if (level.contains(keys.head)) {
				level(keys.head).asInstanceOf[mutable.Map[String, Any]]
			} else {
				mutable.Map[String, Any]()
			}
			treeifyChildren(keys.tail, value, thisLevel)
		} else {
			value
		})

		level
	}
}
