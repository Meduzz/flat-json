package se.kodiak.tools.json.flat

import org.json4s.DefaultFormats
import org.scalatest.FunSuite

class FlatJsonTest extends FunSuite with FlatJson {
	implicit val formats = DefaultFormats.withBigDecimal

	test("the flatter, flatters... things") {
		val json =
			"""{
				| "a":{
				|   "b":{
				|     "c":2
				|   },
				|   "d":"a"
				| }
				|}""".stripMargin

		val result = flattern(json)
		assert(result.size == 2, "Map should only have 2 key/value.")
		assert(result("a.b.c") == 2, "Map should contain 2 on key a.b.c.")
		assert(result("a.d") == "a", "Map should contain a on  key a.d.")
	}

	test("the treeifyer, treeifies things") {
		val data = Map("a.b.c" -> 2, "a.d" -> "a")
		val expected = """{"a":{"b":{"c":2},"d":"a"}}"""

		val result = toJson(data)
		assert(result.length > 0, "Json should not be empty.")
		assert(result.equals(expected), "Json did not match the expected string")
	}

	test("how are nulls treated?") {
		val json =
			"""{
				| "a":{
				|   "b":null
				|  }
				|}
			""".stripMargin

		val result = flattern(json)
		assert(result.size == 1, "Map should only have 1 key/value.")
		assert(result("a.b") == null, "Map should contain null on key a.b.")
	}

	test("terrible decimals, are terrible") {
		val decimal = "1.034621"
		val json =
			s"""{
				| "a":$decimal
				|}
			""".stripMargin

		val result = flattern(json)
		assert(result.size == 1, "Map should contain 1 key/value.")
		assert(result("a") == 1.034621)
		// unfortunately result("a") is a Double.
	}
}
