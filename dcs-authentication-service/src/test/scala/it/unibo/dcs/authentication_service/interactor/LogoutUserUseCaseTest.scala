package it.unibo.dcs.authentication_service.interactor

import _root_.it.unibo.dcs.authentication_service.request.LogoutUserRequest
import it.unibo.dcs.authentication_service.MocksForUseCases._
import org.scalamock.scalatest.MockFactory
import org.scalatest.FlatSpec
import rx.lang.scala.{Observable, Subscriber}

class LogoutUserUseCaseTest extends FlatSpec with MockFactory {

  val token = "token"
  val request = LogoutUserRequest(token)
  val expectedResult: Unit = Unit

  val subscriber: Subscriber[Unit] = stub[Subscriber[Unit]]
  val logoutUserUseCase = new LogoutUserUseCase(threadExecutor, postExecutionThread, authRepository)

  it should "logout the user when the use case is executed" in {
    (authRepository invalidToken (_, _)) expects (token, *) returns (Observable just expectedResult)

    logoutUserUseCase(request).subscribe(subscriber)

    (subscriber onNext _) verify expectedResult once()
    (() => subscriber onCompleted) verify() once()
  }

}
