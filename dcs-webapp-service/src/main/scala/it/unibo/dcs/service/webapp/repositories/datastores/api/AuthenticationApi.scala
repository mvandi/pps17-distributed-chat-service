package it.unibo.dcs.service.webapp.repositories.datastores.api

import io.vertx.core.Vertx
import io.vertx.scala.core.eventbus.EventBus
import io.vertx.servicediscovery.ServiceDiscovery
import it.unibo.dcs.commons.service.HttpEndpointDiscoveryImpl
import it.unibo.dcs.service.webapp.repositories.Requests.{LoginUserRequest, RegisterUserRequest}
import it.unibo.dcs.service.webapp.repositories.datastores.api.impl.AuthenticationRestApi
import rx.lang.scala.Observable

trait AuthenticationApi {
  def loginUser(loginUserRequest: LoginUserRequest): Observable[String]

  def registerUser(request: RegisterUserRequest): Observable[String]

  def logoutUser(username: String): Observable[Unit]
}

/* Companion object */
object AuthenticationApi {

  /* Factory method */
  def authRestApi(vertx: Vertx, eventBus: EventBus): AuthenticationApi = new AuthenticationRestApi(
    new HttpEndpointDiscoveryImpl(ServiceDiscovery.create(vertx), eventBus))
}
