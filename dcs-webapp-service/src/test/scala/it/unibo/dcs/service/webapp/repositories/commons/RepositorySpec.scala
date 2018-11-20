package it.unibo.dcs.service.webapp.repositories.commons

import java.util.Date

import it.unibo.dcs.commons.test.JUnitSpec
import it.unibo.dcs.service.webapp.interaction.Requests.{EditUserRequest, RegisterUserRequest}
import it.unibo.dcs.service.webapp.model.{Message, Participation, Room, User}
import org.scalamock.scalatest.MockFactory
import org.scalatest.OneInstancePerTest

/**
  * Abstract class that encapsulates the objects used by more than one Repository test.
  */
abstract class RepositorySpec extends JUnitSpec with MockFactory with OneInstancePerTest {

  /** ----------------- Model instances ---------------------------------------------------- */
  /** Users */
  protected val user = User("niklegend", "nicola", "piscaglia", "bla", visible = true, new Date())

  /** Tokens */
  protected val token = "token"

  /** Rooms */
  protected val room = Room("Room 1")
  protected val rooms: List[Room] = List(room, room, room)

  /** Participations */
  protected val participation = Participation(room, user.username, new Date())
  protected val participations = Set(participation)

  /** Messages */
  protected val messageContent = "Message content"
  protected val messageTimestamp = new Date
  protected val message = Message(room, user.username, messageContent, messageTimestamp)
  protected val messages = List(message, message, message)

  /** ----------------- Requests ------------------------------------------------------------ */
  protected val registerRequest = RegisterUserRequest(user.username, user.firstName,
    user.lastName, "password", "password")

  protected val editUserRequest =
    EditUserRequest(user.username, user.firstName, user.lastName, user.bio, user.visible, token)
}
