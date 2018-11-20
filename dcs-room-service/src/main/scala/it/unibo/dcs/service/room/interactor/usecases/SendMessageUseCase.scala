package it.unibo.dcs.service.room.interactor.usecases

import it.unibo.dcs.commons.interactor.UseCase
import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.service.room.interactor.validations.SendMessageValidation
import it.unibo.dcs.service.room.model.Message
import it.unibo.dcs.service.room.repository.RoomRepository
import it.unibo.dcs.service.room.request.SendMessageRequest
import rx.lang.scala.Observable

final class SendMessageUseCase (threadExecutor: ThreadExecutor,
                          postExecutionThread: PostExecutionThread,
                          private[this] val roomRepository: RoomRepository,
                          private[this] val validation: SendMessageValidation)
  extends UseCase[Message, SendMessageRequest](threadExecutor, postExecutionThread){

  override protected[this] def createObservable(request: SendMessageRequest): Observable[Message] =
    validation(request).flatMap(_ => roomRepository.sendMessage(request))
}

/** Companion object */
object SendMessageUseCase {

  /** Factory method to create the use case
    *
    * @param threadExecutor      thread executor that will perform the subscription
    * @param postExecutionThread thread that will be notified of the subscription result
    * @param roomRepository      room repository reference
    * @param validation          validation reference
    * @return                    an instantiation of the class
    */
  def apply(threadExecutor: ThreadExecutor, postExecutionThread: PostExecutionThread,
            roomRepository: RoomRepository, validation: SendMessageValidation): SendMessageUseCase = {
    new SendMessageUseCase(threadExecutor, postExecutionThread, roomRepository, validation)
  }

}