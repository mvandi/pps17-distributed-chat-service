package it.unibo.dcs.service.user.interactor.usecases

import it.unibo.dcs.commons.interactor.UseCase
import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.service.user.repository.UserRepository
import rx.lang.scala.Observable

final class UpdateUserAccessUseCase(private[this] val threadExecutor: ThreadExecutor,
                                    private[this] val postExecutionThread: PostExecutionThread,
                                    private[this] val userRepository: UserRepository)
  extends UseCase[Unit, String](threadExecutor, postExecutionThread) {

  override protected[this] def createObservable(username: String): Observable[Unit] =
    userRepository.updateAccess(username)

}


/** Companion object */
object UpdateUserAccessUseCase {

  /** Factory method to create the use case
    *
    * @param threadExecutor      thread executor that will perform the subscription
    * @param postExecutionThread thread that will be notified of the subscription result
    * @param userRepository user repository reference
    * @return
    */
  def apply(threadExecutor: ThreadExecutor, postExecutionThread: PostExecutionThread,
            userRepository: UserRepository): UpdateUserAccessUseCase = {
    new UpdateUserAccessUseCase(threadExecutor, postExecutionThread, userRepository)
  }

}