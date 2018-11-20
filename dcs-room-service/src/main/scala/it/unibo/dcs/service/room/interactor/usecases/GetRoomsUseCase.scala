package it.unibo.dcs.service.room.interactor.usecases

import it.unibo.dcs.commons.interactor.UseCase
import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.service.room.interactor.validations.GetRoomsValidation
import it.unibo.dcs.service.room.model.Room
import it.unibo.dcs.service.room.repository.RoomRepository
import it.unibo.dcs.service.room.request.GetRoomsRequest
import rx.lang.scala.Observable

final class GetRoomsUseCase(threadExecutor: ThreadExecutor, postExecutionThread: PostExecutionThread,
                            private[this] val roomRepository: RoomRepository,
                            private[this] val getRoomsValidation: GetRoomsValidation)
  extends UseCase[List[Room], GetRoomsRequest](threadExecutor, postExecutionThread) {

  override protected[this] def createObservable(request: GetRoomsRequest): Observable[List[Room]] =
    getRoomsValidation(request).flatMap(_ => roomRepository.getRooms(request))

}

/** Companion object */
object GetRoomsUseCase {

  /** Factory method to create the use case
    *
    * @param threadExecutor      thread executor that will perform the subscription
    * @param postExecutionThread thread that will be notified of the subscription result
    * @param roomRepository      room repository reference
    * @param validation          validation reference
    * @return                    an instantiation of the class
    */
  def apply(threadExecutor: ThreadExecutor, postExecutionThread: PostExecutionThread,
            roomRepository: RoomRepository, validation: GetRoomsValidation): GetRoomsUseCase = {
    new GetRoomsUseCase(threadExecutor, postExecutionThread, roomRepository, validation)
  }

}