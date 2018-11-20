package it.unibo.dcs.service.webapp.usecases

import it.unibo.dcs.service.webapp.interaction.Requests.RegisterUserRequest
import it.unibo.dcs.service.webapp.interaction.Results.RegisterResult
import it.unibo.dcs.service.webapp.usecases.commons.Mocks._
import it.unibo.dcs.service.webapp.usecases.commons.UseCaseSpec
import rx.lang.scala.{Observable, Subscriber}

import scala.language.postfixOps

class RegisterUseCaseSpec extends UseCaseSpec {

  private val registerRequest = RegisterUserRequest(user.username, user.firstName, user.lastName, "password", "password")

  private val registerResult: RegisterResult = RegisterResult(user, token)

  private val registerSubscriber: Subscriber[RegisterResult] = stub[Subscriber[RegisterResult]]

  private val registerUseCase = new RegisterUserUseCase(threadExecutor, postExecutionThread, authRepository,
    userRepository, roomRepository)


  it should "register the user when the use case is executed" in {
    // Given
    (authRepository registerUser _) expects registerRequest returns (Observable just token)
    // userRepository is called with `registerRequest` as parameter returns an observable that contains only `user`
    (userRepository registerUser _) expects registerRequest returns (Observable just user)
    (roomRepository registerUser _) expects registerRequest returns (Observable just Unit)

    // When
    // createUserUseCase is executed with argument `request`
    registerUseCase(registerRequest) subscribe registerSubscriber

    // Then
    (registerSubscriber onNext _) verify registerResult once()
    (() => registerSubscriber onCompleted) verify() once()
  }
}