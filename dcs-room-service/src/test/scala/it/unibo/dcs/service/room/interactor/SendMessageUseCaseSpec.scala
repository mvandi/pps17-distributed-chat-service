package it.unibo.dcs.service.room.interactor

import java.util.Date

import it.unibo.dcs.commons.test.JUnitSpec
import it.unibo.dcs.service.room.Mocks.{postExecutionThread, roomRepository, threadExecutor}
import it.unibo.dcs.service.room.interactor.usecases.SendMessageUseCase
import it.unibo.dcs.service.room.interactor.validations.SendMessageValidation
import it.unibo.dcs.service.room.model.{Message, Room}
import it.unibo.dcs.service.room.request.SendMessageRequest
import it.unibo.dcs.service.room.validator.SendMessageValidator
import org.scalamock.scalatest.MockFactory
import org.scalatest.OneInstancePerTest
import rx.lang.scala.{Observable, Subscriber}

class SendMessageUseCaseSpec extends JUnitSpec with MockFactory with OneInstancePerTest {

  private val username = "martynha"
  private val roomName = "Test room"
  private val content = "Contenuto del messaggio"
  private val timestamp = new Date

  private val sendMessageUseCase = {
    val validation = SendMessageValidation(threadExecutor, postExecutionThread, SendMessageValidator())
    SendMessageUseCase(threadExecutor, postExecutionThread, roomRepository, validation)
  }

  private val request = SendMessageRequest(roomName, username, content, timestamp)
  private val expectedResponse = Message(Room(roomName), username, content, timestamp)

  private val subscriber = stub[Subscriber[Message]]

  it should "Create a room when the CreateRoomUseCase is execute" in {
    //Given
    (roomRepository sendMessage _) expects request returns (Observable just expectedResponse)

    //When
    sendMessageUseCase(request).subscribe(subscriber)

    //Then
    (subscriber onNext _) verify expectedResponse once()
    (() => subscriber onCompleted) verify() once()
  }


}
