package it.unibo.dcs.service.webapp.usecases

import io.vertx.scala.core.Context
import it.unibo.dcs.commons.RxHelper
import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.commons.interactor.{ThreadExecutorExecutionContext, UseCase}
import it.unibo.dcs.service.webapp.repositories.Requests.LoginUserRequest
import it.unibo.dcs.service.webapp.repositories.{AuthenticationRepository, UserRepository}
import it.unibo.dcs.service.webapp.usecases.Results.LoginResult
import rx.lang.scala.Observable


final class LoginUserUseCase(private[this] val threadExecutor: ThreadExecutor,
                             private[this] val postExecutionThread: PostExecutionThread,
                             private[this] val authRepository: AuthenticationRepository,
                             private[this] val userRepository: UserRepository)
  extends UseCase[LoginResult, LoginUserRequest](threadExecutor, postExecutionThread) {

  override protected[this] def createObservable(loginRequest: LoginUserRequest): Observable[LoginResult] = {
    /* Monadic style */
    for {
      token <- authRepository.loginUser(loginRequest)
      user <- userRepository.getUserByUsername(loginRequest.username)
    } yield LoginResult(user, token)
  }

}

object LoginUserUseCase {
  def create(authRepository: AuthenticationRepository,
             userRepository: UserRepository)(implicit ctx: Context): LoginUserUseCase = {
    val threadExecutor: ThreadExecutor = ThreadExecutorExecutionContext(ctx.owner())
    val postExecutionThread: PostExecutionThread = PostExecutionThread(RxHelper.scheduler(ctx))
    new LoginUserUseCase(threadExecutor, postExecutionThread, authRepository, userRepository)
  }
}
