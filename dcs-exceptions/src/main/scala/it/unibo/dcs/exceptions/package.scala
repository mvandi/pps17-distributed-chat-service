package it.unibo.dcs

import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.lang.scala.json.{Json, JsonArray, JsonObject}
import io.vertx.scala.ext.web.client.HttpResponse
import it.unibo.dcs.exceptions.ErrorType._
import it.unibo.dcs.exceptions.Implicits.jsonObjectToDcsException

import scala.language.implicitConversions

/** It contains all the exceptions used by the APIs */
package object exceptions {

  private val KEY_ERROR = "error"

  private val KEY_TYPE = "type"
  private val KEY_EXTRAS = "extras"

  private val KEY_SERVICE_NAME = "serviceName"
  private val KEY_DESCRIPTION = "description"
  private val KEY_NAME = "name"
  private val KEY_USERNAME = "username"

  object ErrorType {

    val Internal = "Internal"

    val ServiceUnavailable = "SERVICE_UNAVAILABLE"

    val UserAlreadyExists = "USER_ALREADY_EXISTS"

    val UserNotFound = "USER_NOT_FOUND"

    val RoomAlreadyExists = "ROOM_ALREADY_EXISTS"

    val RoomNotFound = "ROOM_NOT_FOUND"

    val WrongUsernameOrPassword = "WRONG_USERNAME_OR_PASSWORD"

    val InvalidToken = "INVALID_TOKEN"

    val UsernameRequired = "USERNAME_REQUIRED"

    val FirstNameRequired = "FIRST_NAME_REQUIRED"

    val LastNameRequired = "LAST_NAME_REQUIRED"

    val PasswordRequired = "PASSWORD_REQUIRED"

    val PasswordConfirmRequired = "PASSWORD_CONFIRM_REQUIRED"

    val TokenRequired = "TOKEN_REQUIRED"

    val RoomNameRequired = "ROOM_NAME_REQUIRED"

    val ParticipationNotFound = "PARTICIPATION_NOT_FOUND"

    val ParticipationsNotFound = "PARTICIPATIONS_NOT_FOUND"

    val ParticipationAlreadyExists = "PARTICIPATION_ALREADY_EXISTS"

    val MessageContentRequired = "MESSAGE_CONTENT_REQUIRED"
  }

  /** Sum type representing all the specific exceptions for Distributed Chat Service application */
  sealed abstract class DcsException(val errorType: String) extends RuntimeException

  final case class InternalException(description: String = "") extends DcsException(Internal)

  final case object KeyRequiredException {

    def apply(key: String, error: String): InternalException =
      InternalException(s"Key '$key' is required in $error error")

  }

  final case class AuthServiceErrorException(cause: Throwable) extends DcsException(Internal)

  final case class RoomServiceErrorException(cause: Throwable) extends DcsException(Internal)

  final case class UserServiceErrorException(cause: Throwable) extends DcsException(Internal)

  final case class ServiceUnavailableException(serviceName: String) extends DcsException(ServiceUnavailable)

  final case class UserAlreadyExistsException(username: String) extends DcsException(UserAlreadyExists)

  final case class UserNotFoundException(username: String) extends DcsException(UserNotFound)

  final case class RoomAlreadyExistsException(name: String) extends DcsException(RoomAlreadyExists)

  final case class RoomNotFoundException(name: String) extends DcsException(RoomNotFound)

  final case class ParticipationAlreadyExistsException(username: String, name: String) extends DcsException(ParticipationAlreadyExists)

  final case class ParticipationNotFoundException(username: String, name: String) extends DcsException(ParticipationNotFound)

  final case class ParticipationsNotFoundException(name: String) extends DcsException(ParticipationsNotFound)

  final case object InvalidTokenException extends DcsException(InvalidToken)

  final case object WrongUsernameOrPasswordException extends DcsException(WrongUsernameOrPassword)

  /* Field required */
  final case object UsernameRequiredException extends DcsException(UsernameRequired)

  final case object FirstNameRequiredException extends DcsException(FirstNameRequired)

  final case object LastNameRequiredException extends DcsException(LastNameRequired)

  final case object PasswordRequiredException extends DcsException(PasswordRequired)

  final case object PasswordConfirmRequiredException extends DcsException(PasswordRequired)

  final case object TokenRequiredException extends DcsException(TokenRequired)

  final case object RoomNameRequiredException extends DcsException(RoomNameRequired)

  final case object MessageContentRequiredExpection extends DcsException(MessageContentRequired)

  object Implicits {

    implicit def throwableToJsonObject(cause: Throwable): JsonObject = {
      val error = new JsonObject()
      cause match {
        case e: DcsException =>
          error.put(KEY_TYPE, e.errorType)
          e match {
            case ServiceUnavailableException(serviceName) =>
              error.put(KEY_EXTRAS, Json.obj((KEY_SERVICE_NAME, serviceName)))
            case UserAlreadyExistsException(username) =>
              error.put(KEY_EXTRAS, Json.obj((KEY_USERNAME, username)))
            case UserNotFoundException(username) =>
              error.put(KEY_EXTRAS, Json.obj((KEY_USERNAME, username)))
            case RoomAlreadyExistsException(name) =>
              error.put(KEY_EXTRAS, Json.obj((KEY_NAME, name)))
            case RoomNotFoundException(name) =>
              error.put(KEY_EXTRAS, Json.obj((KEY_NAME, name)))
            case InternalException(description) =>
              if (description != null && description.nonEmpty)
                error.put(KEY_EXTRAS, Json.obj((KEY_DESCRIPTION, description)))
            case _ =>
          }
        case _ =>
          error.put(KEY_TYPE, Internal)

          val description = cause.getMessage
          if (description != null && description.nonEmpty)
            error.put(KEY_EXTRAS, Json.obj((KEY_DESCRIPTION, description)))
      }
      Json.obj((KEY_ERROR, error))
    }

    implicit def jsonObjectToDcsException(json: JsonObject): JsonObject = {
      if (json.containsKey(KEY_ERROR)) {
        val error = json.getJsonObject(KEY_ERROR)
        if (error.containsKey(KEY_TYPE)) {
          error.getString(KEY_TYPE) match {
            case ServiceUnavailable =>
              handleServiceUnavailableExceptions(error)
            case UserNotFound =>
              handleUserNotFoundExceptions(error)
            case UserAlreadyExists =>
              handleUserAlreadyExistsExceptions(error)
            case RoomNotFound =>
              handleRoomNotFoundExceptions(error)
            case RoomAlreadyExists =>
              handleRoomAlreadyExistsExceptions(error)
            case ParticipationNotFound =>
              handleParticipationNotFoundExceptions(error)
            case ParticipationsNotFound =>
              handleParticipationsNotFoundExceptions(error)
            case ParticipationAlreadyExists =>
              handleParticipationAlreadyExistsExceptions(error)
            case WrongUsernameOrPassword =>
              throw WrongUsernameOrPasswordException
            case UsernameRequired =>
              throw UsernameRequiredException
            case FirstNameRequired =>
              throw FirstNameRequiredException
            case LastNameRequired =>
              throw LastNameRequiredException
            case PasswordRequired =>
              throw PasswordRequiredException
            case PasswordConfirmRequired =>
              throw PasswordConfirmRequiredException
            case TokenRequired =>
              throw TokenRequiredException
            case RoomNameRequired =>
              throw RoomNameRequiredException
            case MessageContentRequired =>
              throw MessageContentRequiredExpection
            case Internal =>
              handleInternalExceptions(error)
          }
          throw InternalException(s"Missing key '$KEY_TYPE' in DCS error")
        }
      }
      json
    }

    implicit def throwableToHttpResponseStatus(cause: Throwable): HttpResponseStatus = cause match {
      case ServiceUnavailableException(_) =>
        HttpResponseStatus.SERVICE_UNAVAILABLE
      case UserNotFoundException(_)
           | RoomNotFoundException(_)
           | ParticipationNotFoundException(_, _) =>
        HttpResponseStatus.NOT_FOUND
      case UserAlreadyExistsException(_)
           | RoomAlreadyExistsException(_)
           | ParticipationAlreadyExistsException(_, _) =>
        HttpResponseStatus.CONFLICT
      case UsernameRequiredException
           | FirstNameRequiredException
           | LastNameRequiredException
           | PasswordRequiredException
           | PasswordConfirmRequiredException
           | TokenRequiredException
           | RoomNameRequiredException =>
        HttpResponseStatus.PRECONDITION_FAILED
      // HttpResponseStatus.UNPROCESSABLE_ENTITY
      case InvalidTokenException
           | WrongUsernameOrPasswordException =>
        HttpResponseStatus.UNAUTHORIZED
      case _ => HttpResponseStatus.INTERNAL_SERVER_ERROR
    }
  }

  private def handleInternalExceptions(error: JsonObject) = {
    if (error.containsKey(KEY_EXTRAS)) {
      val extras = error.getJsonObject(KEY_EXTRAS)
      if (extras.containsKey(KEY_DESCRIPTION)) {
        val description = extras.getString(KEY_DESCRIPTION)
        throw InternalException(description)
      }
    }
    throw InternalException()
  }

  private def handleParticipationAlreadyExistsExceptions(error: JsonObject) = {
    if (error.containsKey(KEY_EXTRAS)) {
      val extras = error.getJsonObject(KEY_EXTRAS)
      if (extras.containsKey(KEY_NAME)) {
        val name = extras.getString(KEY_NAME)
        if (extras.containsKey(KEY_USERNAME)) {
          val username = extras.getString(KEY_USERNAME)
          throw ParticipationAlreadyExistsException(username, name)
        }
        throw KeyRequiredException(KEY_USERNAME, ParticipationAlreadyExists)
      }
      throw KeyRequiredException(KEY_NAME, ParticipationAlreadyExists)
    }
    throw KeyRequiredException(KEY_EXTRAS, ParticipationAlreadyExists)
  }

  private def handleParticipationsNotFoundExceptions(error: JsonObject) = {
    if (error.containsKey(KEY_EXTRAS)) {
      val extras = error.getJsonObject(KEY_EXTRAS)
      if (extras.containsKey(KEY_NAME)) {
        val name = extras.getString(KEY_NAME)
        if (extras.containsKey(KEY_USERNAME)) {
          val username = extras.getString(KEY_USERNAME)
          throw ParticipationNotFoundException(username, name)
        }
        throw KeyRequiredException(KEY_USERNAME, ParticipationsNotFound)
      }
      throw KeyRequiredException(KEY_NAME, ParticipationsNotFound)
    }
    throw KeyRequiredException(KEY_EXTRAS, ParticipationsNotFound)
  }

  private def handleParticipationNotFoundExceptions(error: JsonObject) = {
    if (error.containsKey(KEY_EXTRAS)) {
      val extras = error.getJsonObject(KEY_EXTRAS)
      if (extras.containsKey(KEY_NAME)) {
        val name = extras.getString(KEY_NAME)
        if (extras.containsKey(KEY_USERNAME)) {
          val username = extras.getString(KEY_USERNAME)
          throw ParticipationNotFoundException(username, name)
        }
        throw KeyRequiredException(KEY_USERNAME, ParticipationNotFound)
      }
      throw KeyRequiredException(KEY_NAME, ParticipationNotFound)
    }
    throw KeyRequiredException(KEY_EXTRAS, ParticipationNotFound)
  }

  private def handleRoomAlreadyExistsExceptions(error: JsonObject) = {
    if (error.containsKey(KEY_EXTRAS)) {
      val extras = error.getJsonObject(KEY_EXTRAS)
      if (extras.containsKey(KEY_NAME)) {
        val name = extras.getString(KEY_NAME)
        throw RoomAlreadyExistsException(name)
      }
      throw KeyRequiredException(KEY_NAME, RoomAlreadyExists)
    }
    throw KeyRequiredException(KEY_EXTRAS, RoomAlreadyExists)
  }

  private def handleRoomNotFoundExceptions(error: JsonObject) = {
    if (error.containsKey(KEY_EXTRAS)) {
      val extras = error.getJsonObject(KEY_EXTRAS)
      if (extras.containsKey(KEY_NAME)) {
        val name = extras.getString(KEY_NAME)
        throw RoomNotFoundException(name)
      }
      throw KeyRequiredException(KEY_NAME, RoomNotFound)
    }
    throw KeyRequiredException(KEY_EXTRAS, RoomNotFound)
  }

  private def handleUserAlreadyExistsExceptions(error: JsonObject) = {
    if (error.containsKey(KEY_EXTRAS)) {
      val extras = error.getJsonObject(KEY_EXTRAS)
      if (extras.containsKey(KEY_USERNAME)) {
        val username = extras.getString(KEY_USERNAME)
        throw UserAlreadyExistsException(username)
      }
      throw KeyRequiredException(KEY_USERNAME, UserAlreadyExists)
    }
    throw KeyRequiredException(KEY_NAME, UserAlreadyExists)
  }

  private def handleUserNotFoundExceptions(error: JsonObject) = {
    if (error.containsKey(KEY_EXTRAS)) {
      val extras = error.getJsonObject(KEY_EXTRAS)
      if (extras.containsKey(KEY_USERNAME)) {
        val username = extras.getString(KEY_USERNAME)
        throw UserNotFoundException(username)
      }
      throw InternalException(s"Missing key '$KEY_USERNAME' in $UserNotFound error")
    }
    throw KeyRequiredException(KEY_NAME, UserNotFound)
  }

  private def handleServiceUnavailableExceptions(error: JsonObject) = {
    if (error.containsKey(KEY_EXTRAS)) {
      val extras = error.getJsonObject(KEY_EXTRAS)
      if (extras.containsKey(KEY_SERVICE_NAME)) {
        val serviceName = extras.getString(KEY_SERVICE_NAME)
        throw ServiceUnavailableException(serviceName)
      }
      throw KeyRequiredException(KEY_SERVICE_NAME, ServiceUnavailable)
    }
    throw KeyRequiredException(KEY_USERNAME, ServiceUnavailable)
  }

  def bodyAsJsonObject[T](default: => JsonObject = Json.emptyObj()): HttpResponse[T] => JsonObject = response =>
    jsonObjectToDcsException(response.bodyAsJsonObject().getOrElse(default))

  def bodyAsJsonArray[T](default: => JsonArray = Json.emptyArr()): HttpResponse[T] => JsonArray = response => {
    if (isJsonArray(response)) {
      response.bodyAsJsonArray().getOrElse(default)
    }
    else {
      jsonObjectToDcsException(response.bodyAsJsonObject().head)
      default
    }
  }

  private def isJsonArray[T](response: HttpResponse[T]): Boolean = {
    response.bodyAsString().fold(false) { s =>
      if (s.startsWith("[")) true else false
    }
  }

}
