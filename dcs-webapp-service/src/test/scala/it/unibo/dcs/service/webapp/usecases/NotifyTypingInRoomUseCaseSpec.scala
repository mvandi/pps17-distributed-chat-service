package it.unibo.dcs.service.webapp.usecases

import it.unibo.dcs.service.webapp.interaction.Requests.{CheckTokenRequest, NotifyTypingUserRequest}
import it.unibo.dcs.service.webapp.interaction.Results.NotifyTypingUserResult
import it.unibo.dcs.service.webapp.usecases.commons.Mocks.{authRepository, postExecutionThread, threadExecutor}
import it.unibo.dcs.service.webapp.usecases.commons.UseCaseSpec
import rx.lang.scala.{Observable, Subscriber}

import scala.language.postfixOps

class NotifyTypingInRoomUseCaseSpec extends UseCaseSpec {

  private val request = NotifyTypingUserRequest("ale", "room1", "token")
  private val expectedResult = NotifyTypingUserResult(request.username)

  private val typingUserSubscriber = stub[Subscriber[NotifyTypingUserResult]]

  private val typingUserUseCase = new NotifyTypingInRoomUseCase(threadExecutor, postExecutionThread, authRepository)

  it should "check the token validity when it notifies the users in the room about a user's typing status" in {
    // Given
    (authRepository checkToken _) expects CheckTokenRequest(token, request.username) returns (Observable just Unit)

    // When
    typingUserUseCase(request) subscribe typingUserSubscriber

    // Then
    (typingUserSubscriber onNext _) verify expectedResult once()
    (() => typingUserSubscriber onCompleted) verify() once()
  }
}
