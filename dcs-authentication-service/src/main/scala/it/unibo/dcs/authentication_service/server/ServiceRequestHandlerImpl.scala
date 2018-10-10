package it.unibo.dcs.authentication_service.server

import io.vertx.lang.scala.json.Json
import io.vertx.scala.ext.web.RoutingContext
import it.unibo.dcs.authentication_service.interactor.{LoginUserUseCase, LogoutUserUseCase, RegisterUserUseCase}
import it.unibo.dcs.authentication_service.request.{LoginUserRequest, LogoutUserRequest, RegisterUserRequest}
import it.unibo.dcs.commons.VertxWebHelper._
import rx.lang.scala.Subscriber

class ServiceRequestHandlerImpl(loginUserUseCase: LoginUserUseCase, logoutUserUseCase: LogoutUserUseCase,
                                registerUserUseCase: RegisterUserUseCase) extends ServiceRequestHandler {

  override def handleRegistration(implicit context: RoutingContext): Unit = {
    val credentials = getCredentials
    doIfValidCredentials(credentials,
      registerUserUseCase(RegisterUserRequest(credentials._1.get, credentials._2.get))
        .subscribe(new TokenSubscriber("Username already taken")))
  }

  override def handleLogin(implicit context: RoutingContext): Unit =  {
    val credentials = getCredentials
    doIfValidCredentials(credentials,
      loginUserUseCase(LoginUserRequest(credentials._1.get, credentials._2.get))
        .subscribe(new TokenSubscriber("Wrong username or password")))
  }

  override def handleLogout(implicit context: RoutingContext): Unit = {
    val token = getTokenFromHeader
    doIfDefined(token, logoutUserUseCase(LogoutUserRequest(token.get)).subscribe(new LogoutSubscriber))
  }

  private def getUsername(implicit context: RoutingContext): Option[String] = getJsonBodyData("username")

  private def getPassword(implicit context: RoutingContext): Option[String] = getJsonBodyData("password")

  private def getCredentials(implicit context: RoutingContext): (Option[String], Option[String]) =
    (getUsername, getPassword)

  private def doIfValidCredentials(credentials: (Option[String], Option[String]), action: => Unit)
                                  (implicit routingContext: RoutingContext): Unit = {
    if (credentials._1.isDefined && credentials._2.isDefined) {
      action
    } else respond(401, "Credentials not present")
  }

  private class TokenSubscriber(errorMessage: String)(implicit routingContext: RoutingContext)
    extends Subscriber[String] {
    override def onNext(token: String): Unit = respondWithToken(token, getUsername(routingContext).get)

    override def onError(error: Throwable): Unit = {
      error.printStackTrace()
      respond(401, errorMessage)
    }

    private def respondWithToken(token: String, username: String)(implicit context: RoutingContext): Unit = {
      context.response
        .putHeader("Authorization", "Bearer " + token)
        .putHeader("content-type", "application/json")
        .setStatusCode(201)
        .end(Json.obj(("username", username)).encodePrettily())
    }
  }

  private class LogoutSubscriber(implicit routingContext: RoutingContext) extends Subscriber[Unit] {
    override def onNext(unit: Unit): Unit = respondWithCode(200)

    override def onError(error: Throwable): Unit =
      respond(400, "invalid token or user not logged in")
  }
}
