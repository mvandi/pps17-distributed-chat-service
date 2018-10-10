package it.unibo.dcs.authentication_service.interactor

import io.vertx.lang.scala.json.JsonObject
import it.unibo.dcs.authentication_service.MocksForUseCases._
import _root_.it.unibo.dcs.authentication_service.request.LoginUserRequest
import io.vertx.scala.ext.auth.jwt.JWTOptions
import org.scalamock.scalatest.MockFactory
import rx.lang.scala.{Observable, Subscriber}
import org.scalatest.FlatSpec

class LoginUserUseCaseTest extends FlatSpec with MockFactory {

  val request = LoginUserRequest("ale", "123456")
  val expectedResult = "token"

  val subscriber: Subscriber[String] = stub[Subscriber[String]]
  val loginUserUseCase = new LoginUserUseCase(threadExecutor, postExecutionThread, authRepository, jwtAuth)

  it should "login the user when the use case is executed" in {
    (jwtAuth generateToken (_: JsonObject, _: JWTOptions)) expects (*, *) returns expectedResult
    (authRepository loginUser(_, _)) expects (request.username, request.password) returns (Observable just expectedResult)

    loginUserUseCase(request).subscribe(subscriber)

    (subscriber onNext _) verify expectedResult once()
    (() => subscriber onCompleted) verify() once()
  }

}
