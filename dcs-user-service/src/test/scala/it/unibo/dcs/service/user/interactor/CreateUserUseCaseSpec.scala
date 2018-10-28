package it.unibo.dcs.service.user.interactor

import java.util.Date

import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.commons.validation.Validator
import it.unibo.dcs.service.user.interactor.usecases.CreateUserUseCase
import it.unibo.dcs.service.user.interactor.validations.ValidateUserCreation
import it.unibo.dcs.service.user.model.User
import it.unibo.dcs.service.user.repository.UserRepository
import it.unibo.dcs.service.user.request.{CreateUserRequest, GetUserRequest}
import it.unibo.dcs.service.user.validator.UserCreationValidator
import org.scalamock.scalatest.MockFactory
import org.scalatest.FlatSpec
import rx.lang.scala.{Observable, Subscriber}

class CreateUserUseCaseSpec extends FlatSpec with MockFactory {

  private val username = "martynha"
  val request = CreateUserRequest(username, "Martina", "Magnani")
  val expectedUser = User(username, "Martina", "Magnani", "", true, new Date())

  val threadExecutor: ThreadExecutor = mock[ThreadExecutor]
  val postExecutionThread: PostExecutionThread = mock[PostExecutionThread]
  val userRepository: UserRepository = mock[UserRepository]

  val subscriber: Subscriber[User] = stub[Subscriber[User]]

  val createUserUseCase = {
    val validator: Validator[CreateUserRequest] = UserCreationValidator(userRepository)
    val validation = new ValidateUserCreation(threadExecutor, postExecutionThread, validator)
    new CreateUserUseCase(threadExecutor, postExecutionThread, userRepository, validation)
  }

  it should "create a new user when the use case is executed" in {
    // Given
    // userRepository is called with `request` as parameter returns an observable that contains only `user`
    (userRepository createUser _) expects request returns (Observable just expectedUser)
    (userRepository checkIfUserExists  _) expects GetUserRequest(username) returns (Observable just())

    // When
    // createUserUseCase is executed with argument `request`
    createUserUseCase(request).subscribe(subscriber)

    // Then
    // Verify that `subscriber.onNext` has been called once with `user` as argument
    (subscriber onNext _) verify expectedUser once()

    // Verify that `subscriber.onCompleted` has been called once
    (() => subscriber onCompleted) verify() once()
  }

}
