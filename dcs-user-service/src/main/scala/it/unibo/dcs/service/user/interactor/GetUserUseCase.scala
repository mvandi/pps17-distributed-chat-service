package it.unibo.dcs.service.user.interactor

import it.unibo.dcs.commons.interactor.UseCase
import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.service.user.model.User
import it.unibo.dcs.service.user.repository.UserRepository
import it.unibo.dcs.service.user.request.GetUserRequest
import rx.lang.scala.Observable

final class GetUserUseCase(private[this] val threadExecutor: ThreadExecutor,
                           private[this] val postExecutionThread: PostExecutionThread,
                           private[this] val userRepository: UserRepository)
  extends UseCase[User, GetUserRequest](threadExecutor, postExecutionThread) {

  override protected[this] def createObservable(request: GetUserRequest): Observable[User] = userRepository.getUserByUsername(request)

}
