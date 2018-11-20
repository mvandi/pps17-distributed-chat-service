package it.unibo.dcs.service.webapp.usecases

import it.unibo.dcs.service.webapp.interaction.Requests.GetMessagesRequest
import it.unibo.dcs.service.webapp.interaction.Results.GetMessagesResult
import it.unibo.dcs.service.webapp.usecases.commons.Mocks.{authRepository, postExecutionThread, roomRepository, threadExecutor}
import it.unibo.dcs.service.webapp.usecases.commons.UseCaseSpec
import rx.lang.scala.{Observable, Subscriber}

import scala.language.postfixOps

class GetMessagesUseCaseSpec extends UseCaseSpec {

  private val getMessagesRequest = GetMessagesRequest(user.username, roomName, token)

  private val getMessagesResult = GetMessagesResult(messages)

  private val getMessagesSubscriber: Subscriber[GetMessagesResult] = stub[Subscriber[GetMessagesResult]]

  private val getMessagesUseCase =
    new GetMessagesUseCase(threadExecutor, postExecutionThread, authRepository, roomRepository)

  it should "return all rooms when the use case is executed" in {
    // Given
    (authRepository checkToken _) expects checkTokenRequest returns (Observable just token)

    (roomRepository getMessages _) expects getMessagesRequest returns (Observable just messages)

    // When
    // getMessagesUseCase is executed with argument `request`
    getMessagesUseCase(getMessagesRequest) subscribe getMessagesSubscriber

    // Then
    (getMessagesSubscriber onNext _) verify getMessagesResult once()
    (() => getMessagesSubscriber onCompleted) verify() once()
  }

}
