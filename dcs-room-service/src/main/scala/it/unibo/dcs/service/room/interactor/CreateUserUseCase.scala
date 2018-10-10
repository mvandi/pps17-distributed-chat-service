package it.unibo.dcs.service.room.interactor

import it.unibo.dcs.commons.interactor.UseCase
import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.service.room.repository.RoomRepository
import it.unibo.dcs.service.room.request.CreateUserRequest
import rx.lang.scala.Observable

final class CreateUserUseCase(threadExecutor: ThreadExecutor, postExecutionThread: PostExecutionThread, private[this] val roomRepository: RoomRepository)
  extends UseCase[Unit, CreateUserRequest](threadExecutor, postExecutionThread) {

  override protected[this] def createObservable(request: CreateUserRequest): Observable[Unit] = roomRepository.createUser(request)

}
