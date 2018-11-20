package it.unibo.dcs.service.room.interactor

import java.util.Date

import it.unibo.dcs.commons.test.JUnitSpec
import it.unibo.dcs.service.room.Mocks.{postExecutionThread, roomRepository, threadExecutor}
import it.unibo.dcs.service.room.interactor.usecases.GetRoomParticipationsUseCase
import it.unibo.dcs.service.room.interactor.validations.GetRoomParticipationsValidation
import it.unibo.dcs.service.room.model.{Participation, Room}
import it.unibo.dcs.service.room.request.GetRoomParticipationsRequest
import it.unibo.dcs.service.room.validator.GetRoomParticipationsValidator
import org.scalamock.scalatest.MockFactory
import org.scalatest.OneInstancePerTest
import rx.lang.scala.{Observable, Subscriber}

final class GetRoomParticipationsUseCaseSpec extends JUnitSpec with MockFactory with OneInstancePerTest {

  private val validation = GetRoomParticipationsValidation(threadExecutor, postExecutionThread,
    GetRoomParticipationsValidator())
  private val getRoomParticipationsUseCase = GetRoomParticipationsUseCase(threadExecutor, postExecutionThread,
    roomRepository, validation)

  private val firstUsername = "nik"
  private val secondUsername = "mvandi"
  private val room = Room("Room 1")
  private val firstParticipation = Participation(room, firstUsername, new Date())
  private val secondParticipation = Participation(room, secondUsername, new Date())

  private val expectedResult = List(firstParticipation, secondParticipation)

  private val request = GetRoomParticipationsRequest(firstUsername)

  private val subscriber = stub[Subscriber[List[Participation]]]

  it should "Get all the participations for a given room" in {
    // Given
    (roomRepository getRoomParticipations _) expects request returns Observable.just(expectedResult)

    // When
    getRoomParticipationsUseCase(request).subscribe(subscriber)

    // Then
    subscriber.onNext _ verify expectedResult
  }

}
