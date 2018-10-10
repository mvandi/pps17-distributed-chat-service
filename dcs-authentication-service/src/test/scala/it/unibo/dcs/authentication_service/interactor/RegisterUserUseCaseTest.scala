package it.unibo.dcs.authentication_service.interactor

import _root_.it.unibo.dcs.authentication_service.request.RegisterUserRequest
import io.vertx.lang.scala.json.JsonObject
import io.vertx.scala.ext.auth.jwt.JWTOptions
import it.unibo.dcs.authentication_service.MocksForUseCases._
import org.scalamock.scalatest.MockFactory
import org.scalatest.FlatSpec
import rx.lang.scala.{Observable, Subscriber}

class RegisterUserUseCaseTest extends FlatSpec with MockFactory {

  val request = RegisterUserRequest("ale", "123456")
  val expectedResult = "token"

  val subscriber: Subscriber[String] = stub[Subscriber[String]]

  val registerUserUseCase = new RegisterUserUseCase(threadExecutor, postExecutionThread, authRepository, jwtAuth)

  it should "register the user when the use case is executed" in {
    (authRepository createUser(_, _)) expects (request.username, request.password) returns (Observable just expectedResult)
    (jwtAuth generateToken (_: JsonObject, _:JWTOptions)) expects (*, *) returns expectedResult

    registerUserUseCase(request).subscribe(subscriber)

    (subscriber onNext _) verify expectedResult once()
    (() => subscriber onCompleted) verify() once()
  }

}