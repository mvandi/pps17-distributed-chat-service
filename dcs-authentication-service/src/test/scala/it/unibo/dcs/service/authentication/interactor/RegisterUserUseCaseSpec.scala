package it.unibo.dcs.service.authentication.interactor

import _root_.it.unibo.dcs.commons.test.JUnitSpec
import _root_.it.unibo.dcs.service.authentication.interactor.usecases.RegisterUserUseCase
import _root_.it.unibo.dcs.service.authentication.interactor.validations.RegisterUserValidation
import _root_.it.unibo.dcs.service.authentication.request.Requests.RegisterUserRequest
import _root_.it.unibo.dcs.service.authentication.validator.RegisterUserValidator
import io.vertx.lang.scala.json.JsonObject
import io.vertx.scala.ext.auth.jwt.JWTOptions
import it.unibo.dcs.service.MocksForUseCases._
import org.scalamock.scalatest.MockFactory
import rx.lang.scala.{Observable, Subscriber}

import scala.language.postfixOps

class RegisterUserUseCaseSpec extends JUnitSpec with MockFactory {

  private val request = RegisterUserRequest("ale", "123456")
  private val expectedResult = "token"

  private val subscriber: Subscriber[String] = stub[Subscriber[String]]
  private val validation =
    RegisterUserValidation(threadExecutor, postExecutionThread, RegisterUserValidator())
  private val registerUserUseCase =
    new RegisterUserUseCase(threadExecutor, postExecutionThread, authRepository, jwtAuth, validation)

  it should "register the user when the use case is executed" in {
    (authRepository createUser(_, _)) expects(request.username, request.password) returns (Observable just expectedResult)
    (jwtAuth generateToken(_: JsonObject, _: JWTOptions)) expects(*, *) returns expectedResult

    registerUserUseCase(request).subscribe(subscriber)

    (subscriber onNext _) verify expectedResult once()
    (() => subscriber onCompleted) verify() once()
  }

}