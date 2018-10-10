package it.unibo.dcs.service.webapp.repositories.datastores.api.impl

import io.vertx.scala.ext.web.client.WebClient
import it.unibo.dcs.commons.service.{AbstractApi, HttpEndpointDiscovery}
import it.unibo.dcs.service.webapp.model.User
import it.unibo.dcs.service.webapp.repositories.Requests.Implicits._
import it.unibo.dcs.service.webapp.repositories.Requests.RegisterUserRequest
import it.unibo.dcs.service.webapp.repositories.datastores.api.UserApi
import it.unibo.dcs.service.webapp.repositories.datastores.api.exceptions.{GetUserResponseException, UserCreationResponseException}
import rx.lang.scala.Observable

import scala.concurrent.ExecutionContext.Implicits.global


class UserRestApi(private[this] val discovery: HttpEndpointDiscovery)
  extends AbstractApi(discovery, "UserService") with UserApi {

  override def createUser(registrationRequest: RegisterUserRequest): Observable[User] = {
    request((userWebClient: WebClient) =>
      Observable.from(userWebClient.post("/register").sendJsonObjectFuture(registrationRequest)))
      .map(response => response.bodyAsJsonObject()
        .getOrElse(throw UserCreationResponseException())
      )
  }

  override def getUserByUsername(username: String): Observable[User] =
    request((userWebClient: WebClient) =>
      Observable.from(userWebClient.post("/user/" + username).sendJsonObjectFuture(username)))
      .map(response => response.bodyAsJsonObject()
        .getOrElse(throw GetUserResponseException())
      )
}
