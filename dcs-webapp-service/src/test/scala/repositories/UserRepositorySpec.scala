package repositories

import java.util.Date

import it.unibo.dcs.service.webapp.model.User
import it.unibo.dcs.service.webapp.repositories.Requests.RegisterUserRequest
import it.unibo.dcs.service.webapp.repositories.UserRepository
import it.unibo.dcs.service.webapp.repositories.datastores.UserDataStore
import it.unibo.dcs.service.webapp.repositories.impl.UserRepositoryImpl
import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, OneInstancePerTest}
import rx.lang.scala.{Observable, Subscriber}

import scala.language.postfixOps

class UserRepositorySpec extends FlatSpec with MockFactory with OneInstancePerTest {
  private val userDataStore: UserDataStore = mock[UserDataStore]
  private val repository: UserRepository = new UserRepositoryImpl(userDataStore)
  private val user = User("niklegend", "nicola", "piscaglia", "bla", visible = true, new Date())
  private val registerRequest = RegisterUserRequest(user.username, "password", user.firstName,
    user.lastName)
  private val createUserSubscriber: Subscriber[User] = stub[Subscriber[User]]
  private val getUserSubscriber: Subscriber[User] = stub[Subscriber[User]]

  it should "register a new user" in {
    // Given
    (userDataStore createUser _) expects registerRequest returns Observable.just(user) noMoreThanOnce()

    // When
    repository registerUser registerRequest subscribe createUserSubscriber

    // Then
    // Verify that `subscriber.onNext` has been called once with `token` as argument
    (createUserSubscriber onNext _) verify user once()
    // Verify that `subscriber.onCompleted` has been called once
    (() => createUserSubscriber onCompleted) verify() once()
  }

  it should "retrieve a registered user given its username" in {
    // Given
    (userDataStore getUserByUsername _) expects user.username returns Observable.just(user)

    // When
    repository.getUserByUsername(user.username).subscribe(getUserSubscriber)

    // Then
    // Verify that `subscriber.onNext` has been called once with `token` as argument
    (getUserSubscriber onNext _) verify user once()
    // Verify that `subscriber.onCompleted` has been called once
    (() => getUserSubscriber onCompleted) verify() once()
  }
}
