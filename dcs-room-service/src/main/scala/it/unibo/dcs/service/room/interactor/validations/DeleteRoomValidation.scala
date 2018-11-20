package it.unibo.dcs.service.room.interactor.validations

import it.unibo.dcs.commons.interactor.Validation
import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.commons.validation.Validator
import it.unibo.dcs.service.room.request.DeleteRoomRequest

final class DeleteRoomValidation(private[this] val threadExecutor: ThreadExecutor,
                                 private[this] val postExecutionThread: PostExecutionThread,
                                 private[this] val validator: Validator[DeleteRoomRequest])
  extends Validation[DeleteRoomRequest](threadExecutor, postExecutionThread, validator)

object DeleteRoomValidation {

  /** Factory method to instantiate the class
    *
    * @param threadExecutor      thread executor that will perform the subscription
    * @param postExecutionThread thread that will be notified of the subscription result
    * @param validator           object containing the rules needed to validate
    * @return an instantiation of the class */
  def apply(threadExecutor: ThreadExecutor, postExecutionThread: PostExecutionThread,
            validator: Validator[DeleteRoomRequest]): DeleteRoomValidation =
    new DeleteRoomValidation(threadExecutor, postExecutionThread, validator)
}