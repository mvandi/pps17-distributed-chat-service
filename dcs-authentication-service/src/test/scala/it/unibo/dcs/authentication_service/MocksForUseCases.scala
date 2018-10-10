package it.unibo.dcs.authentication_service

import io.vertx.scala.ext.auth.jwt.JWTAuth
import it.unibo.dcs.authentication_service.repository.AuthenticationRepository
import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import org.scalamock.scalatest.MockFactory
import org.scalatest.FlatSpec

object MocksForUseCases extends FlatSpec with MockFactory{

  val threadExecutor: ThreadExecutor = mock[ThreadExecutor]
  val postExecutionThread: PostExecutionThread = mock[PostExecutionThread]
  val authRepository: AuthenticationRepository = mock[AuthenticationRepository]
  val jwtAuth: JWTAuth = mock[JWTAuth]
}
