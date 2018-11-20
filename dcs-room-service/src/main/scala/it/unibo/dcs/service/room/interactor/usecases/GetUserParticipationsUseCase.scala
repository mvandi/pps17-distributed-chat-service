package it.unibo.dcs.service.room.interactor.usecases

import it.unibo.dcs.commons.interactor.UseCase
import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.service.room.interactor.validations.GetUserParticipationsValidation
import it.unibo.dcs.service.room.model.Room
import it.unibo.dcs.service.room.repository.RoomRepository
import it.unibo.dcs.service.room.request.GetUserParticipationsRequest
import rx.lang.scala.Observable

final class GetUserParticipationsUseCase(threadExecutor: ThreadExecutor, postExecutionThread: PostExecutionThread,
                                         private[this] val roomRepository: RoomRepository,
                                         private[this] val validation: GetUserParticipationsValidation)
  extends UseCase[List[Room], GetUserParticipationsRequest](threadExecutor, postExecutionThread) {

  override protected[this] def createObservable(request: GetUserParticipationsRequest): Observable[List[Room]] =
    validation(request).flatMap(_ => roomRepository.getParticipationsByUsername(request))

}

/** Companion object */
object GetUserParticipationsUseCase {

  /** Factory method to create the use case
    *
    * @param threadExecutor      thread executor that will perform the subscription
    * @param postExecutionThread thread that will be notified of the subscription result
    * @param roomRepository      room repository reference
    * @param validation          validation reference
    * @return                    an instantiation of the class
    */
  def apply(threadExecutor: ThreadExecutor, postExecutionThread: PostExecutionThread,
            roomRepository: RoomRepository, validation: GetUserParticipationsValidation): GetUserParticipationsUseCase = {
    new GetUserParticipationsUseCase(threadExecutor, postExecutionThread, roomRepository, validation)
  }

}