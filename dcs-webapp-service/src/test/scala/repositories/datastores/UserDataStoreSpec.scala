package repositories.datastores

import java.util.Date

import it.unibo.dcs.service.webapp.model.User
import it.unibo.dcs.service.webapp.repositories.Requests.RegisterUserRequest
import it.unibo.dcs.service.webapp.repositories.datastores.UserDataStore
import it.unibo.dcs.service.webapp.repositories.datastores.api.UserApi
import it.unibo.dcs.service.webapp.repositories.datastores.impl.UserDataStoreNetwork
import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, OneInstancePerTest}
import rx.lang.scala.{Observable, Subscriber}

import scala.language.postfixOps

class UserDataStoreSpec extends FlatSpec with MockFactory with OneInstancePerTest {

  private val userApi: UserApi = mock[UserApi]
  private val dataStore: UserDataStore = new UserDataStoreNetwork(userApi)
  private val user = User("niklegend", "nicola", "piscaglia", "bla", visible = true, new Date())
  private val registerRequest = RegisterUserRequest(user.username, "password", user.firstName,
    user.lastName)
  private val createUserSubscriber: Subscriber[User] = stub[Subscriber[User]]
  private val getUserSubscriber: Subscriber[User] = stub[Subscriber[User]]

  it should "create a new user" in {
    // Given
    (userApi createUser _) expects registerRequest returns Observable.just(user) noMoreThanOnce()

    // When
    dataStore.createUser(registerRequest).subscribe(createUserSubscriber)

    // Then
    // Verify that `subscriber.onNext` has been called once with `token` as argument
    (createUserSubscriber onNext _) verify user once()
    // Verify that `subscriber.onCompleted` has been called once
    (() => createUserSubscriber onCompleted) verify() once()
  }

  it should "retrieve a user given its username" in {
    // Given
    (userApi getUserByUsername _) expects user.username returns Observable.just(user)

    // When
    dataStore.getUserByUsername(user.username).subscribe(getUserSubscriber)

    // Then
    // Verify that `subscriber.onNext` has been called once with `token` as argument
    (getUserSubscriber onNext _) verify user once()
    // Verify that `subscriber.onCompleted` has been called once
    (() => getUserSubscriber onCompleted) verify() once()
  }
}
