package it.unibo.dcs.service.webapp.repositories

import java.util.Date

import io.vertx.lang.scala.json.{Json, JsonObject}
import it.unibo.dcs.service.webapp.model.User

import scala.language.implicitConversions

object Requests {

  final case class LoginUserRequest(username: String, password: String)

  final case class RegisterUserRequest(username: String, password: String,
                                       firstName: String, lastName: String)

  object Implicits {

    implicit def registerRequestToJson(registerRequest: RegisterUserRequest): JsonObject = {
      Json.obj(("username", registerRequest.firstName), ("lastname", registerRequest.lastName),
        ("password", registerRequest.password), ("username", registerRequest.username))
    }

    implicit def JsonToRegisterRequest(json: JsonObject): RegisterUserRequest = {
      RegisterUserRequest(json.getString("username"), json.getString("password"),
        json.getString("firstname"), json.getString("lastname"))
    }

    implicit def usernameToJson(username: String): JsonObject = {
      Json.obj(("username", username))
    }

    implicit def jsonToUsername(json: JsonObject): String = {
      json.getString("username")
    }

    implicit def loginRequestToJson(loginUserRequest: LoginUserRequest): JsonObject = {
      Json.obj(("username", loginUserRequest.username), ("password", loginUserRequest.password))
    }

    implicit def jsonToLoginRequest(json: JsonObject): LoginUserRequest = {
      LoginUserRequest(json.getString("username"), json.getString("password"))
    }

    implicit def jsonToUser(userJson: JsonObject): User = {
      User(userJson.getString("username"), userJson.getString("firstname"),
        userJson.getString("lastname"), "", visible = true, new Date)
    }
  }

}
