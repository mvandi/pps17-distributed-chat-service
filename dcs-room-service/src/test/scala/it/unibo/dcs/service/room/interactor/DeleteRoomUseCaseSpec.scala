package it.unibo.dcs.service.room.interactor

import it.unibo.dcs.commons.test.JUnitSpec
import it.unibo.dcs.service.room.Mocks._
import it.unibo.dcs.service.room.interactor.usecases.DeleteRoomUseCase
import it.unibo.dcs.service.room.interactor.validations.DeleteRoomValidation
import it.unibo.dcs.service.room.request.DeleteRoomRequest
import it.unibo.dcs.service.room.validator.DeleteRoomValidator
import org.scalamock.scalatest.MockFactory
import org.scalatest.OneInstancePerTest
import rx.lang.scala.{Observable, Subscriber}

import scala.language.postfixOps

final class DeleteRoomUseCaseSpec extends JUnitSpec with MockFactory with OneInstancePerTest {

  private val deleteRoomUseCase = {
    val validation = DeleteRoomValidation(threadExecutor, postExecutionThread, DeleteRoomValidator())
    DeleteRoomUseCase(threadExecutor, postExecutionThread, roomRepository, validation)
  }

  private val request = DeleteRoomRequest("Test room", "mvandi")

  private val subscriber = stub[Subscriber[String]]

  it should "Delete a room if the user who is trying to delete the room is also the user who created the room" in {
    (roomRepository deleteRoom _) expects request returns Observable.just(request.name)

    deleteRoomUseCase(request).subscribe(subscriber)

    subscriber.onNext _ verify request.name once()
    (() => subscriber onCompleted) verify() once()
  }

}
