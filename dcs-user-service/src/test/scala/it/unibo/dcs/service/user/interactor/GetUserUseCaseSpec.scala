package it.unibo.dcs.service.user.interactor

import java.util.Date

import it.unibo.dcs.commons.test.JUnitSpec
import it.unibo.dcs.service.user.Mocks._
import it.unibo.dcs.service.user.interactor.usecases.GetUserUseCase
import it.unibo.dcs.service.user.model.User
import it.unibo.dcs.service.user.request.GetUserRequest
import org.scalamock.scalatest.MockFactory
import rx.lang.scala.{Observable, Subscriber}

import scala.language.postfixOps

class GetUserUseCaseSpec extends JUnitSpec with MockFactory {

  private val request = GetUserRequest("martyha")
  private val expectedUser = User(request.username, "Martina", "Magnani", "", visible = true, new Date())

  private val subscriber = stub[Subscriber[User]]

  private val getUserUseCase = GetUserUseCase(threadExecutor, postExecutionThread, userRepository)

  it should "get a user by username (request) when the use case is executed" in {
    // Given
    // userRepository is called with `request` as parameter returns an observable that contains only `user`
    (userRepository getUserByUsername _) expects request returns (Observable just expectedUser)

    // When
    // getUserUseCase is executed with argument `request`
    getUserUseCase(request).subscribe(subscriber)

    // Then
    // Verify that `subscriber.onNext` has been called once with `user` as argument
    (subscriber onNext _) verify expectedUser once()

    // Verify that `subscriber.onCompleted` has been called once
    (() => subscriber onCompleted) verify() once()
  }

}
