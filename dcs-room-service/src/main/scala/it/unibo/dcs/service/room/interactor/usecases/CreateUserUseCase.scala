package it.unibo.dcs.service.room.interactor.usecases

import it.unibo.dcs.commons.interactor.UseCase
import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.service.room.interactor.validations.CreateUserValidation
import it.unibo.dcs.service.room.repository.RoomRepository
import it.unibo.dcs.service.room.request.CreateUserRequest
import rx.lang.scala.Observable

final class CreateUserUseCase(threadExecutor: ThreadExecutor,
                              postExecutionThread: PostExecutionThread,
                              private[this] val roomRepository: RoomRepository,
                              private[this] val createUserValidation: CreateUserValidation)
  extends UseCase[Unit, CreateUserRequest](threadExecutor, postExecutionThread) {

  override protected[this] def createObservable(request: CreateUserRequest): Observable[Unit] =
    createUserValidation(request).flatMap(_ => roomRepository.createUser(request))

}

/** Companion object */
object CreateUserUseCase {

  /** Factory method to create the use case
    *
    * @param threadExecutor      thread executor that will perform the subscription
    * @param postExecutionThread thread that will be notified of the subscription result
    * @param roomRepository      room repository reference
    * @param validation          validation reference
    * @return                    an instantiation of the class
    */
  def apply(threadExecutor: ThreadExecutor, postExecutionThread: PostExecutionThread,
            roomRepository: RoomRepository, validation: CreateUserValidation): CreateUserUseCase = {
    new CreateUserUseCase(threadExecutor, postExecutionThread, roomRepository, validation)
  }

}