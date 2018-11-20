package it.unibo.dcs.service.room.interactor

import java.util.Date

import it.unibo.dcs.commons.test.JUnitSpec
import it.unibo.dcs.service.room.Mocks._
import it.unibo.dcs.service.room.interactor.usecases.JoinRoomUseCase
import it.unibo.dcs.service.room.interactor.validations.JoinRoomValidation
import it.unibo.dcs.service.room.model.{Participation, Room}
import it.unibo.dcs.service.room.request.JoinRoomRequest
import it.unibo.dcs.service.room.validator.JoinRoomValidator
import org.scalamock.scalatest.MockFactory
import org.scalatest.OneInstancePerTest
import rx.lang.scala.{Observable, Subscriber}

import scala.language.postfixOps

final class JoinRoomUseCaseSpec extends JUnitSpec with MockFactory with OneInstancePerTest {

  private val joinRoomUseCase = {
    val validation = JoinRoomValidation(threadExecutor, postExecutionThread, JoinRoomValidator())
    JoinRoomUseCase(threadExecutor, postExecutionThread, roomRepository, validation)
  }

  private val request = JoinRoomRequest("Test room", "martynha")

  private val expectedParticipation = Participation(Room(request.name), request.username, new Date())

  private val subscriber = stub[Subscriber[Participation]]

  it should "create a new participation when the use case is executed" in {
    //Given
    (roomRepository joinRoom _ ) expects request returns (Observable just expectedParticipation)

    //When
    joinRoomUseCase(request).subscribe(subscriber)

    //Then
    (subscriber onNext _) verify expectedParticipation once()
    (() => subscriber onCompleted) verify() once()
  }

}
