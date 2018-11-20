package it.unibo.dcs.service.room.interactor

import it.unibo.dcs.commons.test.JUnitSpec
import it.unibo.dcs.service.room.Mocks._
import it.unibo.dcs.service.room.interactor.usecases.CreateUserUseCase
import it.unibo.dcs.service.room.interactor.validations.CreateUserValidation
import it.unibo.dcs.service.room.request.CreateUserRequest
import it.unibo.dcs.service.room.validator.CreateUserValidator
import org.scalamock.scalatest.MockFactory
import org.scalatest.OneInstancePerTest
import rx.lang.scala.{Observable, Subscriber}

import scala.language.postfixOps

final class CreateUserUseCaseSpec extends JUnitSpec with MockFactory with OneInstancePerTest {

  private val createUserUseCase = {
    val validation = CreateUserValidation(threadExecutor, postExecutionThread, CreateUserValidator())
    CreateUserUseCase(threadExecutor, postExecutionThread, roomRepository, validation)
  }

  private val subscriber = stub[Subscriber[Unit]]

  private val request = CreateUserRequest("mvandi")

  it should "create a new user when the use case is executed" in {
    // Given
    (roomRepository createUser _) expects request returns Observable.just()

    // When
    createUserUseCase(request).subscribe(subscriber)

    // Then
    (() => subscriber onCompleted) verify() once()
  }

}
