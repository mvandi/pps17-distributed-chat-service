package usecases

import java.util.Date

import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.service.webapp.model.User
import it.unibo.dcs.service.webapp.repositories.Requests.LoginUserRequest
import it.unibo.dcs.service.webapp.repositories.{AuthenticationRepository, UserRepository}
import it.unibo.dcs.service.webapp.usecases.LoginUserUseCase
import it.unibo.dcs.service.webapp.usecases.Results.LoginResult
import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, OneInstancePerTest}
import rx.lang.scala.{Observable, Subscriber}

import scala.language.postfixOps

class LoginUseCaseSpec extends FlatSpec with MockFactory with OneInstancePerTest {

  val loginRequest = LoginUserRequest("niklegend", "password")
  val user = User("niklegend", "Nicola", "Piscaglia", "bio", visible = true, new Date)

  val threadExecutor: ThreadExecutor = mock[ThreadExecutor]
  val postExecutionThread: PostExecutionThread = mock[PostExecutionThread]
  val userRepository: UserRepository = mock[UserRepository]
  val authRepository: AuthenticationRepository = mock[AuthenticationRepository]

  val token: String = "token"
  val loginResult: LoginResult = LoginResult(user, token)

  val loginSubscriber: Subscriber[LoginResult] = stub[Subscriber[LoginResult]]

  val loginUseCase = new LoginUserUseCase(threadExecutor, postExecutionThread, authRepository, userRepository)


  it should "login the user when the use case is executed" in {
    // Given
    (authRepository loginUser _) expects loginRequest returns (Observable just token)
    // userRepository is called with `request` as parameter returns an observable that contains only `user`
    (userRepository getUserByUsername _) expects loginRequest.username returns (Observable just user)

    // When
    // createUserUseCase is executed with argument `request`
    loginUseCase(loginRequest) subscribe loginSubscriber

    // Then
    (loginSubscriber onNext _) verify loginResult once()
    (() => loginSubscriber onCompleted) verify() once()
  }
}
