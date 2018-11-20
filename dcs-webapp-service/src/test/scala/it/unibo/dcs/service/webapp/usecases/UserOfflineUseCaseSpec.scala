package it.unibo.dcs.service.webapp.usecases

import it.unibo.dcs.service.webapp.interaction.Requests.GetUserRequest
import it.unibo.dcs.service.webapp.interaction.Results.GetUserResult
import it.unibo.dcs.service.webapp.usecases.commons.Mocks._
import it.unibo.dcs.service.webapp.usecases.commons.UseCaseSpec
import rx.lang.scala.{Observable, Subscriber}

import scala.language.postfixOps

class UserOfflineUseCaseSpec extends UseCaseSpec {

  private val request = GetUserRequest("niklegend")

  private val result = GetUserResult(user)

  private val subscriber: Subscriber[GetUserResult] = stub[Subscriber[GetUserResult]]

  private val useCase = new UserOfflineUseCase(threadExecutor, postExecutionThread, userRepository)

  it should "login the user when the use case is executed" in {
    // Given
    // userRepository is called with `request` as parameter returns an observable that contains only `user`
    (userRepository getUserByUsername _) expects request.username returns (Observable just user)

    // When
    // createUserUseCase is executed with argument `request`
    useCase(request) subscribe subscriber

    // Then
    (subscriber onNext _) verify result once()
    (() => subscriber onCompleted) verify() once()
  }

}
