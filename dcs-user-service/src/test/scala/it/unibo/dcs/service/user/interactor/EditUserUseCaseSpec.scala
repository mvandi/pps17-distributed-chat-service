package it.unibo.dcs.service.user.interactor

import java.util.Date

import it.unibo.dcs.commons.test.JUnitSpec
import it.unibo.dcs.service.user.Mocks.{postExecutionThread, threadExecutor, userRepository}
import it.unibo.dcs.service.user.interactor.usecases.EditUserUseCase
import it.unibo.dcs.service.user.interactor.validations.ValidateUserEditing
import it.unibo.dcs.service.user.model.User
import it.unibo.dcs.service.user.request.{EditUserRequest, GetUserRequest}
import it.unibo.dcs.service.user.validator.UserEditingValidator
import org.scalamock.scalatest.MockFactory
import rx.lang.scala.{Observable, Subscriber}

class EditUserUseCaseSpec extends JUnitSpec with MockFactory {

  private val request = EditUserRequest("martynha", "Martina", "Magnani", "bio", visible = true)
  private val expectedUser =
    User(request.username, request.firstName, request.lastName, request.bio, request.visible, new Date())

  private val subscriber = stub[Subscriber[User]]

  private val editProfileUseCase = {
    val validator = UserEditingValidator(userRepository)
    val validation = ValidateUserEditing(threadExecutor, postExecutionThread, validator)
    EditUserUseCase(threadExecutor, postExecutionThread, userRepository, validation)
  }

  it should "edit the specified profile when the use case is executed" in {
    // Given
    (userRepository editUser _) expects request returns (Observable just expectedUser)
    (userRepository checkIfUserExists  _) expects GetUserRequest(request.username) returns (Observable just())

    // When
    editProfileUseCase(request).subscribe(subscriber)

    // Then
    (subscriber onNext _) verify expectedUser once()

    (() => subscriber onCompleted) verify() once()
  }

}
