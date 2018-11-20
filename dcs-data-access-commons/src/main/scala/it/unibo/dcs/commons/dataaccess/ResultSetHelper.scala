package it.unibo.dcs.commons.dataaccess

import io.vertx.lang.scala.json.JsonObject
import io.vertx.scala.ext.sql.ResultSet

object ResultSetHelper {

  def foldResult[T](whenEmpty: => T)(f: ResultSet => T): ResultSet => T = resultSet =>
    if (resultSet.getResults.isEmpty) {
      whenEmpty
    } else {
      f(resultSet)
    }

  object Implicits {

    implicit class RichResultSet(resultSet: ResultSet) {

      def getRows: Stream[JsonObject] =
        resultSet.getResults.toStream
          .map(result => Stream.range(0, result.size)
            .map(i => (resultSet.getColumnNames(i), result.getValue(i)))
            .foldLeft(new JsonObject()) {
              (result, value) => result.put(value._1, value._2)
            })

    }

  }

}
