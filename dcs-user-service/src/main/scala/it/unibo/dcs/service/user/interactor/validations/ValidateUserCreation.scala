package it.unibo.dcs.service.user.interactor.validations

import it.unibo.dcs.commons.interactor.SimpleValidation
import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.commons.validation.Validator
import it.unibo.dcs.service.user.request.CreateUserRequest

final class ValidateUserCreation(private[this] val threadExecutor: ThreadExecutor,
                                 private[this] val postExecutionThread: PostExecutionThread,
                                 private[this] val validator: Validator[CreateUserRequest])
  extends SimpleValidation[CreateUserRequest](threadExecutor, postExecutionThread, validator)
