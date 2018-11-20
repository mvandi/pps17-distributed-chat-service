package it.unibo.dcs.service.authentication.interactor

import _root_.it.unibo.dcs.commons.test.JUnitSpec
import _root_.it.unibo.dcs.service.authentication.interactor.usecases.LoginUserUseCase
import _root_.it.unibo.dcs.service.authentication.interactor.validations.LoginUserValidation
import _root_.it.unibo.dcs.service.authentication.request.Requests.LoginUserRequest
import _root_.it.unibo.dcs.service.authentication.validator.LoginUserValidator
import io.vertx.lang.scala.json.JsonObject
import io.vertx.scala.ext.auth.jwt.JWTOptions
import it.unibo.dcs.service.MocksForUseCases._
import org.scalamock.scalatest.MockFactory
import rx.lang.scala.{Observable, Subscriber}

import scala.language.postfixOps

class LoginUserUseCaseSpec extends JUnitSpec with MockFactory {

  private val request = LoginUserRequest("ale", "123456")
  private val expectedResult = "token"

  private val subscriber: Subscriber[String] = stub[Subscriber[String]]
  private val validation = LoginUserValidation(threadExecutor, postExecutionThread, LoginUserValidator())
  private val loginUserUseCase =
    new LoginUserUseCase(threadExecutor, postExecutionThread, authRepository, jwtAuth, validation)

  it should "login the user when the use case is executed" in {
    (jwtAuth generateToken(_: JsonObject, _: JWTOptions)) expects(*, *) returns expectedResult
    (authRepository checkUserCredentials(_, _)) expects(request.username, request.password) returns
      (Observable just expectedResult)

    loginUserUseCase(request).subscribe(subscriber)

    (subscriber onNext _) verify expectedResult once()
    (() => subscriber onCompleted) verify() once()
  }

}
