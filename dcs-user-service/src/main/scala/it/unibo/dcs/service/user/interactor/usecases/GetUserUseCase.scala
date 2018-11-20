package it.unibo.dcs.service.user.interactor.usecases

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

  override protected[this] def createObservable(request: GetUserRequest): Observable[User] =
    userRepository.getUserByUsername(request)

}

/** Companion object */
object GetUserUseCase {

  /** Factory method to create the use case
    *
    * @param threadExecutor      thread executor that will perform the subscription
    * @param postExecutionThread thread that will be notified of the subscription result
    * @param userRepository user repository reference
    * @return
    */
  def apply(threadExecutor: ThreadExecutor, postExecutionThread: PostExecutionThread,
            userRepository: UserRepository): GetUserUseCase = {
    new GetUserUseCase(threadExecutor, postExecutionThread, userRepository)
  }

}
