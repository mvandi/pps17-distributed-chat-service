package it.unibo.dcs.service.user

import java.util.Date

import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.service.user.interactor.GetUserUseCase
import it.unibo.dcs.service.user.model.User
import it.unibo.dcs.service.user.repository.UserRepository
import it.unibo.dcs.service.user.request.GetUserRequest
import org.scalamock.scalatest.MockFactory
import org.scalatest.FlatSpec
import rx.lang.scala.{Observable, Subscriber}

class GetUserUseCaseTest extends FlatSpec with MockFactory {
  val request = GetUserRequest("martynha")
  val expectedUser = User("martynha", "Martina", "Magnani", "", true, new Date())

  val threadExecutor: ThreadExecutor = mock[ThreadExecutor]
  val postExecutionThread: PostExecutionThread = mock[PostExecutionThread]
  val userRepository: UserRepository = mock[UserRepository]

  val subscriber: Subscriber[User] = stub[Subscriber[User]]

  val getUserUseCase = new GetUserUseCase(threadExecutor, postExecutionThread, userRepository)

  it should "get a user by username (request) when the use case is executed" in {
    // Given
    // userRepository is called with `request` as parameter returns an observable that contains only `user`
    (userRepository getUserByUsername _) expects request returns (Observable just expectedUser)

    // When
    // createUserUseCase is executed with argument `request`
    getUserUseCase(request).subscribe(subscriber)

    // Then
    // Verify that `subscriber.onNext` has been called once with `user` as argument
    (subscriber onNext _) verify expectedUser once()

    // Verify that `subscriber.onCompleted` has been called once
    (subscriber onCompleted: () => Unit) verify() once()
  }
}
