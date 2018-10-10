package it.unibo.dcs.service.user

import io.vertx.lang.scala.json.JsonObject
import it.unibo.dcs.service.user.model.User

import it.unibo.dcs.commons.dataaccess.Implicits._

package object request {
  final case class CreateUserRequest(username: String, firstName: String, lastName: String)

  final case class GetUserRequest(username: String) extends AnyVal

    object Implicits {
      implicit def jsonObjectToUser(userJsonObject: JsonObject): User = {
        User(userJsonObject.getString("username"), userJsonObject.getString("first_name"),
          userJsonObject.getString("last_name"), userJsonObject.getString("bio"),
          if (userJsonObject.getString("visible") == "0") false else true, userJsonObject.getString("last_seen"))
      }

      implicit def userToJsonObject(user: User): JsonObject = {
        new JsonObject().put("username", user.username).put("first_name", user.firstName)
          .put("last_name", user.lastName).put("bio", user.bio).put("visible", user.visible)
          .put("last_seen", dateToString(user.lastSeen))
      }
    }

}
