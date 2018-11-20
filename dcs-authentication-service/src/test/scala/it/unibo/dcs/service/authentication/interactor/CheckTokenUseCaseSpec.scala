package it.unibo.dcs.service.authentication.interactor

import it.unibo.dcs.commons.test.JUnitSpec
import it.unibo.dcs.service.MocksForUseCases.{authRepository, postExecutionThread, threadExecutor}
import it.unibo.dcs.service.authentication.interactor.usecases.CheckTokenUseCase
import it.unibo.dcs.service.authentication.interactor.validations.CheckTokenValidation
import it.unibo.dcs.service.authentication.request.Requests.CheckTokenRequest
import it.unibo.dcs.service.authentication.validator.CheckTokenValidator
import org.scalamock.scalatest.MockFactory
import rx.lang.scala.{Observable, Subscriber}

import scala.language.postfixOps

class CheckTokenUseCaseSpec extends JUnitSpec with MockFactory {

  private val username = "ale"
  private val token = "header.eyJzdWIiOiAiYWxlIn0=.signature"
  private val request = CheckTokenRequest(token, username)
  private val expectedResult = true

  private val subscriber: Subscriber[Boolean] = stub[Subscriber[Boolean]]
  private val validation = CheckTokenValidation(threadExecutor, postExecutionThread, CheckTokenValidator.apply)
  private val useCase = new CheckTokenUseCase(threadExecutor, postExecutionThread, authRepository, validation)

  it should "return true when the use case is executed" in {
    (authRepository isTokenValid _) expects request.token returns (Observable just expectedResult)
    useCase(request).subscribe(subscriber)

    (subscriber onNext _) verify expectedResult once()
    (() => subscriber onCompleted) verify() once()
  }

}