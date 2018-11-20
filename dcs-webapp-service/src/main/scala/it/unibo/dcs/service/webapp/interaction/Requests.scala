package it.unibo.dcs.service.webapp.interaction

import java.util.Date

import com.google.gson.Gson
import io.vertx.lang.scala.json.{Json, JsonArray, JsonObject}
import it.unibo.dcs.commons.JsonHelper.Implicits.RichGson
import it.unibo.dcs.service.webapp.interaction.Labels.JsonLabels._
import it.unibo.dcs.service.webapp.model.{Participation, Room, User}

import scala.language.{implicitConversions, postfixOps}

/** It wraps all requests used by request handler, use cases, it.unibo.dcs.service.webapp.repositories,
  * datastores and APIs */
object Requests {

  /** Sum type representing all the specific requests for Distributed Chat Service application */
  sealed trait DcsRequest

  final case class LoginUserRequest(username: String, password: String) extends DcsRequest

  final case class DeleteUserRequest(username: String, token: String) extends DcsRequest

  final case class RegisterUserRequest(username: String, firstName: String,
                                       lastName: String, password: String,
                                       passwordConfirm: String) extends DcsRequest

  final case class EditUserRequest(username: String, firstName: String, lastName: String, bio: String,
                                   visible: Boolean, token: String) extends DcsRequest

  final case class LogoutUserRequest(username: String, token: String) extends DcsRequest

  final case class CreateRoomRequest(name: String, username: String, token: String) extends DcsRequest

  final case class DeleteRoomRequest(name: String, username: String, token: String) extends DcsRequest

  final case class RoomJoinRequest(name: String, username: String, token: String) extends DcsRequest

  final case class RoomLeaveRequest(name: String, username: String, token: String) extends DcsRequest

  final case class GetRoomsRequest(username: String, token: String) extends DcsRequest

  final case class GetRoomParticipationsRequest(name: String, username: String, token: String) extends DcsRequest

  final case class CheckTokenRequest(token: String, username: String) extends DcsRequest

  final case class SendMessageRequest(name: String, username: String, content: String, timestamp: Date, token: String) extends DcsRequest

  final case class GetUserParticipationsRequest(username: String, token: String) extends DcsRequest

  final case class NotifyTypingUserRequest(name: String, username: String, token: String) extends DcsRequest
  
  final case class GetUserRequest(username: String) extends DcsRequest

  final case class GetMessagesRequest(username: String, name: String, token: String) extends DcsRequest

  /** It enables implicit conversions in order to clean code that deals with requests. */
  object Implicits {

    private val gson = new Gson()

    implicit def requestToJsonObject(request: DcsRequest): JsonObject = Json.fromObjectString(gson.toJson(request))

    implicit def jsonToNotifyWritingUserRequest(json: JsonObject): NotifyTypingUserRequest = {
      NotifyTypingUserRequest(json.getString(roomNameLabel), json.getString(usernameLabel),
        json.getString(tokenLabel))
    }

    implicit def jsonToDeleteRoomRequest(json: JsonObject): DeleteRoomRequest =
      gson fromJsonObject[DeleteRoomRequest] json

    implicit def jsonToCheckTokenRequest(json: JsonObject): CheckTokenRequest =
      gson fromJsonObject[CheckTokenRequest] json

    implicit def jsonObjectToRegisterUserRequest(json: JsonObject): RegisterUserRequest =
      gson fromJsonObject[RegisterUserRequest] json

    implicit def jsonObjectToEditUserRequest(json: JsonObject): EditUserRequest =
      gson fromJsonObject[EditUserRequest] json

    implicit def jsonObjectToUsername(json: JsonObject): String =
      json.getString(usernameLabel)

    implicit def jsonObjectToLoginUserRequest(json: JsonObject): LoginUserRequest =
      gson fromJsonObject[LoginUserRequest] json

    implicit def jsonObjectToLogoutUserRequest(json: JsonObject): LogoutUserRequest =
      gson fromJsonObject[LogoutUserRequest] json

    implicit def jsonObjectToUser(json: JsonObject): User =
      gson fromJsonObject[User] json

    implicit def jsonObjectToRoom(json: JsonObject): Room =
      gson fromJsonObject[Room] json

    implicit def jsonArrayToParticipationSet(array: JsonArray): Set[Participation] =
      (gson fromJsonArray[Participation] array) toSet

    implicit def jsonObjectToCreateRoomRequest(json: JsonObject): CreateRoomRequest =
      gson fromJsonObject[CreateRoomRequest] json

    implicit def jsonObjectToRoomJoinRequest(json: JsonObject): RoomJoinRequest =
      gson fromJsonObject[RoomJoinRequest] json

    implicit def jsonObjectToRoomLeaveRequest(json: JsonObject): RoomLeaveRequest =
      gson fromJsonObject[RoomLeaveRequest] json

    implicit def jsonObjectToGetRoomsRequest(json: JsonObject): GetRoomsRequest =
      gson fromJsonObject[GetRoomsRequest] json

    implicit def jsonObjectToSendMessageRequest(json: JsonObject): SendMessageRequest =
      gson fromJsonObject[SendMessageRequest] json

    implicit def jsonObjectToGetUserParticipationsRequest(json: JsonObject): GetUserParticipationsRequest =
      gson fromJsonObject[GetUserParticipationsRequest] json


    implicit def jsonObjectToGetMessagesRequest(json: JsonObject): GetMessagesRequest =
      gson fromJsonObject[GetMessagesRequest] json


    implicit def jsonObjectToGetUserRequest(json: JsonObject): GetUserRequest =
      gson fromJsonObject[GetUserRequest] json

  }

}
