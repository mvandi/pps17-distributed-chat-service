package it.unibo.dcs.service.webapp.usecases

import io.vertx.scala.core.Context
import it.unibo.dcs.commons.RxHelper
import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.commons.interactor.{ThreadExecutorExecutionContext, UseCase}
import it.unibo.dcs.service.webapp.interaction.Requests.{CheckTokenRequest, EditUserRequest}
import it.unibo.dcs.service.webapp.interaction.Results.UserEditingResult
import it.unibo.dcs.service.webapp.repositories.{AuthenticationRepository, UserRepository}
import rx.lang.scala.Observable

/** It represents the user editing functionality.
  * It calls the authentication service to check the token validity and then
  * it contacts the user service to edit the user's profile.
  *
  * @param threadExecutor      thread executor that will perform the subscription
  * @param postExecutionThread thread that will be notified of the subscription result
  * @param authRepository      authentication repository reference
  * @param userRepository      user repository reference
  * @usecase editing of an user profile */
final class EditUserUseCase(private[this] val threadExecutor: ThreadExecutor,
                              private[this] val postExecutionThread: PostExecutionThread,
                              private[this] val authRepository: AuthenticationRepository,
                              private[this] val userRepository: UserRepository)
  extends UseCase[UserEditingResult, EditUserRequest](threadExecutor, postExecutionThread) {

  override protected[this] def createObservable(request: EditUserRequest): Observable[UserEditingResult] =
    for {
      _ <- authRepository.checkToken(CheckTokenRequest(request.token, request.username))
      user <- userRepository.editUser(request)
    } yield UserEditingResult(user)
}

/** Companion object */
object EditUserUseCase {

  /** Factory method to create the use case
    *
    * @param authRepository authentication repository reference
    * @param userRepository user repository reference
    * @param ctx            Vertx context
    * @return the use case object */
  def create(authRepository: AuthenticationRepository, userRepository: UserRepository)
           (implicit ctx: Context): EditUserUseCase = {
    val threadExecutor: ThreadExecutor = ThreadExecutorExecutionContext(ctx.owner())
    val postExecutionThread: PostExecutionThread = PostExecutionThread(RxHelper.scheduler(ctx))
    new EditUserUseCase(threadExecutor, postExecutionThread, authRepository, userRepository)
  }
}