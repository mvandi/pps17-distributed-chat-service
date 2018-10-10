package it.unibo.dcs.authentication_service.server

import io.vertx.lang.scala.VertxExecutionContext
import io.vertx.scala.core.Vertx
import io.vertx.scala.ext.jdbc.JDBCClient
import io.vertx.scala.ext.sql.SQLConnection
import it.unibo.dcs.authentication_service.data.AuthenticationDataStoreDatabase
import it.unibo.dcs.authentication_service.repository.AuthenticationRepositoryImpl
import it.unibo.dcs.commons.VertxHelper

import scala.util.{Failure, Success}

object Launcher extends App {
  val vertx = Vertx vertx()
  implicit val executionContext: VertxExecutionContext = VertxExecutionContext(vertx.getOrCreateContext())
  val jdbcClient = JDBCClient.createNonShared(vertx, VertxHelper.readJsonObject("/db_config.json"))
  jdbcClient.getConnectionFuture().onComplete {
    case Success(connection: SQLConnection) => deployVerticle(connection)
    case Failure(cause) => println(s"$cause")
  }

  private def deployVerticle(sqlConnection: SQLConnection): Unit = {
    val authDataStore = new AuthenticationDataStoreDatabase(sqlConnection)
    val authRepository = new AuthenticationRepositoryImpl(authDataStore)
    vertx deployVerticle new AuthenticationVerticle(authRepository)
  }
}