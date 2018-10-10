package it.unibo.dcs.service.room.interactor.repository

import it.unibo.dcs.service.room.data.RoomDataStore
import it.unibo.dcs.service.room.repository.impl.RoomRepositoryImpl
import it.unibo.dcs.service.room.request.CreateUserRequest
import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, OneInstancePerTest}
import rx.lang.scala.{Observable, Subscriber}

final class RoomRepositorySpec extends FlatSpec with MockFactory with OneInstancePerTest {

  val roomDataStore = mock[RoomDataStore]
  val roomRepository = new RoomRepositoryImpl(roomDataStore)

  val subscriber = stub[Subscriber[Unit]]

  val request = CreateUserRequest("mvandi")

  it should "create a new user on the data store" in {
    // Given
    (roomDataStore createUser _) expects request returns Observable.just()

    // When
    roomRepository.createUser(request).subscribe(subscriber)

    // Then
    (() => subscriber.onCompleted) verify() once()
  }

}
