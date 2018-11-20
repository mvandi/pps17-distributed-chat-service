package it.unibo.dcs.service.room.interactor

import java.util.Date

import it.unibo.dcs.commons.test.JUnitSpec
import it.unibo.dcs.service.room.Mocks.{postExecutionThread, roomRepository, threadExecutor}
import it.unibo.dcs.service.room.interactor.usecases.LeaveRoomUseCase
import it.unibo.dcs.service.room.interactor.validations.LeaveRoomValidation
import it.unibo.dcs.service.room.model.{Participation, Room}
import it.unibo.dcs.service.room.request.LeaveRoomRequest
import it.unibo.dcs.service.room.validator.LeaveRoomValidator
import org.scalamock.scalatest.MockFactory
import org.scalatest.OneInstancePerTest
import rx.lang.scala.{Observable, Subscriber}

final class LeaveRoomUseCaseSpec extends JUnitSpec with MockFactory with OneInstancePerTest {

  private val leaveRoomUseCase = {
    val validation = LeaveRoomValidation(threadExecutor, postExecutionThread, LeaveRoomValidator())
    LeaveRoomUseCase(threadExecutor, postExecutionThread, roomRepository, validation)
  }

  private val request = LeaveRoomRequest("Test room", "martynha")

  private val expectedParticipation = Participation(Room(request.name), request.username, new Date())

  private val subscriber = stub[Subscriber[Participation]]

  it should "return the old participation when the use case is executed" in {
    //Given
    (roomRepository leaveRoom _ ) expects request returns (Observable just expectedParticipation)

    //When
    leaveRoomUseCase(request).subscribe(subscriber)

    //Then
    (subscriber onNext _) verify expectedParticipation once()
    (() => subscriber onCompleted) verify() once()
  }

}
