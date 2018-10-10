package it.unibo.dcs.authentication_service.data

import java.time.LocalDateTime
import java.util.Date

import io.vertx.scala.ext.sql.SQLConnection
import it.unibo.dcs.authentication_service.data.{AuthenticationDataStore, AuthenticationDataStoreDatabase}
import org.scalamock.scalatest.MockFactory
import org.scalatest.FlatSpec
import rx.lang.scala.Subscriber

class AuthenticationDataStoreTest extends FlatSpec with MockFactory {

  val username = "ale"
  val password = "123"
  val token = "token"
  val sqlConnection: SQLConnection = mock[SQLConnection]
  val authDataStore: AuthenticationDataStore = new AuthenticationDataStoreDatabase(sqlConnection)

  it should "call sqlConnection and return a result when checkUserExistence is called" in {
    val loginSubscriber: Subscriber[Unit] = stub[Subscriber[Unit]]
    specifyQueryExpectation()
    authDataStore checkUserExistence(username, password) subscribe loginSubscriber

    assertSubscriber(loginSubscriber)
  }

  it should "call sqlConnection and return a result when invalidToken is called" in {
    val invalidateTokenSubscriber: Subscriber[Unit] = stub[Subscriber[Unit]]
    specifyExecuteExpectation()
    authDataStore invalidToken (token, LocalDateTime.now()) subscribe invalidateTokenSubscriber

    assertSubscriber(invalidateTokenSubscriber)
  }

  it should "call sqlConnection and return a result when createUser is called" in {
    val registerSubscriber: Subscriber[Unit] = stub[Subscriber[Unit]]
    specifyExecuteExpectation()
    authDataStore createUser(username, password) subscribe registerSubscriber

    assertSubscriber(registerSubscriber)
  }

  it should "call sqlConnection and return a result when isTokenInvalid is called" in {
    val subscriber: Subscriber[Boolean] = stub[Subscriber[Boolean]]
    specifyQueryExpectation()
    authDataStore isTokenInvalid token subscribe subscriber

    assertSubscriber(subscriber)
  }

  private def specifyQueryExpectation(): Unit = {
    (sqlConnection query (_, _)) expects (*, *) returns sqlConnection
  }

  private def specifyExecuteExpectation(): Unit = {
    (sqlConnection execute (_, _)) expects (*, *) returns sqlConnection
  }

  private def assertSubscriber(subscriber: Subscriber[_]): Unit = {
    (() => subscriber onStart) verify() once()
  }
}