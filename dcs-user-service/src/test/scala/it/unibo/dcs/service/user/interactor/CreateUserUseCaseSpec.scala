package it.unibo.dcs.service.user.interactor

import java.util.Date

import it.unibo.dcs.commons.test.JUnitSpec
import it.unibo.dcs.service.user.Mocks._
import it.unibo.dcs.service.user.interactor.usecases.CreateUserUseCase
import it.unibo.dcs.service.user.interactor.validations.ValidateUserCreation
import it.unibo.dcs.service.user.model.User
import it.unibo.dcs.service.user.request.{CreateUserRequest, GetUserRequest}
import it.unibo.dcs.service.user.validator.UserCreationValidator
import org.scalamock.scalatest.MockFactory
import rx.lang.scala.{Observable, Subscriber}

import scala.language.postfixOps

class CreateUserUseCaseSpec extends JUnitSpec with MockFactory {

  private val request = CreateUserRequest("martynha", "Martina", "Magnani")
  private val expectedUser = User(request.username, request.firstName, request.lastName, "", visible = true, new Date())

  private val subscriber = stub[Subscriber[User]]

  private val createUserUseCase = {
    val validator = UserCreationValidator(userRepository)
    val validation = ValidateUserCreation(threadExecutor, postExecutionThread, validator)
    CreateUserUseCase(threadExecutor, postExecutionThread, userRepository, validation)
  }

  it should "create a new user when the use case is executed" in {
    // Given
    // userRepository is called with `request` as parameter returns an observable that contains only `user`
    (userRepository createUser _) expects request returns (Observable just expectedUser)
    (userRepository checkIfUserExists  _) expects GetUserRequest(request.username) returns (Observable just())

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
