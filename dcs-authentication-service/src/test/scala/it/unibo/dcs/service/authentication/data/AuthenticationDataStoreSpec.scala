package it.unibo.dcs.service.authentication.data

import java.util.Date

import io.vertx.lang.scala.json.{Json, JsonArray}
import io.vertx.scala.ext.sql.SQLConnection
import it.unibo.dcs.commons.test.JUnitSpec
import org.scalamock.scalatest.MockFactory
import rx.lang.scala.Subscriber

import scala.language.postfixOps

class AuthenticationDataStoreSpec extends JUnitSpec with MockFactory {

  private val username = "ale"
  private val password = "123"
  private val token = "token"
  private val sqlConnection: SQLConnection = mock[SQLConnection]
  private val authDataStore: AuthenticationDataStore = new AuthenticationDataStoreDatabase(sqlConnection)
  private val userParam = Json.arr(username)
  private val userAndPasswordParams = Json.arr(username, password)

  it should "call sqlConnection and return a result when invalidToken is called" in {
    val invalidateTokenSubscriber: Subscriber[Unit] = stub[Subscriber[Unit]]
    specifyUpdateExpectation()

    authDataStore invalidToken(token, new Date()) subscribe invalidateTokenSubscriber

    assertSubscriber[Unit](invalidateTokenSubscriber, Unit)
  }

  it should "call sqlConnection and return a result when createUser is called" in {
    val registerSubscriber: Subscriber[Unit] = stub[Subscriber[Unit]]
    specifyUpdateExpectation(userAndPasswordParams)

    authDataStore createUser(username, password) subscribe registerSubscriber

    assertSubscriber[Unit](registerSubscriber, Unit)
  }

  it should "call sqlConnection and return a result when deleteUser is called" in {
    val deleteSubscriber: Subscriber[Unit] = stub[Subscriber[Unit]]
    specifyUpdateExpectation(userParam)

    authDataStore deleteUser(username, password) subscribe deleteSubscriber

    assertSubscriber[Unit](deleteSubscriber, Unit)
  }

  it should "call sqlConnection and return a result when isTokenInvalid is called" in {
    val subscriber: Subscriber[Boolean] = stub[Subscriber[Boolean]]
    specifyQueryExpectation()

    authDataStore isTokenValid token subscribe subscriber

    assertSubscriber(subscriber, true)
  }

  private def specifyQueryExpectation(): Unit = {
    (sqlConnection queryWithParams (_, _, _)) expects(*, *, *) returns sqlConnection
  }

  private def specifyUpdateExpectation(params: JsonArray): Unit = {
    (sqlConnection updateWithParams(_, _, _)) expects(*, params, *) returns sqlConnection
  }

  private def specifyUpdateExpectation(): Unit = {
    (sqlConnection updateWithParams(_, _, _)) expects(*, *, *) returns sqlConnection
  }

  private def assertSubscriber[T](subscriber: Subscriber[T], expectedResult: T): Unit = {
    (() => subscriber onStart) verify() once()
  }

}