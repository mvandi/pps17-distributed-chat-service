package it.unibo.dcs.service.webapp.usecases

import io.vertx.scala.core.Context
import it.unibo.dcs.commons.RxHelper
import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.commons.interactor.{ThreadExecutorExecutionContext, UseCase}
import it.unibo.dcs.service.webapp.interaction.Requests.{CheckTokenRequest, GetMessagesRequest}
import it.unibo.dcs.service.webapp.interaction.Results.GetMessagesResult
import it.unibo.dcs.service.webapp.repositories.{AuthenticationRepository, RoomRepository}
import rx.lang.scala.Observable

class GetMessagesUseCase (private[this] val threadExecutor: ThreadExecutor,
                          private[this] val postExecutionThread: PostExecutionThread,
                          private[this] val authRepository: AuthenticationRepository,
                          private[this] val roomRepository: RoomRepository)
  extends UseCase[GetMessagesResult, GetMessagesRequest](threadExecutor, postExecutionThread) {

  override protected[this] def createObservable(request: GetMessagesRequest): Observable[GetMessagesResult] =
    for {
      _ <- authRepository.checkToken(CheckTokenRequest(request.token, request.username))
      messages <- roomRepository.getMessages(request)
    } yield GetMessagesResult(messages)

}

object GetMessagesUseCase {

  /** Factory method to create the use case
    *
    * @param authRepository authentication repository reference
    * @param roomRepository room repository reference
    * @param ctx            Vertx context
    * @return the use case object */
  def apply(authRepository: AuthenticationRepository, roomRepository: RoomRepository)
           (implicit ctx: Context): GetMessagesUseCase = {
    val threadExecutor: ThreadExecutor = ThreadExecutorExecutionContext(ctx.owner())
    val postExecutionThread: PostExecutionThread = PostExecutionThread(RxHelper.scheduler(ctx))
    new GetMessagesUseCase(threadExecutor, postExecutionThread, authRepository, roomRepository)
  }
}
