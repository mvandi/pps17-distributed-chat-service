package it.unibo.dcs.service.webapp.usecases

import io.vertx.scala.core.Context
import it.unibo.dcs.commons.RxHelper
import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.commons.interactor.{ThreadExecutorExecutionContext, UseCase}
import it.unibo.dcs.service.webapp.interaction.Requests.{CheckTokenRequest, SendMessageRequest}
import it.unibo.dcs.service.webapp.interaction.Results.SendMessageResult
import it.unibo.dcs.service.webapp.repositories.{AuthenticationRepository, RoomRepository}
import rx.lang.scala.Observable

/** It represents the room join functionality.
  * It calls the authentication service to check the token validity and then
  * it contacts the room service to send message from the user to the room.
  *
  * @param threadExecutor      thread executor that will perform the subscription
  * @param postExecutionThread thread that will be notified of the subscription result
  * @param authRepository      authentication repository reference
  * @param roomRepository      room repository reference
  * @usecase user send message in a room */
final class SendMessageUseCase (private[this] val threadExecutor: ThreadExecutor,
                                private[this] val postExecutionThread: PostExecutionThread,
                                private[this] val authRepository: AuthenticationRepository,
                                private[this] val roomRepository: RoomRepository)
  extends UseCase[SendMessageResult, SendMessageRequest](threadExecutor, postExecutionThread) {

  override protected[this] def createObservable(request: SendMessageRequest): Observable[SendMessageResult] =
    for {
      _ <- authRepository.checkToken(CheckTokenRequest(request.token, request.username))
      participation <- roomRepository.sendMessage(request)
    } yield SendMessageResult(participation)
}

/** Companion object */
object SendMessageUseCase {

  /** Factory method to create the use case
    *
    * @param authRepository authentication repository reference
    * @param roomRepository room repository reference
    * @param ctx            Vertx context
    * @return the use case object */
  def apply(authRepository: AuthenticationRepository, roomRepository: RoomRepository)
           (implicit ctx: Context): SendMessageUseCase = {
    val threadExecutor: ThreadExecutor = ThreadExecutorExecutionContext(ctx.owner())
    val postExecutionThread: PostExecutionThread = PostExecutionThread(RxHelper.scheduler(ctx))
    new SendMessageUseCase(threadExecutor, postExecutionThread, authRepository, roomRepository)
  }
}