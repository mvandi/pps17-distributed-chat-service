package it.unibo.dcs.service.webapp.usecases

import io.vertx.scala.core.Context
import it.unibo.dcs.commons.RxHelper
import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.commons.interactor.{ThreadExecutorExecutionContext, UseCase}
import it.unibo.dcs.service.webapp.interaction.Requests.GetUserRequest
import it.unibo.dcs.service.webapp.interaction.Results.GetUserResult
import it.unibo.dcs.service.webapp.repositories.{AuthenticationRepository, UserRepository}
import rx.lang.scala.Observable

/** It represents the functionality of retrieving a user information.
  * It calls the authentication service to check the token validity and then
  * it contacts the user service to fetch the user's profile information.
  *
  * @param threadExecutor      thread executor that will perform the subscription
  * @param postExecutionThread thread that will be notified of the subscription result
  * @param authRepository      authentication repository reference
  * @param userRepository      user repository reference
  * @usecase editing of an user profile */
final class GetUserUseCase(private[this] val threadExecutor: ThreadExecutor,
                           private[this] val postExecutionThread: PostExecutionThread,
                           private[this] val authRepository: AuthenticationRepository,
                           private[this] val userRepository: UserRepository)
  extends UseCase[GetUserResult, GetUserRequest](threadExecutor, postExecutionThread) {

  override protected[this] def createObservable(request: GetUserRequest): Observable[GetUserResult] =
    userRepository.getUserByUsername(request.username)
      .map(GetUserResult)
}

/** Companion object */
object GetUserUseCase {

  /** Factory method to create the use case
    *
    * @param authRepository authentication repository reference
    * @param userRepository user repository reference
    * @param ctx            Vertx context
    * @return the use case object */
  def apply(authRepository: AuthenticationRepository, userRepository: UserRepository)
            (implicit ctx: Context): GetUserUseCase = {
    val threadExecutor: ThreadExecutor = ThreadExecutorExecutionContext(ctx.owner())
    val postExecutionThread: PostExecutionThread = PostExecutionThread(RxHelper.scheduler(ctx))
    new GetUserUseCase(threadExecutor, postExecutionThread, authRepository, userRepository)
  }
}
