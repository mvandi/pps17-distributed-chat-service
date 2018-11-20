package it.unibo.dcs.service.webapp.usecases

import it.unibo.dcs.service.webapp.interaction.Requests.SendMessageRequest
import it.unibo.dcs.service.webapp.interaction.Results.SendMessageResult
import it.unibo.dcs.service.webapp.usecases.commons.Mocks._
import it.unibo.dcs.service.webapp.usecases.commons.UseCaseSpec
import rx.lang.scala.{Observable, Subscriber}

import scala.language.postfixOps

class SendMessageUseCaseSpec extends UseCaseSpec {
  private val sendMessageRequest = SendMessageRequest(room.name, user.username, content, timestamp, token)

  private val sendMessageResult = SendMessageResult(message)
  private val sendMessageSubscriber = stub[Subscriber[SendMessageResult]]

  private val sendMessageUseCase =
    new SendMessageUseCase(threadExecutor, postExecutionThread, authRepository, roomRepository)

  it should "execute the send message use case" in {
    // Given
    (authRepository checkToken _) expects checkTokenRequest returns (Observable just token)
    (roomRepository sendMessage _) expects sendMessageRequest returns (Observable just message)

    // When
    // sendMessageUseCase is executed with argument `request`
    sendMessageUseCase(sendMessageRequest) subscribe sendMessageSubscriber

    // Then
    (sendMessageSubscriber onNext _) verify sendMessageResult once()
    (() => sendMessageSubscriber onCompleted) verify() once()
  }
}
