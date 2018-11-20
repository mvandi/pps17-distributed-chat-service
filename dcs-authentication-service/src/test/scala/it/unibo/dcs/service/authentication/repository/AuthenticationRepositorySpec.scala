package it.unibo.dcs.service.authentication.repository

import java.util.Date

import it.unibo.dcs.commons.test.JUnitSpec
import it.unibo.dcs.service.authentication.data.AuthenticationDataStore
import org.scalamock.scalatest.MockFactory
import rx.lang.scala.{Observable, Subscriber}

import scala.language.postfixOps

class AuthenticationRepositorySpec extends JUnitSpec with MockFactory {

  val username = "ale"
  val password = "123"
  val token = "token"
  val tokenExpirationDate = new Date()
  val expectedResult: Unit = Unit
  val expectedTokenValidityResult = true
  val authDataStore: AuthenticationDataStore = mock[AuthenticationDataStore]
  val authRepository = new AuthenticationRepositoryImpl(authDataStore)
  val logoutSubscriber: Subscriber[Unit] = stub[Subscriber[Unit]]

  it should "logout the user when invalidToken is called" in {
    (authDataStore invalidToken(_, _)) expects(token, tokenExpirationDate) returns (Observable just expectedResult)

    authRepository invalidToken(token, tokenExpirationDate) subscribe logoutSubscriber

    (logoutSubscriber onNext _) verify expectedResult once()
    (() => logoutSubscriber onCompleted) verify() once()
  }

  it should "register the user when createUser is called" in {
    val registerSubscriber: Subscriber[Unit] = stub[Subscriber[Unit]]
    (authDataStore createUser(_, _)) expects(username, password) returns (Observable just expectedResult)

    authRepository createUser(username, password) subscribe registerSubscriber

    (registerSubscriber onNext _) verify expectedResult once()
    (() => registerSubscriber onCompleted) verify() once()
  }

  it should "delete the user when deleteUser is called" in {
    val deleteUserSubscriber: Subscriber[Unit] = stub[Subscriber[Unit]]
    (authDataStore deleteUser(_, _)) expects(username, token) returns (Observable just expectedResult)

    authRepository deleteUser(username, token) subscribe deleteUserSubscriber

    (deleteUserSubscriber onNext _) verify expectedResult once()
    (() => deleteUserSubscriber onCompleted) verify() once()
  }

  it should "check the token validity when isTokenValid is called" in {
    val subscriber: Subscriber[Boolean] = stub[Subscriber[Boolean]]
    (authDataStore isTokenValid _) expects token returns Observable.just(expectedTokenValidityResult)

    authRepository isTokenValid token subscribe subscriber

    (subscriber onNext _) verify expectedTokenValidityResult once()
    (() => subscriber onCompleted) verify() once()
  }

}