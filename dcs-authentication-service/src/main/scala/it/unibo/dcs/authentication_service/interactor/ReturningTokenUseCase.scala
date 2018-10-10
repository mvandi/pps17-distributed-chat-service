package it.unibo.dcs.authentication_service.interactor

import io.vertx.scala.ext.auth.jwt.JWTAuth
import it.unibo.dcs.authentication_service.business_logic.JwtTokenGenerator
import it.unibo.dcs.authentication_service.repository.AuthenticationRepository
import it.unibo.dcs.authentication_service.request.TokenRequest
import it.unibo.dcs.commons.interactor.UseCase
import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import rx.lang.scala.Observable

abstract class ReturningTokenUseCase(private[this] val threadExecutor: ThreadExecutor,
                             private[this] val postExecutionThread: PostExecutionThread,
                             private[this] val authRepository: AuthenticationRepository,
                             private[this] val jwtAuth: JWTAuth)
  extends UseCase[String, TokenRequest](threadExecutor, postExecutionThread) {

  val tokenGenerator = new JwtTokenGenerator(jwtAuth)

  override protected[this] def createObservable(request: TokenRequest): Observable[String] =
    getMainObservable(request.username, request.password).map(_ => tokenGenerator.generate(request.username))

  protected def getMainObservable(username: String, password: String): Observable[Unit]
}