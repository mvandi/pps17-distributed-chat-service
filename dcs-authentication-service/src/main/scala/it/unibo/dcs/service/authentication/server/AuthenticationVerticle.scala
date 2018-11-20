package it.unibo.dcs.service.authentication.server

import io.vertx.core
import io.vertx.core.{AbstractVerticle, Context}
import io.vertx.scala.ext.auth.jwt.JWTAuth
import io.vertx.scala.ext.web.Router
import io.vertx.scala.ext.web.handler.BodyHandler
import it.unibo.dcs.commons.RxHelper
import it.unibo.dcs.commons.VertxWebHelper.Implicits.contentTypeToString
import it.unibo.dcs.commons.VertxWebHelper._
import it.unibo.dcs.commons.interactor.ThreadExecutorExecutionContext
import it.unibo.dcs.commons.interactor.executor.PostExecutionThread
import it.unibo.dcs.commons.service.{HttpEndpointPublisher, ServiceVerticle}
import it.unibo.dcs.service.authentication.repository.AuthenticationRepository
import it.unibo.dcs.service.authentication.server.containers._
import it.unibo.dcs.service.authentication.server.handlers.{ServiceRequestHandler, ServiceRequestHandlerImpl}
import it.unibo.dcs.service.authentication.server.setupHelpers._
import org.apache.http.entity.ContentType

/** Verticle that runs the Authentication Service */
final class AuthenticationVerticle(private[this] val authenticationRepository: AuthenticationRepository,
                                   private[this] val publisher: HttpEndpointPublisher) extends ServiceVerticle {

  @SuppressWarnings(Array("org.wartremover.warts.Var", "org.wartremover.warts.Null"))
  private var host: String = _
  @SuppressWarnings(Array("org.wartremover.warts.Var", "org.wartremover.warts.Null"))
  private var port: Int = _

  override protected def initializeRouter(router: Router): Unit = {
    router.route().handler(BodyHandler.create())
    setupCors(router)
    val authProvider = JWTAuth.create(vertx, createJwtAuthOptions())
    setupProtectedRoutes(router, authProvider, vertx, authenticationRepository)
    setupRoutes(router, authProvider)
  }

  override def init(jVertx: core.Vertx, context: Context, verticle: AbstractVerticle): Unit = {
    super.init(jVertx, context, verticle)
    host = config getString "host"
    port = config getInteger "port"
  }

  override def start(): Unit = startHttpServer(host, port)
    .doOnCompleted(
      publisher.publish(name = "authentication-service", host = host, port = port)
        .subscribe(record => log.info(s"${record.getName} record published!"),
          log.error(s"Could not publish record", _)))
    .subscribe(server => log.info(s"Server started at http://$host:${server.actualPort}"),
      log.error(s"Could not start server at http://$host:$port", _))

  private def setupRoutes(router: Router, jwtAuth: JWTAuth): Unit = {
    val requestHandler = getRequestHandler(jwtAuth)

    router.post("/register")
      .consumes(ContentType.APPLICATION_JSON)
      .produces(ContentType.APPLICATION_JSON)
      .handler(requestHandler.handleRegistration(_))

    router.post("/login")
      .consumes(ContentType.APPLICATION_JSON)
      .produces(ContentType.APPLICATION_JSON)
      .handler(requestHandler.handleLogin(_))

    router.delete("/protected/logout")
      .produces(ContentType.APPLICATION_JSON)
      .handler(requestHandler.handleLogout(_))

    router.get("/protected/verify/:username")
      .produces(ContentType.APPLICATION_JSON)
      .handler(requestHandler.handleTokenCheck(_))

    router.delete("/protected/users/:username")
      .produces(ContentType.APPLICATION_JSON)
      .handler(requestHandler.handleUserDeletion(_))
  }

  private def getRequestHandler(jwtAuth: JWTAuth): ServiceRequestHandler = {
    val threadExecutor = ThreadExecutorExecutionContext(vertx)
    val postExecutionThread = PostExecutionThread(RxHelper.scheduler(this.ctx))

    val validations = createValidations(threadExecutor, postExecutionThread, authenticationRepository)
    val useCases = createUseCases(threadExecutor, postExecutionThread, authenticationRepository, jwtAuth, validations)
    ServiceRequestHandlerImpl(useCases, validations)
  }

}

object AuthenticationVerticle {
  def apply(authenticationRepository: AuthenticationRepository,
            publisher: HttpEndpointPublisher):AuthenticationVerticle =
    new AuthenticationVerticle(authenticationRepository, publisher)
}
