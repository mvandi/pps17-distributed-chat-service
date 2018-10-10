package it.unibo.dcs.service.webapp.verticles.requesthandler

import io.vertx.core.Vertx
import io.vertx.scala.core.Context
import io.vertx.scala.core.eventbus.EventBus
import io.vertx.scala.ext.web.RoutingContext
import it.unibo.dcs.service.webapp.repositories.datastores.AuthenticationDataStore.authDataStoreNetwork
import it.unibo.dcs.service.webapp.repositories.datastores.UserDataStore.userDataStoreNetwork
import it.unibo.dcs.service.webapp.repositories.datastores.api.AuthenticationApi.authRestApi
import it.unibo.dcs.service.webapp.repositories.datastores.api.UserApi.userRestApi
import it.unibo.dcs.service.webapp.repositories.{AuthenticationRepository, UserRepository}
import it.unibo.dcs.service.webapp.verticles.requesthandler.impl.ServiceRequestHandlerImpl

trait ServiceRequestHandler {

  def handleLogin(context: RoutingContext)(implicit ctx: Context): Unit

  def handleRegistration(context: RoutingContext)(implicit ctx: Context): Unit

  def handleLogout(context: RoutingContext)(implicit ctx: Context): Unit

}

/* Companion object */
object ServiceRequestHandler {
  /* Factory method */
  def apply(vertx: Vertx, eventBus: EventBus): ServiceRequestHandler = {
    val userApi = userRestApi(vertx, eventBus)
    val authApi = authRestApi(vertx, eventBus)
    val userDataStore = userDataStoreNetwork(userApi)
    val authDataStore = authDataStoreNetwork(authApi)
    val userRepository = UserRepository(userDataStore)
    val authRepository = AuthenticationRepository(authDataStore)
    new ServiceRequestHandlerImpl(userRepository, authRepository)
  }
}