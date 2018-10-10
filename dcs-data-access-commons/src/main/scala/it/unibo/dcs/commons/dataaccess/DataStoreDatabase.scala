package it.unibo.dcs.commons.dataaccess

import io.vertx.scala.ext.sql.SQLConnection
import it.unibo.dcs.commons.VertxHelper
import it.unibo.dcs.commons.VertxHelper.Implicits.functionToHandler
import rx.lang.scala.Observable

trait DataStoreDatabase {

  protected def connection: SQLConnection

  final def insert(tableName: String, params: InsertParams): Observable[Unit] =
    VertxHelper.toObservable[Unit] {
      connection.execute(s"INSERT INTO $tableName(${params.columnNames}) VALUES (${params.values});", _)
    }

}
