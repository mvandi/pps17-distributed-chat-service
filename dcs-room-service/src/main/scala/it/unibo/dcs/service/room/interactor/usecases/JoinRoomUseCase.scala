package it.unibo.dcs.service.room.interactor.usecases

import it.unibo.dcs.commons.interactor.UseCase
import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.service.room.interactor.validations.JoinRoomValidation
import it.unibo.dcs.service.room.model.Participation
import it.unibo.dcs.service.room.repository.RoomRepository
import it.unibo.dcs.service.room.request.JoinRoomRequest
import rx.lang.scala.Observable

class JoinRoomUseCase(threadExecutor: ThreadExecutor,
                      postExecutionThread: PostExecutionThread,
                      private[this] val roomRepository: RoomRepository,
                      private[this] val validation: JoinRoomValidation)
  extends UseCase[Participation, JoinRoomRequest](threadExecutor, postExecutionThread) {

  override protected[this] def createObservable(request: JoinRoomRequest): Observable[Participation] =
    validation(request).flatMap(_ => roomRepository.joinRoom(request))

}

/** Companion object */
object JoinRoomUseCase {

  /** Factory method to create the use case
    *
    * @param threadExecutor      thread executor that will perform the subscription
    * @param postExecutionThread thread that will be notified of the subscription result
    * @param roomRepository      room repository reference
    * @param validation          validation reference
    * @return                    an instantiation of the class
    */
  def apply(threadExecutor: ThreadExecutor, postExecutionThread: PostExecutionThread,
            roomRepository: RoomRepository, validation: JoinRoomValidation): JoinRoomUseCase = {
    new JoinRoomUseCase(threadExecutor, postExecutionThread, roomRepository, validation)
  }

}
