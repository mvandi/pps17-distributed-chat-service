package it.unibo.dcs.service.authentication.interactor

import _root_.it.unibo.dcs.commons.test.JUnitSpec
import _root_.it.unibo.dcs.service.authentication.interactor.usecases.LogoutUserUseCase
import _root_.it.unibo.dcs.service.authentication.interactor.validations.LogoutUserValidation
import _root_.it.unibo.dcs.service.authentication.request.Requests.LogoutUserRequest
import _root_.it.unibo.dcs.service.authentication.validator.LogoutUserValidator
import it.unibo.dcs.service.MocksForUseCases._
import org.scalamock.scalatest.MockFactory
import rx.lang.scala.{Observable, Subscriber}

import scala.language.postfixOps

class LogoutUserUseCaseSpec extends JUnitSpec with MockFactory {

  private val username = "ale"
  private val token = "header.eyJzdWIiOiAiYWxlIn0=.signature"
  private val request = LogoutUserRequest(username, token)
  private val expectedResult: Unit = Unit

  private val subscriber: Subscriber[Unit] = stub[Subscriber[Unit]]
  private val validation =
    LogoutUserValidation(threadExecutor, postExecutionThread, LogoutUserValidator())
  private val logoutUserUseCase = new LogoutUserUseCase(threadExecutor, postExecutionThread, authRepository, validation)

  it should "logout the user when the use case is executed" in {
    (authRepository invalidToken(_, _)) expects(token, *) returns (Observable just expectedResult)

    logoutUserUseCase(request).subscribe(subscriber)

    (subscriber onNext _) verify expectedResult once()
    (() => subscriber onCompleted) verify() once()
  }

}
