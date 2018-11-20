package it.unibo.dcs.service.authentication.data

import java.util.Date

import io.vertx.lang.scala.json.Json
import io.vertx.scala.ext.sql.SQLConnection
import it.unibo.dcs.commons.dataaccess.DataStoreDatabase
import it.unibo.dcs.commons.dataaccess.Implicits.dateToString
import rx.lang.scala.Observable

class AuthenticationDataStoreDatabase(private[this] val connection: SQLConnection)
  extends DataStoreDatabase(connection) with AuthenticationDataStore {

  private val login = "SELECT * FROM `users` WHERE `username`=? AND `password`=?"
  private val insertUser = "INSERT INTO `users` (`username`, `password`) VALUES (?, ?);"
  private val deleteUser = "DELETE FROM `users` WHERE `username`=?"
  private val insertInvalidToken = "INSERT INTO `invalid_tokens` (`token`, `expiration_date`) VALUES (?, ?);"
  private val checkToken = "SELECT * FROM `invalid_tokens` WHERE token = ?"

  override def createUser(username: String, password: String): Observable[Unit] =
    execute(insertUser, Json.arr(username, password))

  override def deleteUser(username: String, token: String): Observable[Unit] =
    execute(deleteUser, Json.arr(username)).map(_ => invalidToken(token, new Date()))

  override def checkUserCredentials(username: String, password: String): Observable[Unit] =
    query(login, Json.arr(username, password)).flatMap(resultSet => {
      if(resultSet.getResults.isEmpty){
        Observable.error(new IllegalStateException("Wrong credentials"))
      } else {
        Observable.just(Unit)
      }
    })

  override def invalidToken(token: String, expirationDate: Date): Observable[Unit] =
    execute(insertInvalidToken, Json.arr(token, dateToString(expirationDate)))

  override def isTokenValid(token: String): Observable[Boolean] =
    query(checkToken, Json.arr(token)).map(_.getResults.isEmpty)

}

private[data] object AuthenticationDataStoreDatabase {
  def apply(connection: SQLConnection): AuthenticationDataStoreDatabase =
    new AuthenticationDataStoreDatabase(connection)
}
