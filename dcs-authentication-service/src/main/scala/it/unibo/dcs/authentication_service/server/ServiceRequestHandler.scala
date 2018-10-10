package it.unibo.dcs.authentication_service.server

import io.vertx.scala.ext.web.RoutingContext

trait ServiceRequestHandler {

  def handleRegistration(implicit context: RoutingContext): Unit

  def handleLogin(implicit context: RoutingContext): Unit

  def handleLogout(implicit context: RoutingContext): Unit
}
