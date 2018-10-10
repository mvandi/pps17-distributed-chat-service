package it.unibo.dcs.service.user.repository.impl

import io.vertx.lang.scala.json.JsonArray
import io.vertx.scala.ext.sql.SQLConnection
import it.unibo.dcs.commons.VertxHelper
import it.unibo.dcs.commons.VertxHelper.Implicits._
import it.unibo.dcs.commons.dataaccess.ResultSetHelper
import it.unibo.dcs.service.user.model.User
import it.unibo.dcs.service.user.model.exception.UserNotFoundException
import it.unibo.dcs.service.user.repository.UserDataStore
import it.unibo.dcs.service.user.request.{CreateUserRequest, GetUserRequest}
import rx.lang.scala.Observable
import it.unibo.dcs.service.user.request.Implicits._

final class UserDataStoreDatabase(private[this] val connection: SQLConnection) extends UserDataStore {

  override def getUserByUsername(request: GetUserRequest): Observable[User] = {
    VertxHelper.toObservable[User] { handler =>
      connection.queryWithParams("SELECT * FROM users WHERE username = ?", new JsonArray().add(request.username), ar => {
        if (ar.succeeded()) {
          if (ar.result().getResults.isEmpty) {
            handler(UserNotFoundException(request.username))
          } else {
            val resultSet = ar.result()
            val userJsonObject = ResultSetHelper.getRows(resultSet).head
            println(s"Request: $userJsonObject")
            val user: User = userJsonObject
            handler(user)
          }
        } else handler(ar.cause())
      })
    }
  }

  override def createUser(user: CreateUserRequest): Observable[User] = {
    def insertUser(): Observable[Unit] = {
      VertxHelper.toObservable[Unit] { handler =>
        connection.execute(s"INSERT INTO users(username, first_name, last_name) VALUES('${user.username}', " +
          s"'${user.firstName}', '${user.lastName}')", handler)
      }
    }
    insertUser().flatMap(_ => getUserByUsername(GetUserRequest(user.username)))
  }

}
