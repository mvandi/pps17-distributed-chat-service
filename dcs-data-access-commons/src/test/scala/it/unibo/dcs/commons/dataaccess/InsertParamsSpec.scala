package it.unibo.dcs.commons.dataaccess

import io.vertx.lang.scala.json.JsonObject
import it.unibo.dcs.commons.test.JUnitSpec

final class InsertParamsSpec extends JUnitSpec {

  val paramNumber = 5

  val params = InsertParams(new JsonObject()
    .put("username", "mvandi")
    .put("first_name", "Mattia")
    .put("last_name", "Vandi")
    .put("visible", true)
    .put("count", paramNumber)
  )

  assertResult("`username`, `first_name`, `last_name`, `visible`, `count`")(params.columnNames)
  assertResult("'mvandi', 'Mattia', 'Vandi', TRUE, 5")(params.values)

}
