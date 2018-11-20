package it.unibo.dcs.service.webapp.usecases

import io.vertx.scala.core.Context
import it.unibo.dcs.commons.RxHelper
import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.commons.interactor.{ThreadExecutorExecutionContext, UseCase}
import it.unibo.dcs.commons.logging.Logging
import it.unibo.dcs.exceptions.{RoomServiceErrorException, UserServiceErrorException}
import it.unibo.dcs.service.webapp.interaction.Requests.{DeleteUserRequest, RegisterUserRequest}
import it.unibo.dcs.service.webapp.interaction.Results.RegisterResult
import it.unibo.dcs.service.webapp.model.User
import it.unibo.dcs.service.webapp.repositories.{AuthenticationRepository, RoomRepository, UserRepository}
import rx.lang.scala.Observable

/** It represents the user registration functionality.
  * It calls the authentication service to retrieve the token,
  * then it contacts the User Service to store the new User and
  * finally it tells Room Service to .
  *
  * @param threadExecutor      thread executor that will perform the subscription
  * @param postExecutionThread thread that will be notified of the subscription result
  * @param authRepository      authentication repository reference
  * @param userRepository      user repository reference
  * @param roomRepository      room repository reference
  * @usecase registration of a new user */
final class RegisterUserUseCase(private[this] val threadExecutor: ThreadExecutor,
                                private[this] val postExecutionThread: PostExecutionThread,
                                private[this] val authRepository: AuthenticationRepository,
                                private[this] val userRepository: UserRepository,
                                private[this] val roomRepository: RoomRepository)
  extends UseCase[RegisterResult, RegisterUserRequest](threadExecutor, postExecutionThread) with Logging {

  override protected[this] def createObservable(request: RegisterUserRequest): Observable[RegisterResult] =
    for {
      token <- authRepository.registerUser(request)
      user <- userRepository.registerUser(request).onErrorResumeNext {
        case UserServiceErrorException(error) =>
          log.info("Rolling back auth repository...")
          rollbackAuthRepository(request.username, token, error)
      }
      _ <- roomRepository.registerUser(request).onErrorResumeNext {
        case RoomServiceErrorException(error) =>
          log.info("Rolling back auth and room repository...")
          rollbackAuthRepository(request.username, token, error)
          rollbackUserRepository(request.username, error)
      }
    } yield RegisterResult(user, token)

  /* Rollback changes previously performed in Authentication service */
  private def rollbackAuthRepository(username: String, token: String, error: Throwable): Observable[User] = {
    authRepository.deleteUser(DeleteUserRequest(username, token))
      .map(_ => throw error)
  }

  /* Rollback changes previously performed in User service */
  private def rollbackUserRepository(username: String, error: Throwable): Observable[User] = {
    userRepository.deleteUser(username)
      .map(_ => throw error)
  }

}

/** Companion object */
object RegisterUserUseCase {

  /** Factory method to create the use case
    *
    * @param authRepository authentication repository reference
    * @param userRepository user repository reference
    * @param roomRepository room repository reference
    * @param ctx            Vertx context
    * @return the use case object */
  def create(authRepository: AuthenticationRepository,
             userRepository: UserRepository,
             roomRepository: RoomRepository)(implicit ctx: Context): RegisterUserUseCase = {
    val threadExecutor: ThreadExecutor = ThreadExecutorExecutionContext(ctx.owner())
    val postExecutionThread: PostExecutionThread = PostExecutionThread(RxHelper.scheduler(ctx))
    new RegisterUserUseCase(threadExecutor, postExecutionThread, authRepository, userRepository, roomRepository)
  }

}
