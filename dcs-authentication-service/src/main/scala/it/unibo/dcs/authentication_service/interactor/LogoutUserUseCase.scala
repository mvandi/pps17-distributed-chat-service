package it.unibo.dcs.authentication_service.interactor

import java.time.LocalDateTime

import it.unibo.dcs.authentication_service.repository.AuthenticationRepository
import it.unibo.dcs.authentication_service.request.LogoutUserRequest
import it.unibo.dcs.commons.interactor.UseCase
import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import rx.lang.scala.Observable

final class LogoutUserUseCase(private[this] val threadExecutor: ThreadExecutor,
                         private[this] val postExecutionThread: PostExecutionThread,
                         private[this] val authRepository: AuthenticationRepository)
  extends UseCase[Unit, LogoutUserRequest](threadExecutor, postExecutionThread) {

  override protected[this] def createObservable(request: LogoutUserRequest): Observable[Unit] =
    authRepository.invalidToken(request.token, LocalDateTime.now())
}