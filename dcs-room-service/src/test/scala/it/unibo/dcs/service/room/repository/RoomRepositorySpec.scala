package it.unibo.dcs.service.room.repository

import java.util.Date

import it.unibo.dcs.commons.test.JUnitSpec
import it.unibo.dcs.service.room.Mocks._
import it.unibo.dcs.service.room.model.{Message, Participation, Room}
import it.unibo.dcs.service.room.repository.impl.RoomRepositoryImpl
import it.unibo.dcs.service.room.request._
import org.scalamock.scalatest.MockFactory
import org.scalatest.OneInstancePerTest
import rx.lang.scala.{Observable, Subscriber}

final class RoomRepositorySpec extends JUnitSpec with MockFactory with OneInstancePerTest {

  private val roomRepository = new RoomRepositoryImpl(roomDataStore)

  private val username = "mvandi"
  private val secondUsername = "mvandi"
  private val roomName = "Test room"
  private val room = Room(roomName)
  private val rooms = List(Room("Room 01"), Room("Room 02"))
  val expectedParticipation = Participation(room, username, new Date())
  private val firstParticipation = Participation(rooms.head, username, new Date())
  private val secondParticipation = Participation(rooms.head, secondUsername , new Date())
  private val participations = List(firstParticipation, secondParticipation)

  val timestamp = new Date
  val content = "Contenuto del messaggio"
  val message = Message(Room(roomName), username, content, timestamp)
  val messages = List(message, message, message)

  it should "get all the participations from the data store for a given room" in {
    val request = GetRoomParticipationsRequest(username)
    val subscriber = stub[Subscriber[List[Participation]]]

    //Given
    (roomDataStore getRoomParticipations _) expects request returns Observable.just(participations)

    roomRepository.getRoomParticipations(request).subscribe(subscriber)

    //Then
    subscriber.onNext _ verify participations once()
  }

  it should "create a new user on the data store" in {
    val request = CreateUserRequest(username)

    val subscriber = stub[Subscriber[Unit]]

    // Given
    (roomDataStore createUser _) expects request returns Observable.just()

    // When
    roomRepository.createUser(request).subscribe(subscriber)

    // Then
    (() => subscriber.onCompleted) verify() once()
  }

  it should "Create a new room on the data store" in {
    val request = CreateRoomRequest(roomName, username)

    val subscriber = stub[Subscriber[Room]]

    //Given
    (roomDataStore createRoom _) expects request returns Observable.just()

    roomRepository.createRoom(request).subscribe(subscriber)

    //Then
    (() => subscriber.onCompleted()) verify() once()
  }

  it should "Delete a room if the user who is trying to delete the room is also the user who created the room" in {
    val request = DeleteRoomRequest(roomName, username)

    val subscriber = stub[Subscriber[String]]

    // Given
    (roomDataStore deleteRoom _) expects request returns Observable.just(request.name)

    // When
    roomRepository.deleteRoom(request).subscribe(subscriber)

    // Then
    subscriber.onNext _ verify request.name once()
    (() => subscriber.onCompleted()) verify() once()
  }

  it should "Get all the rooms on the data store" in {
    val request = GetRoomsRequest(username)

    val subscriber = stub[Subscriber[List[Room]]]

    //Given
    (roomDataStore getRooms _) expects request returns Observable.just(rooms)

    roomRepository.getRooms(request).subscribe(subscriber)

    //Then
    subscriber.onNext _ verify rooms once()
  }

  it should "Send a message to room on the data store" in {
    val request = SendMessageRequest(roomName, username, content , timestamp)
    val expectedResponse = message

    val subscriber = stub[Subscriber[Message]]

    //Given
    (roomDataStore sendMessage _) expects request returns Observable.just(expectedResponse)

    //When
    roomRepository.sendMessage(request).subscribe(subscriber)

    //Then
    subscriber.onNext _ verify expectedResponse once()
    (() => subscriber.onCompleted()) verify() once()
  }
  
  it should "Let the user join the selected room on the data store" in {
    val request = JoinRoomRequest(roomName, username)

    val subscriber = stub[Subscriber[Participation]]

    //Given
    (roomDataStore joinRoom  _) expects request returns Observable.just(expectedParticipation)

    roomRepository.joinRoom(request).subscribe(subscriber)

    //Then
    subscriber.onNext _ verify expectedParticipation once()
  }

  it should "Let the user leave the selected room on the data store" in {
    val request = LeaveRoomRequest(roomName, username)

    val subscriber = stub[Subscriber[Participation]]

    //Given
    (roomDataStore leaveRoom  _) expects request returns Observable.just(expectedParticipation)

    roomRepository.leaveRoom(request).subscribe(subscriber)

    //Then
    subscriber.onNext _ verify expectedParticipation once()
  }
  
  it should "Get all the participations for a given user" in {
    val request = GetUserParticipationsRequest(username)

    val subscriber = stub[Subscriber[List[Room]]]

    (roomDataStore getParticipationsByUsername _) expects request returns Observable.just(rooms)

    roomRepository.getParticipationsByUsername(request).subscribe(subscriber)

    subscriber.onNext _ verify rooms once()
    (() => subscriber onCompleted) verify() once()
  }

  it should "Get all the messages for a given room" in {
    //Given
    val request = GetMessagesRequest(roomName)
    val subscriber = stub[Subscriber[List[Message]]]

    (roomDataStore getMessages _) expects request returns Observable.just(messages)

    //When
    roomRepository.getMessages(request).subscribe(subscriber)

    //Then
    subscriber.onNext _ verify messages once()
    (() => subscriber onCompleted) verify() once()
  }

}
