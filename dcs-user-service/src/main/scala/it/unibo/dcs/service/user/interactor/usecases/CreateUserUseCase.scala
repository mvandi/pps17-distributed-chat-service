package it.unibo.dcs.service.user.interactor.usecases

import it.unibo.dcs.commons.interactor.UseCase
import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.service.user.interactor.validations.ValidateUserCreation
import it.unibo.dcs.service.user.model.User
import it.unibo.dcs.service.user.repository.UserRepository
import it.unibo.dcs.service.user.request.CreateUserRequest
import rx.lang.scala.Observable

final class CreateUserUseCase(private[this] val threadExecutor: ThreadExecutor,
                              private[this] val postExecutionThread: PostExecutionThread,
                              private[this] val userRepository: UserRepository,
                              private[this] val validation: ValidateUserCreation)
  extends UseCase[User, CreateUserRequest](threadExecutor, postExecutionThread) {

  override protected[this] def createObservable(request: CreateUserRequest): Observable[User] =
    validation(request)
      .flatMap(_ => userRepository.createUser(request))

}
