package it.unibo.dcs.service

import io.vertx.scala.ext.auth.jwt.JWTAuth
import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.service.authentication.repository.AuthenticationRepository
import org.scalamock.scalatest.MockFactory
import org.scalatest.FlatSpec

object MocksForUseCases extends MockFactory {

  val threadExecutor: ThreadExecutor = mock[ThreadExecutor]
  val postExecutionThread: PostExecutionThread = mock[PostExecutionThread]
  val authRepository: AuthenticationRepository = mock[AuthenticationRepository]
  val jwtAuth: JWTAuth = mock[JWTAuth]
}
