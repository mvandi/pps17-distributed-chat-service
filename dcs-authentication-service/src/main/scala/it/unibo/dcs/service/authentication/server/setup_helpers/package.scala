package it.unibo.dcs.service.authentication.server

import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.lang.scala.json.Json
import io.vertx.scala.core.Vertx
import io.vertx.scala.ext.auth.jwt.{JWTAuth, JWTAuthOptions}
import io.vertx.scala.ext.web.handler.JWTAuthHandler
import io.vertx.scala.ext.web.{Router, RoutingContext}
import it.unibo.dcs.commons.VertxWebHelper.{getTokenFromHeader, respond}
import it.unibo.dcs.service.authentication.repository.AuthenticationRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.io.Source
import scala.util.Success

package object setupHelpers {

  def createJwtAuthOptions(): JWTAuthOptions = {
    val keyStoreSecret = Source.fromResource("keystore-secret.txt").getLines().next()
    val keyStoreOptions = Json.obj(("type", "jceks"), ("path", "keystore.jceks"), ("password", keyStoreSecret))
    JWTAuthOptions.fromJson(Json.obj(("keyStore", keyStoreOptions)))
  }

  def setupProtectedRoutes(router: Router, jwtAuth: JWTAuth, vertx: Vertx,
                                   authenticationRepository: AuthenticationRepository): Unit = {
    val jwtAuthHandler = JWTAuthHandler.create(jwtAuth)
    val protectedRouter = Router.router(vertx)
    protectedRouter.route("/*").handler(checkTokenValidity(jwtAuthHandler, jwtAuth, authenticationRepository)(_))
    router.mountSubRouter("/protected", protectedRouter)
  }

  private def checkTokenValidity(jwtAuthHandler: JWTAuthHandler, jwtAuth: JWTAuth,
                                 authenticationRepository: AuthenticationRepository)
                                (implicit context: RoutingContext): Unit = {
    val token = getTokenFromHeader
    token.fold[Unit](() => returnError())(tokenValue => {
      isTokenValid(jwtAuth, tokenValue).onComplete {
        case Success(true) => checkTokenValidityInDb(tokenValue, authenticationRepository)
        case _ => returnError()
      }})

    def isTokenValid(jwtAuth: JWTAuth, token: String): Future[Boolean] =
      jwtAuth.authenticateFuture(Json.obj(("jwt", token))).map(user => user != null)

    def checkTokenValidityInDb(token: String, authenticationRepository: AuthenticationRepository)
                              (implicit context: RoutingContext): Unit =
      authenticationRepository.isTokenValid(token).subscribe(tokenValid => if (tokenValid) {
        context.next()
      } else {
        returnError()
      }, _ => returnError())

    def returnError(): Unit =  respond(HttpResponseStatus.UNAUTHORIZED)
  }

}
