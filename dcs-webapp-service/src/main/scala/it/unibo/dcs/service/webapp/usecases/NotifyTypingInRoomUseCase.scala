package it.unibo.dcs.service.webapp.usecases

import io.vertx.scala.core.Context
import it.unibo.dcs.commons.RxHelper
import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.commons.interactor.{ThreadExecutorExecutionContext, UseCase}
import it.unibo.dcs.service.webapp.interaction.Requests.{CheckTokenRequest, NotifyTypingUserRequest}
import it.unibo.dcs.service.webapp.interaction.Results.NotifyTypingUserResult
import it.unibo.dcs.service.webapp.repositories.AuthenticationRepository
import rx.lang.scala.Observable

/** It represents the functionality of real-time update on the status of user typing.
  * To do so it first checks the provided token. The actual update to the other users
  * will be delivered by a subscriber to this use case.
  *
  * @param threadExecutor      thread executor that will perform the subscription
  * @param postExecutionThread thread that will be notified of the subscription result
  * @param authRepository      authentication repository reference
  * @usecase real-time update on the status of user typing */
final class NotifyTypingInRoomUseCase(private[this] val threadExecutor: ThreadExecutor,
                                      private[this] val postExecutionThread: PostExecutionThread,
                                      private[this] val authRepository: AuthenticationRepository)
  extends UseCase[NotifyTypingUserResult, NotifyTypingUserRequest](threadExecutor, postExecutionThread) {

  override protected[this] def createObservable(request: NotifyTypingUserRequest):
  Observable[NotifyTypingUserResult] =
    authRepository.checkToken(CheckTokenRequest(request.token, request.username))
      .map(_ => NotifyTypingUserResult(request.username))
}

object NotifyTypingInRoomUseCase {

  /** Factory method to create the use case
    *
    * @param authRepository      authentication repository reference
    * @return the use case object*/
  def apply(authRepository: AuthenticationRepository)(implicit ctx: Context): NotifyTypingInRoomUseCase = {
    val threadExecutor: ThreadExecutor = ThreadExecutorExecutionContext(ctx.owner())
    val postExecutionThread: PostExecutionThread = PostExecutionThread(RxHelper.scheduler(ctx))
    new NotifyTypingInRoomUseCase(threadExecutor, postExecutionThread, authRepository)
  }
}