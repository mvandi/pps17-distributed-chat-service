package it.unibo.dcs.service.webapp.usecases

import it.unibo.dcs.service.webapp.interaction.Requests.LoginUserRequest
import it.unibo.dcs.service.webapp.interaction.Results.LoginResult
import it.unibo.dcs.service.webapp.usecases.commons.Mocks._
import it.unibo.dcs.service.webapp.usecases.commons.UseCaseSpec
import rx.lang.scala.{Observable, Subscriber}

import scala.language.postfixOps

class LoginUseCaseSpec extends UseCaseSpec {

  private val loginRequest = LoginUserRequest("niklegend", "password")

  private val loginResult: LoginResult = LoginResult(user, token)

  private val loginSubscriber: Subscriber[LoginResult] = stub[Subscriber[LoginResult]]

  private val loginUseCase = new LoginUserUseCase(threadExecutor, postExecutionThread, authRepository, userRepository)


  it should "login the user when the use case is executed" in {
    // Given
    (authRepository loginUser _) expects loginRequest returns (Observable just token)
    // userRepository is called with `request` as parameter returns an observable that contains only `user`
    (userRepository getUserByUsername _) expects loginRequest.username returns (Observable just user)
    (userRepository updateAccess _) expects loginRequest.username returns (Observable just Unit)

    // When
    // createUserUseCase is executed with argument `request`
    loginUseCase(loginRequest) subscribe loginSubscriber

    // Then
    (loginSubscriber onNext _) verify loginResult once()
    (() => loginSubscriber onCompleted) verify() once()
  }
}
