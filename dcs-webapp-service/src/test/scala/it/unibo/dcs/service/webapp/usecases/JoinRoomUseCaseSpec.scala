package it.unibo.dcs.service.webapp.usecases

import it.unibo.dcs.service.webapp.interaction.Requests.{CheckTokenRequest, RoomJoinRequest}
import it.unibo.dcs.service.webapp.interaction.Results.RoomJoinResult
import it.unibo.dcs.service.webapp.usecases.commons.Mocks._
import it.unibo.dcs.service.webapp.usecases.commons.UseCaseSpec
import rx.lang.scala.{Observable, Subscriber}

import scala.language.postfixOps

class JoinRoomUseCaseSpec extends UseCaseSpec {

  private val joinRoomRequest = RoomJoinRequest(room.name, user.username, token)

  private val joinRoomSubscriber = stub[Subscriber[RoomJoinResult]]
  private val joinRoomResult = RoomJoinResult(participation)

  private val joinRoomUseCase =
    new JoinRoomUseCase(threadExecutor, postExecutionThread, authRepository, roomRepository)

  it should "execute the room join use case" in {
    // Given
    (authRepository checkToken _) expects CheckTokenRequest(token, user.username) returns (Observable just Unit) once()
    (roomRepository joinRoom _) expects joinRoomRequest returns (Observable just participation) once()

    // When
    // createUserUseCase is executed with argument `request`
    joinRoomUseCase(joinRoomRequest) subscribe joinRoomSubscriber

    // Then
    (joinRoomSubscriber onNext _) verify joinRoomResult once()
    (() => joinRoomSubscriber onCompleted) verify() once()
  }
}
