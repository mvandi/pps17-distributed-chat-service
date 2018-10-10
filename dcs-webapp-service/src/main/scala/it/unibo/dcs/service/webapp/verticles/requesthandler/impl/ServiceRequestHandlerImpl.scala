package it.unibo.dcs.service.webapp.verticles.requesthandler.impl

import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.scala.core.Context
import io.vertx.scala.ext.web.RoutingContext
import it.unibo.dcs.service.webapp.repositories.Requests.Implicits._
import it.unibo.dcs.service.webapp.repositories.{AuthenticationRepository, UserRepository}
import it.unibo.dcs.service.webapp.usecases.Results.Implicits._
import it.unibo.dcs.service.webapp.usecases.{LoginUserUseCase, LogoutUserUseCase, RegisterUserUseCase}
import it.unibo.dcs.service.webapp.verticles.requesthandler.ServiceRequestHandler

import scala.language.postfixOps

final class ServiceRequestHandlerImpl(private val userRepository: UserRepository,
                                      private val authRepository: AuthenticationRepository)
  extends ServiceRequestHandler {

  override def handleRegistration(context: RoutingContext)(implicit ctx: Context): Unit = {
    context.getBodyAsJson().fold(responseNotAcceptable(context, "Missing registration information")) {
      val useCase = RegisterUserUseCase create(authRepository, userRepository)
      useCase(_) subscribe (result => context.response() end result)
    }
  }

  private def responseNotAcceptable(context: RoutingContext, response: String): Unit = {
    context.response().setStatusCode(HttpResponseStatus.NOT_ACCEPTABLE.code)
      .end(response)
  }

  override def handleLogout(context: RoutingContext)(implicit ctx: Context): Unit = {
    context.getBodyAsJson().fold(responseNotAcceptable(context, "Missing logout information")) {
      val useCase = LogoutUserUseCase.create(authRepository)
      useCase(_) subscribe (_ => context response() end)
    }
  }

  override def handleLogin(context: RoutingContext)(implicit ctx: Context): Unit = {
    context.getBodyAsJson().fold(responseNotAcceptable(context, "Missing logout information")) {
      val useCase = LoginUserUseCase.create(authRepository, userRepository)
      useCase(_) subscribe (result => context response() end result)
    }
  }

}
