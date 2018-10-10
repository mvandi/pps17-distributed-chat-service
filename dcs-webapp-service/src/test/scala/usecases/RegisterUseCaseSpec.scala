package usecases

import java.util.Date

import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.service.webapp.model.User
import it.unibo.dcs.service.webapp.repositories.Requests.RegisterUserRequest
import it.unibo.dcs.service.webapp.repositories.{AuthenticationRepository, UserRepository}
import it.unibo.dcs.service.webapp.usecases.RegisterUserUseCase
import it.unibo.dcs.service.webapp.usecases.Results.RegisterResult
import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, OneInstancePerTest}
import rx.lang.scala.{Observable, Subscriber}

import scala.language.postfixOps

class RegisterUseCaseSpec extends FlatSpec with MockFactory with OneInstancePerTest {

  val user = User("niklegend", "Nicola", "Piscaglia", "bio", visible = true, new Date)
  val registerRequest = RegisterUserRequest(user.username, "password", user.firstName, user.lastName)

  val threadExecutor: ThreadExecutor = mock[ThreadExecutor]
  val postExecutionThread: PostExecutionThread = mock[PostExecutionThread]
  val userRepository: UserRepository = mock[UserRepository]
  val authRepository: AuthenticationRepository = mock[AuthenticationRepository]

  val token: String = "token"
  val registerResult: RegisterResult = RegisterResult(user, token)

  val registerSubscriber: Subscriber[RegisterResult] = stub[Subscriber[RegisterResult]]

  val registerUseCase = new RegisterUserUseCase(threadExecutor, postExecutionThread, authRepository, userRepository)


  it should "login the user when the use case is executed" in {
    // Given
    (authRepository registerUser _) expects registerRequest returns (Observable just token)
    // userRepository is called with `registerRequest` as parameter returns an observable that contains only `user`
    (userRepository registerUser _) expects registerRequest returns (Observable just user)

    // When
    // createUserUseCase is executed with argument `request`
    registerUseCase(registerRequest) subscribe registerSubscriber

    // Then
    (registerSubscriber onNext _) verify registerResult once()
    (() => registerSubscriber onCompleted) verify() once()
  }
}
