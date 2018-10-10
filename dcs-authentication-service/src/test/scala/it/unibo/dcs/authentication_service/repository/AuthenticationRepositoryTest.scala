package it.unibo.dcs.authentication_service.repository

import java.time.LocalDateTime

import it.unibo.dcs.authentication_service.data.AuthenticationDataStore
import org.scalamock.scalatest.MockFactory
import org.scalatest.FlatSpec
import rx.lang.scala.{Observable, Subscriber}

class AuthenticationRepositoryTest extends FlatSpec with MockFactory {

  val username = "ale"
  val password = "123"
  val token = "token"
  val tokenExpirationDate: LocalDateTime = LocalDateTime.now
  val expectedResult: Unit = Unit
  val expectedTokenInvalidityResult = true
  val authDataStore: AuthenticationDataStore = mock[AuthenticationDataStore]
  val authRepository = new AuthenticationRepositoryImpl(authDataStore)
  val logoutSubscriber: Subscriber[Unit] = stub[Subscriber[Unit]]

  it should "login the user when the loginUser is called" in {
    val loginSubscriber: Subscriber[Unit] = stub[Subscriber[Unit]]
    (authDataStore checkUserExistence(_, _)) expects (username, password) returns (Observable just expectedResult)

    authRepository loginUser(username, password) subscribe loginSubscriber

    (loginSubscriber onNext _) verify expectedResult once()
    (() => loginSubscriber onCompleted) verify() once()
  }

  it should "logout the user when invalidToken is called" in {
    (authDataStore invalidToken (_ ,_)) expects (token, tokenExpirationDate) returns (Observable just expectedResult)

    authRepository invalidToken(token, tokenExpirationDate) subscribe logoutSubscriber

    (logoutSubscriber onNext _) verify expectedResult once()
    (() => logoutSubscriber onCompleted) verify() once()
  }

  it should "register the user when createUser is called" in {
    val registerSubscriber: Subscriber[Unit] = stub[Subscriber[Unit]]
    (authDataStore createUser(_, _)) expects (username, password) returns (Observable just expectedResult)

    authRepository createUser(username, password) subscribe registerSubscriber

    (registerSubscriber onNext _) verify expectedResult once()
    (() => registerSubscriber onCompleted) verify() once()
  }

  it should "check the token validity when isTokenValid is called" in {
    val subscriber: Subscriber[Boolean] = stub[Subscriber[Boolean]]
    (authDataStore isTokenInvalid _) expects token returns Observable.just(expectedTokenInvalidityResult)

    authRepository isTokenInvalid token subscribe subscriber

    (subscriber onNext _) verify expectedTokenInvalidityResult once()
    (() => subscriber onCompleted) verify() once()
  }

}