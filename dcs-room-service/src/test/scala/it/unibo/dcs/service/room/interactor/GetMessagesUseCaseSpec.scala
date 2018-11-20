package it.unibo.dcs.service.room.interactor

import java.util.Date

import it.unibo.dcs.commons.test.JUnitSpec
import it.unibo.dcs.service.room.Mocks.{postExecutionThread, roomRepository, threadExecutor}
import it.unibo.dcs.service.room.interactor.usecases.GetMessagesUseCase
import it.unibo.dcs.service.room.interactor.validations.GetMessagesValidation
import it.unibo.dcs.service.room.model.{Message, Room}
import it.unibo.dcs.service.room.request.GetMessagesRequest
import it.unibo.dcs.service.room.validator.GetMessagesValidator
import org.scalamock.scalatest.MockFactory
import org.scalatest.OneInstancePerTest
import rx.lang.scala.{Observable, Subscriber}

class GetMessagesUseCaseSpec extends JUnitSpec with MockFactory with OneInstancePerTest {
  val validation = GetMessagesValidation(threadExecutor, postExecutionThread, GetMessagesValidator())
  private val getMessagesUseCase = GetMessagesUseCase(threadExecutor, postExecutionThread, roomRepository, validation)

  private val roomName = "Test room"
  private val username = "martynha"

  val timestamp = new Date
  val content = "Contenuto del messaggio"
  val message = Message(Room(roomName), username, content, timestamp)
  private val expectedResult = List(message, message, message)

  val request = GetMessagesRequest(roomName)

  val subscriber: Subscriber[List[Message]] = stub[Subscriber[List[Message]]]

  it should "Get all the messages for a given room" in {
    //Given
    (roomRepository getMessages _) expects request returns Observable.just(expectedResult)

    //When
    getMessagesUseCase(request).subscribe(subscriber)

    //Then
    subscriber.onNext _ verify expectedResult once()
    (() => subscriber onCompleted) verify() once()
  }

}
