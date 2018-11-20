package it.unibo.dcs.service.user.interactor.validations

import it.unibo.dcs.commons.interactor.Validation
import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.commons.validation.Validator
import it.unibo.dcs.service.user.request.CreateUserRequest

final class ValidateUserCreation(private[this] val threadExecutor: ThreadExecutor,
                                 private[this] val postExecutionThread: PostExecutionThread,
                                 private[this] val validator: Validator[CreateUserRequest])
  extends Validation[CreateUserRequest](threadExecutor, postExecutionThread, validator)

/** Companion object */
object ValidateUserCreation {

  /** Factory Method for the Creation User Validation
    *
    * @param threadExecutor      thread executor that will perform the subscription
    * @param postExecutionThread thread that will be notified of the subscription result
    * @param validator           validator for CreateUserRequest
    * @return ValidateUserCreation
    */
  def apply(threadExecutor: ThreadExecutor, postExecutionThread: PostExecutionThread,
            validator: Validator[CreateUserRequest]): ValidateUserCreation = {
    new ValidateUserCreation(threadExecutor, postExecutionThread, validator)
  }
}