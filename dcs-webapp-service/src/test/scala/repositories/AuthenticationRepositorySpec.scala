package repositories

import java.util.Date

import it.unibo.dcs.service.webapp.model.User
import it.unibo.dcs.service.webapp.repositories.AuthenticationRepository
import it.unibo.dcs.service.webapp.repositories.Requests.{LoginUserRequest, RegisterUserRequest}
import it.unibo.dcs.service.webapp.repositories.datastores.AuthenticationDataStore
import it.unibo.dcs.service.webapp.repositories.impl.AuthenticationRepositoryImpl
import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, OneInstancePerTest}
import rx.lang.scala.{Observable, Subscriber}

import scala.language.postfixOps

class AuthenticationRepositorySpec extends FlatSpec with MockFactory with OneInstancePerTest {
  val dataStore: AuthenticationDataStore = mock[AuthenticationDataStore]
  val repository: AuthenticationRepository = new AuthenticationRepositoryImpl(dataStore)
  val user = User("niklegend", "nicola", "piscaglia", "bla", visible = true, new Date())
  val registerRequest = RegisterUserRequest(user.username, "password", user.firstName,
    user.lastName)
  val loginUserRequest = LoginUserRequest(user.username, "password")
  val registeredSubscriber: Subscriber[String] = stub[Subscriber[String]]
  val loginSubscriber: Subscriber[String] = stub[Subscriber[String]]
  val logoutSubscriber: Subscriber[Unit] = stub[Subscriber[Unit]]
  val token = "token"


  it should "register a new user" in {
    // Given
    (dataStore registerUser _) expects registerRequest returns (Observable just token) noMoreThanOnce()

    // When
    repository.registerUser(registerRequest).subscribe(registeredSubscriber)

    // Then
    // Verify that `subscriber.onNext` has been called once with `token` as argument
    (registeredSubscriber onNext _) verify token once()
    // Verify that `subscriber.onCompleted` has been called once
    (() => registeredSubscriber onCompleted) verify() once()
  }


  it should "authenticate a registered user" in {
    // Given
    (dataStore registerUser _) expects registerRequest returns (Observable just token)
    (dataStore loginUser _) expects loginUserRequest returns (Observable just token)

    // When
    repository.registerUser(registerRequest).subscribe(_ => {
      repository.loginUser(loginUserRequest).subscribe(loginSubscriber)
    })

    // Then
    // Verify that `subscriber.onNext` has been called once with `token` as argument
    (loginSubscriber onNext _) verify token once()
    // Verify that `subscriber.onCompleted` has been called once
    (() => loginSubscriber onCompleted) verify() once()
  }


  it should "logout a logged user" in {
    // Given
    (dataStore loginUser _) expects loginUserRequest returns (Observable just token)
    (dataStore logoutUser _) expects user.username returns Observable.empty

    // When
    repository.loginUser(loginUserRequest).subscribe(_ => {
      repository.logoutUser(user.username).subscribe(logoutSubscriber)
    })

    // Then
    // Verify that `subscriber.onCompleted` has been called once
    (() => logoutSubscriber onCompleted) verify() once()
  }
}
