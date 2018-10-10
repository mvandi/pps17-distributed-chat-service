package it.unibo.dcs.service.webapp.usecases

import io.vertx.scala.core.Context
import it.unibo.dcs.commons.RxHelper
import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.commons.interactor.{ThreadExecutorExecutionContext, UseCase}
import it.unibo.dcs.service.webapp.repositories.AuthenticationRepository
import rx.lang.scala.Observable

final class LogoutUserUseCase(private[this] val threadExecutor: ThreadExecutor,
                              private[this] val postExecutionThread: PostExecutionThread,
                              private[this] val authRepository: AuthenticationRepository)
  extends UseCase[Unit, String](threadExecutor, postExecutionThread) {

  override protected[this] def createObservable(username: String): Observable[Unit] = {
    authRepository.logoutUser(username)
  }
}

object LogoutUserUseCase {
  def create(authRepository: AuthenticationRepository)(implicit ctx: Context): LogoutUserUseCase = {
    val threadExecutor: ThreadExecutor = ThreadExecutorExecutionContext(ctx.owner())
    val postExecutionThread: PostExecutionThread = PostExecutionThread(RxHelper.scheduler(ctx))
    new LogoutUserUseCase(threadExecutor, postExecutionThread, authRepository)
  }
}
